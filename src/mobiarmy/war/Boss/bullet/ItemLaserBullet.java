package mobiarmy.war.Boss.bullet;

import mobiarmy.war.Gun;

/**
 *
 * @author Văn Tú
 */
public class ItemLaserBullet extends Bullet {
    
    public ItemLaserBullet(Gun gun, int att, int x, int y, int vx, int vy) {
        super(gun, 15, att, x, y, vx, vy, gun.windX * 10 / 100, gun.windY * 10 / 100, 50);
        super.isCanCollision = false;
    }
    
    @Override
    public void nextXY() {
        super.nextXY();
        if (super.collect) {
            super.gun.bullets.add(new Bullet(super.gun, 15, 100 + super.att + (super.att / 2), super.bX, super.bY, 0, 0, 0, 0, 0));
        }
    }
    
}
