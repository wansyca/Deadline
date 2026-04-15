package com.deadline;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.Timer;

public class GamePanel extends JPanel implements ActionListener, KeyListener {

    // =========================
    // SCREEN
    // =========================
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    // =========================
    // WORLD (MAP SUPER LUAS)
    // =========================
    private static final int WORLD_WIDTH = 4000;
    private static final int WORLD_HEIGHT = 3000;

    private static final int FPS = 60;

    // =========================
    // CAMERA
    // =========================
    private int camX = 0;
    private int camY = 0;

    // =========================
    // GAME OBJECT
    // =========================
    private Timer timer;
    private Player player;
    private List<SubmissionDesk> submissionDesks;
    private List<Lecturer> lecturers;
    private List<Assignment> assignments;
    private List<Rectangle> obstacles;

    private Random random = new Random();
    private boolean isGameOver = false;
    private boolean isGameWon = false;

    // 🔥 TIMER & BOOKS
    private int timeLeft = 120 * FPS;
    private int totalSubmitted = 0;
    private final int MAX_BOOKS = 10;

    // Movement
    private boolean up, down, left, right;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addKeyListener(this);

        initGame();

        timer = new Timer(1000 / FPS, this);
        timer.start();

        System.out.println("GAME PANEL 4000x3000 FIX VERSION KELOAD ✅");
    }

    public void resetGame(String playerName, String avatarPath) {
        // Reset state
        isGameOver = false;
        isGameWon = false;
        timeLeft = 120 * FPS;
        totalSubmitted = 0;
        
        up = false; down = false; left = false; right = false;

        // Reset entities
        player = new Player(WORLD_WIDTH / 2, WORLD_HEIGHT / 2);
        player.setName(playerName);
        player.setAvatar(avatarPath);

        lecturers.clear();
        assignments.clear();
        submissionDesks = new ArrayList<>();

        initObstacles();
        spawnLecturer();
        spawnLecturer(); 

        for (int i = 0; i < 15; i++) {
            spawnAssignment();
        }
    }

    // =========================
    // INIT GAME
    // =========================
    private void initGame() {
        player = new Player(WORLD_WIDTH / 2, WORLD_HEIGHT / 2);
        player.setAvatar("/assets/Avatar_1_cowo.png");

        lecturers = new ArrayList<>();
        assignments = new ArrayList<>();
        submissionDesks = new ArrayList<>();

        initObstacles();

        spawnLecturer();
        spawnLecturer();

        for (int i = 0; i < 15; i++) {
            spawnAssignment();
        }
    }

    // =========================
    // MAP (TIKTOK STYLE -> CLASSROOM)
    // =========================
    private void initObstacles() {
        obstacles = new ArrayList<>();

        int wallThin = 40;
        // Tembok luar
        obstacles.add(new Rectangle(0, 0, WORLD_WIDTH, wallThin));
        obstacles.add(new Rectangle(0, WORLD_HEIGHT - wallThin, WORLD_WIDTH, wallThin));
        obstacles.add(new Rectangle(0, 0, wallThin, WORLD_HEIGHT));
        obstacles.add(new Rectangle(WORLD_WIDTH - wallThin, 0, wallThin, WORLD_HEIGHT));

        // Layout Kelas yang lebih luas (Grup-grup meja)
        int mejaWidth = 120;
        int mejaHeight = 80;
        
        // Buat 4 area kelas besar
        for (int areaY = 0; areaY < 2; areaY++) {
            for (int areaX = 0; areaX < 2; areaX++) {
                int offsetX = 400 + (areaX * 1800);
                int offsetY = 400 + (areaY * 1300);
                
                for (int baris = 0; baris < 4; baris++) {
                    for (int kolom = 0; kolom < 4; kolom++) {
                        if (kolom == 2) continue; // Lorong antar meja
                        obstacles.add(new Rectangle(offsetX + (kolom * 260), offsetY + (baris * 180), mejaWidth, mejaHeight));
                    }
                }
            }
        }

        // Meja Dosen Besar di beberapa titik (sebagai titik referensi & submission)
        Rectangle d1 = new Rectangle(WORLD_WIDTH / 2 - 150, 100, 300, 100);
        Rectangle d2 = new Rectangle(WORLD_WIDTH / 2 - 150, WORLD_HEIGHT - 200, 300, 100);
        obstacles.add(d1);
        obstacles.add(d2);

        submissionDesks.add(new SubmissionDesk(d1.x, d1.y));
        submissionDesks.add(new SubmissionDesk(d2.x, d2.y));
    }

    private void spawnAssignment() {
        Assignment a;
        boolean overlap;
        do {
            overlap = false;
            a = new Assignment(
                    random.nextInt(WORLD_WIDTH - 100) + 50,
                    random.nextInt(WORLD_HEIGHT - 100) + 50);
            
            // Verifikasi agar tidak spawn di dalam atau nempel dengan halangan
            Rectangle areaCek = new Rectangle(a.getX() - 15, a.getY() - 15, a.getWidth() + 30, a.getHeight() + 30);
            for (Rectangle r : obstacles) {
                if (areaCek.intersects(r)) {
                    overlap = true;
                    break;
                }
            }
        } while (overlap);
        assignments.add(a);
    }

    private void spawnLecturer() {
        lecturers.add(new Lecturer(
                random.nextInt(WORLD_WIDTH),
                random.nextInt(WORLD_HEIGHT),
                2.0 + random.nextDouble() * 0.8)); // Kecepatan di-nerf agak wajar
    }

    // =========================
    // GAME LOOP
    // =========================
    @Override
    public void actionPerformed(ActionEvent e) {
        updateGame();
        repaint();
    }

    private void updateGame() {
        if (isGameOver || isGameWon) {
            return; // Hentikan game loop jika End Game
        }

        // Hitung mundur waktu
        timeLeft--;
        if (timeLeft <= 0) {
            isGameOver = true;
        }

        // Movement
        int dx = 0, dy = 0;
        if (up)
            dy--;
        if (down)
            dy++;
        if (left)
            dx--;
        if (right)
            dx++;

        player.setDirection(dx, dy);
        player.update();

        // Collision
        for (Rectangle rect : obstacles) {
            if (player.getBounds().intersects(rect)) {
                player.rollback();
                break;
            }
        }

        // Batas map
        player.setX(Math.max(0, Math.min(player.getX(), WORLD_WIDTH - player.getWidth())));
        player.setY(Math.max(0, Math.min(player.getY(), WORLD_HEIGHT - player.getHeight())));

        // Dosen AI
        for (Lecturer l : lecturers) {
            l.updateAI(player);

            if (l.intersects(player)) {
                System.out.println("KETANGKAP DOSEN 💀");
                isGameOver = true;
            }
        }

        // Ambil tugas (Tahan di player, Maks 3)
        for (int i = 0; i < assignments.size(); i++) {
            Assignment a = assignments.get(i);
            if (player.intersects(a) && player.canCarryMore()) {
                player.collectAssignment();
                assignments.remove(i);
                
                // Spawn buku baru supaya di map tetap ada buku untuk diambil
                if (totalSubmitted + assignments.size() + player.getCarriedAssignments() < MAX_BOOKS) {
                    spawnAssignment();
                }
                
                i--;
            }
        }

        // Submit tugas ke meja manapun
        for (SubmissionDesk d : submissionDesks) {
            // Beri area submit yang lebih luas (+30 pixel setiap sisi)
            Rectangle submitArea = new Rectangle(d.getX() - 30, d.getY() - 30, d.getWidth() + 60, d.getHeight() + 60);
            if (player.getBounds().intersects(submitArea)) {
                if (player.getCarriedAssignments() > 0) {
                    totalSubmitted += player.getCarriedAssignments();
                    player.resetCarriedAssignments();
                    System.out.println("TUGAS DISUBMIT! TOTAL: " + totalSubmitted);

                    if (totalSubmitted == 3 || totalSubmitted == 6 || totalSubmitted == 8) {
                        spawnLecturer();
                    }

                    if (totalSubmitted >= MAX_BOOKS) {
                        isGameWon = true;
                    }
                }
            }
        }

        // Kamera follow player
        camX = player.getX() - WIDTH / 2;
        camY = player.getY() - HEIGHT / 2;

        camX = Math.max(0, Math.min(camX, WORLD_WIDTH - WIDTH));
        camY = Math.max(0, Math.min(camY, WORLD_HEIGHT - HEIGHT));
    }

    // =========================
    // RENDER
    // =========================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Camera
        g2.translate(-camX, -camY);

        // Background - Lantai Kelas (Bersih & Elegan)
        g2.setColor(new Color(230, 230, 235)); 
        g2.fillRect(0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        
        // Garis ubin lantai yang tipis dan halus
        g2.setColor(new Color(0, 0, 0, 15)); 
        for (int x = 0; x < WORLD_WIDTH; x += 100) {
            g2.drawLine(x, 0, x, WORLD_HEIGHT);
        }
        for (int y = 0; y < WORLD_HEIGHT; y += 100) {
            g2.drawLine(0, y, WORLD_WIDTH, y);
        }

        // Obstacles (Meja & Tembok)
        for (Rectangle rect : obstacles) {
            if (rect.width == WORLD_WIDTH || rect.height == WORLD_HEIGHT) {
                // Tembok Luar
                g2.setColor(new Color(44, 62, 80)); 
                g2.fill(rect);
                g2.setColor(new Color(52, 73, 94));
                g2.draw(rect);
            } else if (rect.width == 300) {
                // Meja Dosen sudah digambar oleh submissionDesks.draw()
            } else {
                // MEJA MAHASISWA (GAMBAR PROSEDURAL - BERSIH & NO JARING)
                // 1. Kaki Meja
                g2.setColor(new Color(80, 50, 40));
                g2.fillRect(rect.x + 5, rect.y + 5, 8, rect.height - 10);
                g2.fillRect(rect.x + rect.width - 13, rect.y + 5, 8, rect.height - 10);
                
                // 2. Permukaan Meja
                g2.setColor(new Color(121, 85, 72));
                g2.fillRoundRect(rect.x, rect.y, rect.width, rect.height - 10, 8, 8);
                
                // 3. Highlight/Shading
                g2.setColor(new Color(255, 255, 255, 30));
                g2.drawRoundRect(rect.x, rect.y, rect.width, rect.height - 10, 8, 8);
            }
        }

        for (SubmissionDesk d : submissionDesks) {
            d.draw(g2);
        }

        for (Assignment a : assignments)
            a.draw(g2);
        for (Lecturer l : lecturers)
            l.draw(g2);
        player.draw(g2);

        // Reset camera
        g2.translate(camX, camY);

        drawUI(g2);
    }

    private void drawUI(Graphics2D g2) {
        // ==========================================
        // 💎 MODERN FLOATING HUD
        // ==========================================
        int panelW = getWidth();
        int panelH = getHeight();
        
        int hudW = 320; // Diperkecil karena Tangan dihapus
        int hudH = 50;
        int hudX = (panelW - hudW) / 2;
        int hudY = 15;

        // Base Glass/Dark Look
        g2.setColor(new Color(30, 30, 35, 220));
        g2.fillRoundRect(hudX, hudY, hudW, hudH, 25, 25);
        g2.setColor(new Color(255, 255, 255, 50));
        g2.drawRoundRect(hudX, hudY, hudW, hudH, 25, 25);

        // Styling teks
        g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        int textY = hudY + 32;

        // 1. INFO BUKU (KIRI)
        g2.setColor(new Color(255, 215, 0)); // Emas
        g2.drawString("Buku: " + totalSubmitted + " / " + MAX_BOOKS, hudX + 25, textY);

        // 2. TIMER (KANAN)
        int secondsLeft = Math.max(0, timeLeft / FPS);
        if (secondsLeft <= 10) g2.setColor(new Color(255, 80, 80)); 
        else g2.setColor(new Color(150, 255, 150)); 

        String timeStr = "⏳ " + secondsLeft + "s";
        int tWidth = g2.getFontMetrics().stringWidth(timeStr);
        g2.drawString(timeStr, hudX + hudW - tWidth - 25, textY);

        // GAME OVER SCREEN
        if (isGameOver) {
            g2.setColor(new Color(0, 0, 0, 180));
            g2.fillRect(0, 0, panelW, panelH);

            g2.setColor(Color.RED);
            g2.setFont(new Font("Arial", Font.BOLD, 72));
            String overText = "GAME OVER";
            int textWidth = g2.getFontMetrics().stringWidth(overText);
            g2.drawString(overText, (panelW - textWidth) / 2, panelH / 2 - 20);
            
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.PLAIN, 24));
            String subText = secondsLeft <= 0 ? "Waktu Habis!" : "Kamu tertangkap dosen!";
            int subWidth = g2.getFontMetrics().stringWidth(subText);
            g2.drawString(subText, (panelW - subWidth) / 2, panelH / 2 + 30);
        }

        // GAME WON SCREEN
        if (isGameWon) {
            g2.setColor(new Color(0, 0, 0, 180));
            g2.fillRect(0, 0, panelW, panelH);

            g2.setColor(new Color(50, 255, 50));
            g2.setFont(new Font("Arial", Font.BOLD, 72));
            String winText = "YOU SURVIVED!";
            int wWidth = g2.getFontMetrics().stringWidth(winText);
            g2.drawString(winText, (panelW - wWidth) / 2, panelH / 2 - 20);
            
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.PLAIN, 24));
            String subText = "Semua tugas berhasil disubmit tepat waktu!";
            int sWidth = g2.getFontMetrics().stringWidth(subText);
            g2.drawString(subText, (panelW - sWidth) / 2, panelH / 2 + 30);
        }
    }

    // =========================
    // INPUT
    // =========================
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                up = true;
                break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                down = true;
                break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                left = true;
                break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                right = true;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                up = false;
                break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                down = false;
                break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                left = false;
                break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                right = false;
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}