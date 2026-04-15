package com.deadline;

import javax.swing.*;
import java.awt.*;

public class PlayerCard extends JPanel {

    public PlayerCard(String rank, String name, String score) {
        setLayout(new BorderLayout());
        setBackground(new Color(30, 30, 30));
        setBorder(BorderFactory.createLineBorder(new Color(139, 0, 0), 2));

        JLabel rankLabel = new JLabel("#" + rank, SwingConstants.CENTER);
        rankLabel.setForeground(Color.RED);
        rankLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel nameLabel = new JLabel(name, SwingConstants.CENTER);
        nameLabel.setForeground(Color.WHITE);

        JLabel scoreLabel = new JLabel(score, SwingConstants.CENTER);
        scoreLabel.setForeground(Color.GREEN);

        add(rankLabel, BorderLayout.NORTH);
        add(nameLabel, BorderLayout.CENTER);
        add(scoreLabel, BorderLayout.SOUTH);

        setPreferredSize(new Dimension(200, 80));
        setMaximumSize(new Dimension(200, 80));
        setMinimumSize(new Dimension(200, 80));

    }
}