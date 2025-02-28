package mobiarmy.war.Boss.bullet;

import mobiarmy.war.Gun;

/**
 *
 * @author TGDD
 */
public class ChickyBullet extends Bullet {
    
    public ChickyBullet(Gun gun, int bulletId, int att, int x, int y, int vx, int vy) {
        super(gun, 19, att, x, y, vx, vy, gun.windX * 20 / 100, gun.windY * 20 / 100, 50);
    }
    
    @Override
    public void nextXY() {
        super.nextXY();
        if (super.frame == super.gun.force2) {
            Bullet bullet = new Bullet(super.gun, 20, super.att, super.lastX, super.lastY + 8, 0, 0, super.ax100 / 2, super.ay100 / 2, 30);
            for (int i = 0; i < super.gun.force / 5; i++) {
                bullet.frames.add(new Bullet.Frame(++bullet.frame, bullet.bX, bullet.bY, bullet.bX - bullet.xOld, bullet.bY - bullet.yOld));
            }
            super.gun.bullets.add(bullet);
            
        }
    }
    
}
