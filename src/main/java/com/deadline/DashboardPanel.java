package com.deadline;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {

    private Image bg;

    public DashboardPanel() {
        setLayout(null);

        bg = new ImageIcon(getClass().getResource("/assets/bg.png")).getImage();

        int centerX = 800 / 2; // Default width 800

        // TITLE
        JLabel title = new JLabel("23.59 — SUBMIT OR DIE", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 48));
        title.setForeground(new Color(255, 50, 50));
        title.setBounds(centerX - 350, 100, 700, 60);
        add(title);

        // START GAME
        JButton start = createButton("START GAME");
        start.setBounds(centerX - 130, 280, 260, 60);
        start.addActionListener(e -> Main.switchPage(Main.INPUT_PLAYER));
        add(start);

        // LEADERBOARD
        JButton leaderboard = createButton("LEADERBOARD");
        leaderboard.setBounds(centerX - 130, 360, 260, 60);
        leaderboard.addActionListener(e -> Main.goToLeaderboardWithLoading());
        add(leaderboard);

        // EXIT
        JButton exit = createButton("EXIT");
        exit.setBounds(centerX - 130, 440, 260, 60);
        exit.addActionListener(e -> System.exit(0));
        add(exit);
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(new Color(100, 0, 0));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(200, 0, 0));
                } else {
                    g2.setColor(new Color(150, 0, 0));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (bg != null) {
            g.drawImage(bg, 0, 0, getWidth(), getHeight(), null);
        }

        // overlay gelap biar aesthetic dikit
        g.setColor(new Color(0, 0, 0, 130));
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}