package mobiarmy.war.Boss.bullet;

import mobiarmy.war.Gun;

/**
 *
 * @author Văn Tú
 */
public class ItemMeteorBullet extends Bullet {
    
    public ItemMeteorBullet(Gun gun, int att, int x, int y, int vx, int vy) {
        super(gun, 23, att, x, y, vx, vy, gun.windX * 20 / 100, gun.windY * 20 / 100, 100);
        super.isCanCollision = false;
    }
    
    @Override
    public void nextXY() {
        super.nextXY();
        if (super.collect) {
            super.gun.bullets.add(new Bullet(super.gun, 24, super.att, super.bX - 90, super.bY - 187, 0, 0, 0, 0, 50));
            super.gun.bullets.add(new Bullet(super.gun, 24, super.att, super.bX - 60, super.bY - 187, 0, 1, 0, 0, 50));
            super.gun.bullets.add(new Bullet(super.gun, 24, super.att, super.bX - 30, super.bY - 187, 0, 2, 0, 0, 50));
            super.gun.bullets.add(new Bullet(super.gun, 24, super.att, super.bX, super.bY - 187, 0, 3, 0, 0, 50));
            super.gun.bullets.add(new Bullet(super.gun, 24, super.att, super.bX + 30, super.bY - 187, 0, 2, 0, 0, 50));
            super.gun.bullets.add(new Bullet(super.gun, 24, super.att, super.bX + 60, super.bY - 187, 0, 1, 0, 0, 50));
            super.gun.bullets.add(new Bullet(super.gun, 24, super.att, super.bX + 90, super.bY - 187, 0, 0, 0, 0, 50));
        }
    }
    
}
