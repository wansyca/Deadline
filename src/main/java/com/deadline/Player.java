package com.deadline;

import java.awt.*;
import javax.swing.ImageIcon;

public class Player extends GameObject {

    private Image avatar;

    private int prevX, prevY;
    private double velX = 0, velY = 0;
    private int dX, dY;

    private int carriedAssignments = 0;
    private final int MAX_CARRY = 3;

    private int animTick = 0;
    private boolean facingRight = true;

    public Player(int x, int y) {
        super(x, y, 60, 60);
    }

    public void setAvatar(String path) {
        avatar = new ImageIcon(getClass().getResource(path)).getImage();
    }

    public void setDirection(int dx, int dy) {
        dX = dx;

        dY = dy;

        if (dx > 0)
            facingRight = true;

        if (dx < 0)
            facingRight = false;
    }

    @Override
    public void update() {
        prevX = x;
        prevY = y;

        double accel = 0.6;
        double friction = 0.85;

        velX += dX * accel;
        velY += dY * accel;

        double maxSpeed = 4;

        velX = Math.max(-maxSpeed, Math.min(maxSpeed, velX));
        velY = Math.max(-maxSpeed, Math.min(maxSpeed, velY));

        velX *= friction;
        velY *= friction;

        x += velX;
        y += velY;

        if (Math.abs(velX) > 0.1 || Math.abs(velY) > 0.1)
            animTick++;
        else
            animTick = 0;
    }

    public void rollback() {
        x = prevX;
        y = prevY;
    }

    @Override
    public void draw(Graphics2D g) {
        int offsetY = (int) (Math.sin(animTick * 0.4) * 4);
        int offsetX = (int) (Math.cos(animTick * 0.3) * 2);

        // Shadow
        g.setColor(new Color(0, 0, 0, 80));
        g.fillOval(x + 15, y + height - 6, 30, 8);

        if (avatar != null) {
            if (facingRight)
                g.drawImage(avatar, x + offsetX, y + offsetY, width, height, null);
            else
                g.drawImage(avatar, x + width, y + offsetY, -width, height, null);
        }

        if (carriedAssignments > 0) {
            g.setColor(Color.YELLOW);
            g.fillOval(x + 35, y - 10, 20, 20);

            g.setColor(Color.BLACK);
            g.drawString("" + carriedAssignments, x + 42, y + 5);
        }
    }

    public boolean canCarryMore() {
        return carriedAssignments < MAX_CARRY;
    }

    public void collectAssignment() {
        carriedAssignments++;
    }

    public int getCarriedAssignments() {
        return carriedAssignments;
    }

    public void resetCarriedAssignments() {
        carriedAssignments = 0;
    }
}