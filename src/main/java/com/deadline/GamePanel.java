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
    // WORLD (MAP BESAR)
    // =========================
    private static final int WORLD_WIDTH = 2000;
    private static final int WORLD_HEIGHT = 1500;

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
    private SubmissionDesk desk;
    private List<Lecturer> lecturers;
    private List<Assignment> assignments;
    private List<Rectangle> obstacles;

    private Random random = new Random();

    // Movement
    private boolean up, down, left, right;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addKeyListener(this);

        initGame();

        timer = new Timer(1000 / FPS, this);
        timer.start();

        System.out.println("GAME PANEL CAMERA VERSION KELOAD ✅");
    }

    // =========================
    // INIT GAME
    // =========================
    private void initGame() {
        player = new Player(WORLD_WIDTH / 2, WORLD_HEIGHT / 2);
        player.setAvatar("/assets/Avatar_1_cowo.png");

        desk = new SubmissionDesk(WORLD_WIDTH / 2 - 50, WORLD_HEIGHT - 120);

        lecturers = new ArrayList<>();
        assignments = new ArrayList<>();

        initObstacles();

        spawnLecturer();

        for (int i = 0; i < 5; i++) {
            spawnAssignment();
        }
    }

    // =========================
    // MAP (TIKTOK STYLE)
    // =========================
    private void initObstacles() {
        obstacles = new ArrayList<>();

        // Tembok luar
        obstacles.add(new Rectangle(0, 0, WORLD_WIDTH, 20));
        obstacles.add(new Rectangle(0, WORLD_HEIGHT - 20, WORLD_WIDTH, 20));
        obstacles.add(new Rectangle(0, 0, 20, WORLD_HEIGHT));
        obstacles.add(new Rectangle(WORLD_WIDTH - 20, 0, 20, WORLD_HEIGHT));

        // Koridor tengah
        obstacles.add(new Rectangle(300, 0, 20, WORLD_HEIGHT));
        obstacles.add(new Rectangle(1500, 0, 20, WORLD_HEIGHT));

        // Kelas atas
        for (int i = 0; i < 4; i++) {
            int baseX = 350 + (i * 250);
            for (int j = 0; j < 3; j++) {
                obstacles.add(new Rectangle(baseX, 100 + j * 80, 120, 40));
            }
        }

        // Kelas bawah
        for (int i = 0; i < 4; i++) {
            int baseX = 350 + (i * 250);
            for (int j = 0; j < 3; j++) {
                obstacles.add(new Rectangle(baseX, 900 + j * 80, 120, 40));
            }
        }

        // Ruangan samping
        obstacles.add(new Rectangle(50, 200, 200, 300));
        obstacles.add(new Rectangle(1750, 200, 200, 300));
    }

    private void spawnAssignment() {
        assignments.add(new Assignment(
                random.nextInt(WORLD_WIDTH - 50),
                random.nextInt(WORLD_HEIGHT - 50)));
    }

    private void spawnLecturer() {
        lecturers.add(new Lecturer(
                random.nextInt(WORLD_WIDTH),
                random.nextInt(WORLD_HEIGHT),
                1.5 + random.nextDouble()));
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
            }
        }

        // Ambil tugas
        for (int i = 0; i < assignments.size(); i++) {
            Assignment a = assignments.get(i);
            if (player.intersects(a) && player.canCarryMore()) {
                player.collectAssignment();
                assignments.remove(i);
                i--;
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

        // Background
        g2.setColor(new Color(200, 200, 200));
        g2.fillRect(0, 0, WORLD_WIDTH, WORLD_HEIGHT);

        // Grid biar keliatan gerak
        g2.setColor(Color.LIGHT_GRAY);
        for (int x = 0; x < WORLD_WIDTH; x += 100) {
            for (int y = 0; y < WORLD_HEIGHT; y += 100) {
                g2.drawRect(x, y, 100, 100);
            }
        }

        // Obstacles
        for (Rectangle rect : obstacles) {
            if (rect.width > rect.height) {
                g2.setColor(new Color(139, 69, 19)); // meja
            } else {
                g2.setColor(Color.DARK_GRAY); // tembok
            }
            g2.fill(rect);
        }

        desk.draw(g2);

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
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.drawString("Tugas: " + player.getCarriedAssignments(), 20, 30);
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