package mobiarmy.war.Boss.bullet;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import mobiarmy.server.GameData;
import mobiarmy.war.Gun;
import mobiarmy.war.Player;

/**
 *
 * @author Văn Tú
 */
public class MirrorBullet extends Bullet {
    
//    BufferedImage image;
//    int offsetX = 0;
//    int offsetY = 0;
    
    public MirrorBullet(Gun gun, int att, int x, int y, int vx, int vy) {
        super(gun, 49, att, x, y, vx, vy, gun.windX * 40 / 100, gun.windY * 40 / 100, 70);
//        gun.mapData.initImage();
//        image = new BufferedImage(gun.mapData.width, gun.mapData.height * 10, BufferedImage.TYPE_INT_RGB);
//        a();
    }
    
//    private void a() {
//        for (int x = 0; x < image.getWidth(); x++) {
//            for (int y = 0; y < image.getHeight(); y++) {
//                image.setRGB(x, y, 0xFFFFFF);
//            }
//        }
//        offsetY = gun.mapData.height * 9;
//        for (int x = 0; x < gun.mapData.image.getWidth(); x++) {
//            for (int y = 0; y < gun.mapData.image.getHeight(); y++) {
//                // Lấy màu của từng pixel trong ảnh nhỏ
//                int rgb = gun.mapData.image.getRGB(x, y);
//
//                // Chỉ chèn các pixel không phải màu trong suốt (nếu ảnh có alpha channel)
//                if ((rgb >> 24) != 0x00) { // Kiểm tra giá trị alpha
//                    // Đặt pixel tương ứng vào ảnh lớn
//                    image.setRGB(x + offsetX, y + offsetY, rgb);
//                }
//            }
//        }
//
//    }
    
    @Override
    public void nextXY() {
//        Graphics g = image.getGraphics();
//        g.setColor(Color.BLUE);
//        g.drawLine(super.xOld, super.yOld + offsetY, super.bX, super.bY + offsetY);
        super.nextXY();
        if (super.collect) {
            super.frames.get(super.frames.size() - 1).jumpX = 0;
            super.frames.get(super.frames.size() - 1).jumpY = 0;
        } else if(super.vy >= 0) {
//            g.drawLine(super.xOld, super.yOld + offsetY, super.bX, super.bY + offsetY);
            this.frames.add(new Bullet.Frame(this.frame, this.bX, this.bY, this.bX - this.xOld, this.bY - this.yOld)); 
           
            int ang = GameData.getArg(super.lastX - super.frames.get(0).fX, super.frames.get(0).fY - super.lastY);
            int vx0 = super.vx = (gun.force * GameData.cos(ang) >> 10);
            int vy0 = super.vy = (gun.force * GameData.sin(ang) >> 10);
            if (super.vx != 0) {
                while (Math.abs(super.vx) < 15) {
                    super.vx += super.vx;
                    super.vy += super.vy;
                }
            }
            
//            g.setColor(Color.RED);
//            
//            g.drawLine(super.x0, super.y0 + offsetY, super.bX, super.bY + offsetY);
            while(true) {
                if((super.bX < -100) || (super.bX > super.gun.mapData.width + 100) || (super.bY > super.gun.mapData.height + 100)) {
                    break;
                }
                
                int[] collision = super.getCollision(super.xOld, super.yOld, super.bX, super.bY);
                if(collision != null) {
                    super.bX = collision[0];
                    super.bY = collision[1];
                    break;
                } else {
                    this.xOld = this.bX;
                    this.yOld = this.bY;
                    this.lastX = this.bX = this.bX + this.vx;
                    this.lastY = this.bY = this.bY + this.vy;
                }
            }
//            g.drawLine(super.lastX, super.lastY + offsetY, super.bX, super.bY + offsetY);
            super.frames.add(new Bullet.Frame(super.frame, super.bX, super.bY, vx0, -vy0));
            super.collect = true;
            if(super.isCanCollision) {
                super.collision();
            }
//            // Lưu ảnh ra file PNG
//            try {
//                File outputFile = new File("line_image.png");
//                ImageIO.write(image, "png", outputFile);
//            } catch (IOException e) {
//                System.err.println(e.getMessage());
//            }
        }
    }
    
}
