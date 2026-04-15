package com.deadline;

import javax.swing.*;
import java.awt.*;

public class SurvivorRankingUI extends JPanel {

    public SurvivorRankingUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(15, 15, 15));

        add(createHeader(), BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(createPodiumSection(), BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(createListSection());
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(100, 0, 0);
                this.trackColor = new Color(20, 20, 20);
            }
        });
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
    }

    // ===== HEADER =====
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(25, 0, 0));
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JButton backBtn = new JButton("← BACK") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(200, 0, 0) : new Color(120, 0, 0));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        backBtn.setForeground(Color.WHITE);
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backBtn.setFocusPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setPreferredSize(new Dimension(100, 35));
        backBtn.addActionListener(e -> Main.switchPage(Main.DASHBOARD));

        JLabel title = new JLabel("TOP SURVIVORS", SwingConstants.CENTER);
        title.setForeground(new Color(255, 50, 50));
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));

        header.add(backBtn, BorderLayout.WEST);
        header.add(title, BorderLayout.CENTER);

        return header;
    }

    // ===== PODIUM =====
    private JPanel createPodiumSection() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 0));
        panel.setBackground(new Color(20, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 20, 50));

        panel.add(createPodiumCard("2ND", "Donita", "1,320", 130, new Color(180, 180, 180)));
        panel.add(createPodiumCard("1ST", "David", "2,450", 170, new Color(255, 215, 0)));
        panel.add(createPodiumCard("3RD", "Steven", "953", 110, new Color(205, 127, 50)));

        return panel;
    }

    private JPanel createPodiumCard(String rank, String name, String score, int height, Color accent) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);

        JLabel nameLbl = new JLabel(name);
        nameLbl.setForeground(Color.WHITE);
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel scoreLbl = new JLabel(score + " PTS");
        scoreLbl.setForeground(accent);
        scoreLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        scoreLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel box = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(100, 0, 0), 0, getHeight(), new Color(40, 0, 0));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.setColor(accent);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 15, 15);
                g2.dispose();
            }
        };
        box.setPreferredSize(new Dimension(80, height));
        box.setMaximumSize(new Dimension(80, height));
        box.setOpaque(false);

        JLabel rankLbl = new JLabel(rank);
        rankLbl.setForeground(Color.WHITE);
        rankLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        box.add(rankLbl);

        card.add(nameLbl);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(scoreLbl);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(box);

        return card;
    }

    // ===== LIST =====
    private JPanel createListSection() {
        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(new Color(15, 15, 15));
        list.setBorder(BorderFactory.createEmptyBorder(10, 30, 30, 30));

        list.add(createRow("4", "Hendry", "690"));
        list.add(createRow("5", "Britney", "889"));
        list.add(createRow("6", "Andreas", "800"));
        list.add(createRow("7", "Renaldy", "780"));

        return list;
    }

    private JPanel createRow(String rank, String name, String score) {
        JPanel row = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 30, 30));
                g2.fillRoundRect(0, 2, getWidth(), getHeight() - 4, 10, 10);
                g2.dispose();
            }
        };
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(0, 50));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        row.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        JLabel left = new JLabel("#" + rank + "  " + name);
        left.setForeground(Color.WHITE);
        left.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JLabel right = new JLabel(score + " PTS");
        right.setForeground(new Color(255, 100, 100));
        right.setFont(new Font("Segoe UI", Font.BOLD, 14));

        row.add(left, BorderLayout.WEST);
        row.add(right, BorderLayout.EAST);

        return row;
    }
}
