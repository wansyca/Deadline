package com.deadline;

import java.awt.*;
import javax.swing.ImageIcon;

public class Lecturer extends GameObject {

    private double speed;
    private double exactX, exactY;
    private Image sprite;

    // 🔥 animasi ringan
    private int animTick = 0;

    private enum State {
        WANDER, CHASE
    }

    private State state = State.WANDER;

    private double visionRange = 250;

    public Lecturer(int x, int y, double speed) {
        super(x, y, 80, 90);
        this.speed = speed;
        exactX = x;
        exactY = y;

        sprite = new ImageIcon(getClass().getResource("/assets/Avatar_3_dosen.png")).getImage();
    }

    public void updateAI(Player target) {
        double dx = target.getX() - exactX;
        double dy = target.getY() - exactY;

        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance < visionRange)
            state = State.CHASE;
        else
            state = State.WANDER;

        if (state == State.CHASE) {
            double dirX = dx / distance;
            double dirY = dy / distance;

            double boost = 1 + (1 - (distance / visionRange));

            exactX += dirX * speed * boost;
            exactY += dirY * speed * boost;

        } else {
            exactX += Math.sin(animTick * 0.05) * 1.2;
            exactY += Math.cos(animTick * 0.05) * 1.2;
        }

        animTick++;
        x = (int) exactX;
        y = (int) exactY;
    }

    public boolean intersects(Player p) {
        return getBounds().intersects(p.getBounds());
    }

    @Override
    public void update() {
    }

    @Override
    public void draw(Graphics2D g) {

        int offsetY = (int) (Math.sin(animTick * 0.3) * 3);

        g.setColor(new Color(0, 0, 0, 80));
        g.fillOval(x + 20, y + height - 8, 40, 10);

        int shakeX = 0;
        int shakeY = 0;

        if (state == State.CHASE) {
            shakeX = (int) (Math.random() * 4 - 2);
            shakeY = (int) (Math.random() * 4 - 2);

            g.setColor(new Color(255, 0, 0, 40));
            g.fillOval(x, y, width, height);
        }

        g.drawImage(sprite, x + shakeX, y + offsetY + shakeY, width, height, null);
    }
}