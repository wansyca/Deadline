package com.deadline;

import java.awt.Color;
import java.awt.Graphics2D;

public class Assignment extends GameObject {

    public Assignment(int x, int y) {
        super(x, y, 20, 25);
    }

    @Override
    public void update() {
        // Assignments don't move
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(new Color(255, 215, 0)); // Gold/Yellow color
        g2.fillRect(x, y, width, height);

        // draw book details
        g2.setColor(Color.BLACK);
        g2.drawRect(x, y, width, height);
        g2.drawLine(x + 5, y + 10, x + 35, y + 10);
        g2.drawLine(x + 5, y + 20, x + 35, y + 20);
        g2.drawLine(x + 5, y + 30, x + 35, y + 30);
    }
}
