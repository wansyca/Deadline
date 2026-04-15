package com.deadline;

import java.awt.*;
import javax.swing.ImageIcon;

public class Lecturer extends GameObject {

    private double speed;
    private double exactX, exactY;
    private Image sprite;
    private double distanceToTarget = 9999;

    private int animTick = 0;

    private enum State {
        WANDER, CHASE
    }

    private State state = State.WANDER;

    // 🔥 VISION DIPERBESAR SUPAYA SELALU NGEJAR
    private double visionRange = 2500;

    public Lecturer(int x, int y, double speed) {
        // 🔥 DOSEN DIGEDEIN (180x200)
        super(x, y, 180, 200);

        this.speed = speed;
        exactX = x;
        exactY = y;

        sprite = new ImageIcon(getClass().getResource("/assets/Avatar_3_dosen.png")).getImage();
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getSpeed() {
        return this.speed;
    }

    public void updateAI(Player target) {
        double dx = target.getX() - exactX;
        double dy = target.getY() - exactY;

        double distance = Math.sqrt(dx * dx + dy * dy);
        this.distanceToTarget = distance;

        // 🔥 FIX BUG (biar ga NaN)
        if (distance == 0) distance = 0.0001;

        // Selalu chase jika dalam jangkauan map (karena vision range 2500, pasti selalu ngejar)
        if (distance < visionRange)
            state = State.CHASE;
        else
            state = State.WANDER;

        if (state == State.CHASE) {
            double dirX = dx / distance;
            double dirY = dy / distance;

            // 🔥 BOOST KECIL BIAR BISA KABUR
            double boost = 1 + (0.3 * (1 - (distance / visionRange)));

            exactX += dirX * speed * boost;
            exactY += dirY * speed * boost;

        } else {
            // 🔥 GERAK NGELIAR LEBIH HALUS
            exactX += Math.sin(animTick * 0.03) * 1.5;
            exactY += Math.cos(animTick * 0.03) * 1.5;
        }

        animTick++;

        x = (int) exactX;
        y = (int) exactY;
    }

    // 🔥 HITBOX LEBIH KECIL (BIAR FAIR & MUDAH LEWAT MEJA)
    public Rectangle getBounds() {
        // Padding lebih besar agar hitbox tetap ramping di tengah (70-80px)
        return new Rectangle(x + 55, y + 60, width - 110, height - 120);
    }

    public boolean intersects(Player p) {
        return getBounds().intersects(p.getBounds());
    }

    @Override
    public void update() {
        // dikontrol dari GamePanel
    }

    @Override
    public void draw(Graphics2D g) {

        int offsetY = (int) (Math.sin(animTick * 0.3) * 5);

        // 🔥 SHADOW IKUT SIZE
        g.setColor(new Color(0, 0, 0, 70));
        g.fillOval(x + width / 4, y + height - 18, width / 2, 20);

        int shakeX = 0;
        int shakeY = 0;

        // 🔥 MODE NGEJAR + DEKAT = EFEK SEREM
        if (state == State.CHASE && distanceToTarget < 300) {
            shakeX = (int) (Math.random() * 8 - 4);
            shakeY = (int) (Math.random() * 8 - 4);

            g.setColor(new Color(255, 0, 0, 40));
            g.fillOval(x - 30, y - 30, width + 60, height + 60);
        }

        g.drawImage(sprite, x + shakeX, y + offsetY + shakeY, width, height, null);

        // 🔥 DEBUG HITBOX (optional)
        // g.setColor(Color.GREEN);
        // g.draw(getBounds());
    }
}