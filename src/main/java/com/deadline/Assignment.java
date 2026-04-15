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
        // Anti-aliasing agar lebih halus
        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Shading bawah (drop shadow tipis)
        g2.setColor(new Color(0, 0, 0, 30));
        g2.fillRoundRect(x + 2, y + 2, width, height, 5, 5);

        // 2. Cover Buku Utama (Biru Donker)
        g2.setColor(new Color(44, 62, 80));
        g2.fillRoundRect(x, y, width, height, 3, 3);

        // 3. Spine (Pinggiran buku - warna lebih terang sedikit)
        g2.setColor(new Color(52, 73, 94));
        g2.fillRect(x, y, 5, height);

        // 4. Tebal Halaman (Kertas putih di sisi berlawanan)
        g2.setColor(new Color(236, 240, 241));
        g2.fillRect(x + width - 4, y + 2, 3, height - 4);

        // 5. Dekorasi sampul (garis emas mewah)
        g2.setColor(new Color(241, 196, 15, 180));
        g2.fillRect(x + 8, y + 5, width - 15, 2);
        g2.fillRect(x + 8, y + 10, width - 18, 2);
    }
}
