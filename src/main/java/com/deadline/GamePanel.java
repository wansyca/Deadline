// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.deadline;

import com.deadline.backend.ScoreService;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;

public class GamePanel extends JPanel implements ActionListener, KeyListener {

    // =========================
    // SCREEN
    // =========================
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    // =========================
    // WORLD (MAP SUPER LUAS - KAMPUS GEDUNG A)
    // =========================
    private static final int WORLD_WIDTH = 8000;
    private static final int WORLD_HEIGHT = 6000;

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
   private List<Map<String, Object>> cachedTopScores = new ArrayList<>();
    private List<Lecturer> lecturers;
    private List<Assignment> assignments;
    private List<Rectangle> obstacles;

    private Random random = new Random();
    private boolean isGameOver = false;

    private Image deskImage;
    private Image dosenTua;
    private Image dosenMuda;
    private Image dosenCewe;


    // 🔥 SURVIVAL MODE
    private int survivalTime = 0;
    private int ticks = 0;
    private int collectedBooks = 0;
    private int totalScore = 0;
    private long gameStartTime;
    
    // BACKEND INTEGRATION
    private int currentPlayerId = -1;

    // Movement
    private boolean up, down, left, right;
    
    // UI Buttons bounds
    private Rectangle btnMenu;
    private Rectangle btnRetry;
    private Rectangle btnExit;
    private Rectangle btnExitGame;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addKeyListener(this);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point p = e.getPoint();
                // 🔥 EXIT SAAT MAIN
                if (!isGameOver && btnExitGame != null && btnExitGame.contains(p)) {
                    UIManager.put("OptionPane.background", new Color(30, 30, 30));
                    UIManager.put("Panel.background", new Color(30, 30, 30));

                    UIManager.put("OptionPane.messageForeground", Color.WHITE);
                    UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.BOLD, 14));

                    UIManager.put("Button.background", new Color(50, 50, 50));
                    UIManager.put("Button.foreground", Color.WHITE);
                    UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 12));

                    JPanel panel = new JPanel();
                    panel.setBackground(new Color(25, 25, 30));
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

                    JLabel title = new JLabel("KONFIRMASI KELUAR");
                    title.setForeground(new Color(255, 80, 80));
                    title.setFont(new Font("Segoe UI", Font.BOLD, 18));
                    title.setAlignmentX(JLabel.CENTER_ALIGNMENT);

                    JLabel msg = new JLabel("<html>Yakin mau keluar?<br>Score kamu tidak akan masuk leaderboard.</html>");
                    msg.setForeground(Color.WHITE);
                    msg.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    msg.setAlignmentX(JLabel.CENTER_ALIGNMENT);

                    panel.add(Box.createVerticalStrut(10));
                    panel.add(title);
                    panel.add(Box.createVerticalStrut(10));
                    panel.add(msg);
                    panel.add(Box.createVerticalStrut(10));

                    int result = JOptionPane.showOptionDialog(
                            GamePanel.this,
                            panel,
                            "EXIT GAME",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            new String[]{"Keluar", "Batal"},
                            "Batal"
                    );

                    if (result == JOptionPane.YES_OPTION) {
                        Main.switchPage(Main.DASHBOARD);
                    }
                }
                if (isGameOver) {
                    if (btnRetry != null && btnRetry.contains(p)) {
                        initGame();
                    } else if (btnMenu != null && btnMenu.contains(p)) {
                        Main.switchPage(Main.DASHBOARD);
                    }
                }
            }
        });


        initGame();

      try {
         this.deskImage = (new ImageIcon(this.getClass().getResource("/assets/meja.png"))).getImage();
         this.dosenTua = (new ImageIcon(this.getClass().getResource("/assets/avatar_3_dosen.png"))).getImage();
         this.dosenMuda = (new ImageIcon(this.getClass().getResource("/assets/d.png"))).getImage();
         this.dosenCewe = (new ImageIcon(this.getClass().getResource("/assets/dc.png"))).getImage();
      } catch (Exception var2) {
         System.err.println("Gagal load assets (meja/dosen) ❌");
      }

      this.timer = new Timer(16, this);
      this.timer.start();
      System.out.println("GAME PANEL 8000x6000 SURVIVAL VERSION KELOAD ✅");
   }

   public void resetGame(int playerId, String playerName, String avatarPath) {
      this.currentPlayerId = playerId;
      this.up = false;
      this.down = false;
      this.left = false;
      this.right = false;
      this.player = new Player(4000, 3000);
      this.player.setName(playerName);
      this.player.setAvatar(avatarPath);
      this.initGame();
   }

   private void initGame() {
      this.isGameOver = false;
      this.survivalTime = 0;
      this.ticks = 0;
      this.collectedBooks = 0;
      this.totalScore = 0;
      this.gameStartTime = System.currentTimeMillis();
      if (this.player == null) {
         this.player = new Player(4000, 3000);
         this.player.setAvatar("/assets/avatar_1_cowo.png");
      }

      this.lecturers = new ArrayList();
      this.assignments = new ArrayList();
      this.initObstacles();
      boolean safe = false;
      int attempts = 0;
      int spawnX = 4000;
      int spawnY = 3000;

      while(!safe && attempts < 100) {
         this.player.setX(spawnX);
         this.player.setY(spawnY);
         safe = true;

         for(Rectangle r : this.obstacles) {
            if (this.player.getBounds().intersects(r)) {
               safe = false;
               spawnX = this.random.nextInt(7500) + 250;
               spawnY = this.random.nextInt(5500) + 250;
               ++attempts;
               break;
            }
         }
      }

      this.player.resetCarriedAssignments();
      this.player.setCollectedBooks(0);
      this.lecturers.clear();

      for(int i = 0; i < 50; ++i) {
         this.spawnAssignment();
      }

   }

   private void spawnLecturer() {
      int books = this.player.getCollectedBooks();
      double speed = 2.0;
      if (books >= 10) {
         speed = 2.0 + (double)(books - 9) * 0.2;
         speed = Math.min(speed, 5.0);
      }

      int rand = this.random.nextInt(3);
      Image selectedImage = (rand == 0) ? this.dosenTua : (rand == 1 ? this.dosenMuda : this.dosenCewe);

      int sx, sy;
      int side = this.random.nextInt(4);
      int padding = 150;

      if (side == 0) { // Top
         sx = this.camX + this.random.nextInt(800);
         sy = this.camY - padding;
      } else if (side == 1) { // Bottom
         sx = this.camX + this.random.nextInt(800);
         sy = this.camY + 600 + padding;
      } else if (side == 2) { // Left
         sx = this.camX - padding;
         sy = this.camY + this.random.nextInt(600);
      } else { // Right
         sx = this.camX + 800 + padding;
         sy = this.camY + this.random.nextInt(600);
      }

      // Constrain to world bounds
      sx = Math.max(0, Math.min(sx, 7840));
      sy = Math.max(0, Math.min(sy, 5840));

      this.lecturers.add(new Lecturer(sx, sy, speed, selectedImage));
   }

   private void initObstacles() {
      this.obstacles = new ArrayList();
      int wallThin = 60;
      this.obstacles.add(new Rectangle(0, 0, 8000, wallThin));
      this.obstacles.add(new Rectangle(0, 6000 - wallThin, 8000, wallThin));
      this.obstacles.add(new Rectangle(0, 0, wallThin, 6000));
      this.obstacles.add(new Rectangle(8000 - wallThin, 0, wallThin, 6000));
      int corridorY = 2750;
      int corridorH = 500;

      for(int x = 0; x < 8000; x += 1000) {
         this.obstacles.add(new Rectangle(x, corridorY, 800, wallThin));
         this.obstacles.add(new Rectangle(x, corridorY + corridorH, 800, wallThin));
      }

      int roomW = 2000;
      int roomH = (6000 - corridorH) / 2;

      for(int row = 0; row < 2; ++row) {
         for(int col = 0; col < 4; ++col) {
            int startX = col * roomW;
            int startY = row == 0 ? 0 : corridorY + corridorH;
            if (col > 0) {
               this.obstacles.add(new Rectangle(startX, startY, wallThin, roomH));
            }

            this.generateRoomDecor(startX + 150, startY + 150, roomW - 300, roomH - 350);
            if ((row + col) % 2 == 0) {
               // Removed SubmissionDesk logic
            }
         }
      }

      for(int x = 500; x < 8000; x += 1500) {
         this.obstacles.add(new Rectangle(x, corridorY + 100, 150, 80));
         this.obstacles.add(new Rectangle(x + 400, corridorY + corridorH - 180, 200, 60));
      }

   }

   private void generateRoomDecor(int x, int y, int w, int h) {
      int mejaWidth = 100;
      int mejaHeight = 80;
      int spacingX = 350;
      int spacingY = 250;

      for(int curY = y + 100; curY < y + h - 100; curY += spacingY) {
         for(int curX = x; curX < x + w - 100; curX += spacingX) {
            this.obstacles.add(new Rectangle(curX, curY, mejaWidth, mejaHeight));
         }
      }

      this.obstacles.add(new Rectangle(x + w / 2 - 100, y, 200, 80));
      this.obstacles.add(new Rectangle(x, y, 80, 150));
      this.obstacles.add(new Rectangle(x + w - 80, y + h - 150, 80, 150));
   }

   private void loadLeaderboardFromDB() {
    com.deadline.backend.ScoreService ss = new com.deadline.backend.ScoreService();
    cachedTopScores = ss.getTopScores(5);
}

    private void updateButtonBounds() {
    int panelW = getWidth();
    int panelH = getHeight();

    int btnY = panelH / 2 + 160;

    btnRetry = new Rectangle(panelW / 2 - 310, btnY, 180, 50);
    btnMenu  = new Rectangle(panelW / 2 - 90, btnY, 180, 50);
    btnExit  = new Rectangle(panelW / 2 + 130, btnY, 180, 50);

    btnExitGame = new Rectangle(panelW - 130, 25, 100, 40);
}

    private void spawnAssignment() {
        Assignment a;
        boolean overlap;
        do {
            overlap = false;
            a = new Assignment(
                    random.nextInt(WORLD_WIDTH - 100) + 50,
                    random.nextInt(WORLD_HEIGHT - 100) + 50);
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
    
    // =========================
    // GAME LOOP
    // =========================
    @Override
    public void actionPerformed(ActionEvent e) {
        updateGame();
        repaint();
    }

   private void updateGame() {
      if (!this.isGameOver) {
         ++this.ticks;
         if (this.ticks % 60 == 0) {
            ++this.survivalTime;
            this.totalScore = this.survivalTime + this.collectedBooks * 10;
         }

         int books = this.player.getCollectedBooks();
         int maxLecturers = 10;
         double currentSpeed = 2.0;
         if (books >= 10) {
            currentSpeed = 2.0 + (double)(books - 9) * 0.2;
            currentSpeed = Math.min(currentSpeed, 5.0);
         }

         while (this.lecturers.size() < books && this.lecturers.size() < maxLecturers) {
            this.spawnLecturer();
         }

         for (Lecturer l : this.lecturers) {
            l.setSpeed(currentSpeed);
         }

         int dx = 0;
         int dy = 0;
         if (this.up) {
            --dy;
         }

         if (this.down) {
            ++dy;
         }

         if (this.left) {
            --dx;
         }

         if (this.right) {
            ++dx;
         }

         this.player.setDirection(dx, dy);
         this.player.update();
         this.player.applyMoveX();
         if (this.player.getX() < 0 || this.player.getX() > 8000 - this.player.getWidth()) {
            this.player.rollbackX();
         }

         for(Rectangle rect : this.obstacles) {
            if (this.player.getBounds().intersects(rect)) {
               this.player.rollbackX();
               break;
            }
         }

         this.player.applyMoveY();
         if (this.player.getY() < 0 || this.player.getY() > 6000 - this.player.getHeight()) {
            this.player.rollbackY();
         }

         for(Rectangle rect : this.obstacles) {
            if (this.player.getBounds().intersects(rect)) {
               this.player.rollbackY();
               break;
            }
         }

         for(Lecturer l : this.lecturers) {
            l.updateAI(this.player, this.lecturers);
            if (l.intersects(this.player)) {
               System.out.println("KETANGKAP DOSEN \ud83d\udc80");
               this.isGameOver = true;
               this.saveFinalScore();
               this.loadLeaderboardFromDB();
            }
         }

         for(int i = 0; i < this.assignments.size(); ++i) {
            Assignment a = (Assignment)this.assignments.get(i);
            if (this.player.intersects(a)) {
               this.player.incrementCollectedBooks();
               this.collectedBooks = this.player.getCollectedBooks();
               this.totalScore = this.survivalTime + this.collectedBooks * 10;
               this.assignments.remove(i);
               // Removed desk submission processing

               this.spawnAssignment();
               --i;
            }
         }

         this.camX = this.player.getX() - 400;
         this.camY = this.player.getY() - 300;
         this.camX = Math.max(0, Math.min(this.camX, 7200));
         this.camY = Math.max(0, Math.min(this.camY, 5400));
      }
   }

   protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D)g;
      g2.translate(-this.camX, -this.camY);
      g2.setColor(new Color(230, 230, 235));
      g2.fillRect(0, 0, 8000, 6000);
      g2.setColor(new Color(0, 0, 0, 15));

      for(int x = 0; x < 8000; x += 100) {
         g2.drawLine(x, 0, x, 6000);
      }

      for(int y = 0; y < 6000; y += 100) {
         g2.drawLine(0, y, 8000, y);
      }

      for(Rectangle rect : this.obstacles) {
         boolean isWall = rect.width > 250 || rect.height > 250;
         if (isWall) {
            g2.setColor(new Color(40, 40, 45));
            g2.fill(rect);
            g2.setColor(new Color(60, 60, 70));
            g2.setStroke(new BasicStroke(2.0F));
            g2.draw(rect);
         } else if (rect.width != 300) {
            g2.setColor(new Color(0, 0, 0, 30));
            g2.fillRoundRect(rect.x + 5, rect.y + 10, rect.width, rect.height, 10, 10);
            if (this.deskImage != null) {
               g2.drawImage(this.deskImage, rect.x, rect.y, rect.width, rect.height, (ImageObserver)null);
            } else {
               g2.setColor(new Color(121, 85, 72));
               g2.fillRoundRect(rect.x, rect.y, rect.width, rect.height - 10, 8, 8);
            }
         }
      }

      // Removed SubmissionDesk drawing

      for(Assignment a : this.assignments) {
         a.draw(g2);
      }

      for(Lecturer l : this.lecturers) {
         l.draw(g2);
      }

      this.player.draw(g2);
      g2.translate(this.camX, this.camY);
      this.drawUI(g2);
   }


   private void drawButton(Graphics2D g2, String text, Rectangle rect, Color bgColor) {
      g2.setColor(new Color(0, 0, 0, 100));
      g2.fillRoundRect(rect.x + 3, rect.y + 3, rect.width, rect.height, 15, 15);
      GradientPaint gp = new GradientPaint((float)rect.x, (float)rect.y, bgColor, (float)rect.x, (float)(rect.y + rect.height), bgColor.darker());
      g2.setPaint(gp);
      g2.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 15, 15);
      g2.setColor(new Color(255, 255, 255, 100));
      g2.setStroke(new BasicStroke(2.0F));
      g2.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 15, 15);
      g2.setFont(new Font("Segoe UI", 1, 18));
      int tw = g2.getFontMetrics().stringWidth(text);
      g2.setColor(Color.WHITE);
      g2.drawString(text, rect.x + (rect.width - tw) / 2, rect.y + 32);
   }

    private void drawUI(Graphics2D g2) {
        int panelW = getWidth();
        int panelH = getHeight();
        updateButtonBounds(); // wajib biar posisi ke-update
        
        boolean danger = collectedBooks >= 10;

        // --- 💎 MODERN GLASS HUD ---
        int hudW = 350;
        int hudH = 100;
        int hudX = 25;
        int hudY = 25;

      g2.setColor(new Color(0, 0, 0, 50));
      g2.fillRoundRect(hudX - 2, hudY - 2, hudW + 4, hudH + 4, 20, 20);
      
      Color hudColor = danger ? new Color(80, 0, 0, 220) : new Color(20, 20, 25, 200);
      g2.setColor(hudColor);
      g2.fillRoundRect(hudX, hudY, hudW, hudH, 20, 20);
      
      g2.setColor(new Color(255, 255, 255, 30));
      g2.fillRoundRect(hudX, hudY, hudW, 35, 20, 20);
      g2.fillRect(hudX, hudY + 20, hudW, 15);
      
      Color borderColor = danger ? new Color(255, 50, 50, 150) : new Color(255, 255, 255, 60);
      g2.setColor(borderColor);
      g2.setStroke(new BasicStroke(danger ? 3.0F : 1.5F));
      g2.drawRoundRect(hudX, hudY, hudW, hudH, 20, 20);

        // Styling teks
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Player Name (Header)
        g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g2.setColor(new Color(180, 200, 255));
        g2.drawString("📍 SURVIVOR: " + player.getName().toUpperCase(), hudX + 20, hudY + 25);

        // Stats
        g2.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 18));
        g2.setColor(Color.WHITE);
        g2.drawString("⏱️ SURVIVED", hudX + 20, hudY + 58);
        
        g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
        g2.setColor(new Color(0, 255, 200));
        g2.drawString(survivalTime + "s", hudX + 150, hudY + 59);

        g2.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 18));
        g2.setColor(Color.WHITE);
        g2.drawString("📚 BOOKS", hudX + 20, hudY + 85);
        
        g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
        g2.setColor(new Color(255, 215, 0));
        g2.drawString(String.valueOf(collectedBooks), hudX + 150, hudY + 86);

        // Score Badge
        int badgeW = 100;
        int badgeH = 50;
        int badgeX = hudX + hudW - badgeW - 15;
        int badgeY = hudY + (hudH - badgeH) / 2 + 5;
        
        g2.setColor(new Color(255, 255, 255, 20));
        g2.fillRoundRect(badgeX, badgeY, badgeW, badgeH, 10, 10);
        g2.setColor(new Color(255, 255, 255, 80));
        g2.drawRoundRect(badgeX, badgeY, badgeW, badgeH, 10, 10);
        
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2.setColor(new Color(200, 200, 200));
        g2.drawString("SCORE", badgeX + (badgeW - g2.getFontMetrics().stringWidth("SCORE"))/2, badgeY + 18);
        
        g2.setFont(new Font("Impact", Font.PLAIN, 24));
        g2.setColor(Color.WHITE);
        String scoreStr = String.valueOf(totalScore);
        g2.drawString(scoreStr, badgeX + (badgeW - g2.getFontMetrics().stringWidth(scoreStr))/2, badgeY + 42);

        // 🔥 Tombol EXIT saat gameplay
        if (!isGameOver) {
            drawButton(g2, "EXIT", btnExitGame, new Color(120, 0, 0));
        }
        
        // --- 💀 GAME OVER SCREEN ---
        if (isGameOver) {
            updateButtonBounds();
            
            // Background Blur-ish Overlay
            g2.setColor(new Color(15, 5, 5, 220));
            g2.fillRect(0, 0, panelW, panelH);

            // Red Vignette
            RadialGradientPaint rgp = new RadialGradientPaint(
                new Point(panelW / 2, panelH / 2),
                panelW,
                new float[]{0.0f, 1.0f},
                new Color[]{new Color(100, 0, 0, 0), new Color(50, 0, 0, 150)}
            );
            g2.setPaint(rgp);
            g2.fillRect(0, 0, panelW, panelH);

            // Text Shadow
            g2.setFont(new Font("Impact", Font.PLAIN, 100));
            String overText = "GAME OVER";
            int textWidth = g2.getFontMetrics().stringWidth(overText);
            g2.setColor(new Color(0, 0, 0, 150));
            g2.drawString(overText, (panelW - textWidth) / 2 + 5, panelH / 2 - 125);
            
            g2.setColor(new Color(255, 50, 50));
            g2.drawString(overText, (panelW - textWidth) / 2, panelH / 2 - 130);
            
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI Light", Font.PLAIN, 28));
            String subText = "Yahh, telat submit tugas";
            int subWidth = g2.getFontMetrics().stringWidth(subText);
            g2.drawString(subText, (panelW - subWidth) / 2, panelH / 2 - 80);
            
            // --- 🏆 EMBEDDED LEADERBOARD ---
            if (cachedTopScores != null) {
                int lbW = 400;
                int lbH = 220;
                int lbX = (panelW - lbW) / 2;
                int lbY = panelH / 2 - 40;

                // Glass Panel
                g2.setColor(new Color(255, 255, 255, 15));
                g2.fillRoundRect(lbX, lbY, lbW, lbH, 15, 15);
                g2.setColor(new Color(255, 255, 255, 40));
                g2.drawRoundRect(lbX, lbY, lbW, lbH, 15, 15);

                g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                g2.setColor(new Color(150, 150, 180));
                g2.drawString("TOP SURVIVORS", lbX + 20, lbY + 30);

                int entryY = lbY + 65;
                for (int i = 0; i < Math.min(cachedTopScores.size(), 5); i++) {
                  Map<String, Object> ps = cachedTopScores.get(i);

                    String name = (String) ps.get("username");
                    int score = (int) ps.get("score");
                    boolean isCurrent = name.equalsIgnoreCase(player.getName()) && score == totalScore;

                    // Rank 1 Glow Effect
                    if (i == 0) {
                        g2.setColor(new Color(255, 215, 0, 40));
                        g2.fillRoundRect(lbX + 10, entryY - 22, lbW - 20, 30, 8, 8);
                    }

               if (isCurrent) {
                  g2.setColor(new Color(0, 255, 150));
                  g2.setFont(new Font("Segoe UI", 1, 18));
               } else {
                  g2.setColor(Color.WHITE);
                  g2.setFont(new Font("Segoe UI", 0, 18));
               }

                    String rankText = "#" + (i + 1);
                    g2.drawString(rankText, lbX + 20, entryY);
                    g2.drawString(name, lbX + 60, entryY);
                    
                    String sText = String.valueOf(score);
                    int sWidth = g2.getFontMetrics().stringWidth(sText);
                    g2.drawString(sText, lbX + lbW - sWidth - 20, entryY);

                    entryY += 35;
                }
            }
            
            int btnY_real = panelH / 2 + 200;
            if (btnRetry != null) btnRetry.y = btnY_real;
            if (btnMenu != null) btnMenu.y = btnY_real;
           
            
            if (btnRetry != null) drawButton(g2, "COBA LAGI", btnRetry, new Color(180, 30, 30));
            if (btnMenu != null) drawButton(g2, "KE MENU", btnMenu, new Color(40, 40, 45));
           
        }
    }

   public void keyPressed(KeyEvent e) {
      switch (e.getKeyCode()) {
         case 10:
         default:
            break;
         case 37:
         case 65:
            this.left = true;
            break;
         case 38:
         case 87:
            this.up = true;
            break;
         case 39:
         case 68:
            this.right = true;
            break;
         case 40:
         case 83:
            this.down = true;
            break;
         case 66:
            if (this.isGameOver) {
               Main.switchPage("DASHBOARD");
            }
            break;
         case 82:
            if (this.isGameOver) {
               this.initGame();
            }
      }

   }

   private void saveFinalScore() {
      int timePlayed = (int)((System.currentTimeMillis() - this.gameStartTime) / 1000L);
      if (this.currentPlayerId != -1) {
         ScoreService scoreService = new ScoreService();
         scoreService.saveScore(this.currentPlayerId, this.totalScore, timePlayed);
      }

      LeaderboardManager.saveScore(this.player.getName(), this.totalScore, timePlayed);
      this.gameStartTime = System.currentTimeMillis();
   }

   public void keyReleased(KeyEvent e) {
      switch (e.getKeyCode()) {
         case 37:
         case 65:
            this.left = false;
            break;
         case 38:
         case 87:
            this.up = false;
            break;
         case 39:
         case 68:
            this.right = false;
            break;
         case 40:
         case 83:
            this.down = false;
      }

   }

   public void keyTyped(KeyEvent e) {
   }
}
