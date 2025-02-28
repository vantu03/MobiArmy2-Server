package mobiarmy;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import mobiarmy.server.GameData;

public class Test extends JPanel {
    
    public class Player {
        private final int x = 400;
        private final int y = 400;
        
        BufferedImage image;

        public Player() {
            try {
                this.image = ImageIO.read(new File("res/player/conKhiP.png"));
                this.image = this.image.getSubimage(6, 10, 24, 24);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public Player player = new Player();


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Vẽ người chơi
        //g2d.drawRect(player.x - player.w / 2, player.y - player.h, player.w, player.h);
        g2d.drawImage(player.image, player.x - player.image.getWidth() / 2, player.y - player.image.getHeight(), this);
        for (int i = 0; i < 360; i += 10) {
            shoot(g2d, i, 20, player.image.getWidth(), player.image.getHeight());
        }
    }
    
    private void shoot(Graphics2D g2d, int ang, int force, int w, int h) {
        int x = player.x + ((w - 4) * GameData.cos(ang) >> 10);
        int y = player.y -(h / 2) - ((h - 4) * GameData.sin(ang) >> 10);
        int vx = force * GameData.cos(ang) >> 10;
        int vy = -(force * GameData.sin(ang) >> 10);
        drawBullet(g2d, x, y, vx, vy, 60);
    }
    
    private void drawBullet(Graphics2D g2d, int x, int y, int vx, int vy, int g100) {
        int vyTemp2 = 0;
        for (int i = 0; i< 100; i++) {
            g2d.setColor(Color.BLUE);
            g2d.drawLine(x, y, x += vx, y += vy);
            vyTemp2 += g100;
            if(Math.abs(vyTemp2) >= 100) {
                vy += vyTemp2 / 100;
                vyTemp2 %= 100;
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Draw Line and Rectangle");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        Test panel = new Test();

        frame.add(panel);
        frame.setVisible(true);
    }
}
