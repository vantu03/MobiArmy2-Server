package mobiarmy.war.Boss.bullet;

import mobiarmy.war.Gun;

/**
 *
 * @author Văn Tú
 */
public class ItemBomBullet extends Bullet {
    
    public ItemBomBullet(Gun gun, int att, int x, int y, int vx, int vy) {
        super(gun, 7, att, x, y, vx, vy, 0, 0, 100);
        super.isCanCollision = false;
    }
    
    @Override
    public void nextXY() {
        super.nextXY();
        if (super.collect) {
            super.gun.bullets.add(new Bullet(super.gun, 12, super.att, super.bX - 8, super.bY - 493, -1, 2, 0, 0, 100));
            super.gun.bullets.add(new Bullet(super.gun, 12, super.att, super.bX + 12, super.bY - 496, 0, 1, 0, 0, 100));
            super.gun.bullets.add(new Bullet(super.gun, 12, super.att, super.bX - 19, super.bY - 505, -2, 1, 0, 0, 100));
            super.gun.bullets.add(new Bullet(super.gun, 12, super.att, super.bX + 18, super.bY - 505, 1, 1, 0, 0, 100));
            super.gun.bullets.add(new Bullet(super.gun, 12, super.att, super.bX + 20, super.bY - 512, 2, 0, 0, 0, 100));
            super.gun.bullets.add(new Bullet(super.gun, 12, super.att, super.bX - 20, super.bY - 512, -3, 0, 0, 0, 100));
        }
    }
    
}
