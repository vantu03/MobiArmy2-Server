package mobiarmy.war.Boss.bullet;

import mobiarmy.war.Gun;

/**
 *
 * @author Văn Tú
 */
public class ItemRainBullet extends Bullet {
    
    public ItemRainBullet(Gun gun, int att, int x, int y, int vx, int vy) {
        super(gun, 28, att, x, y, vx, vy, 0, 0, 20);
        super.isCanCollision = false;
    }
    
    @Override
    public void nextXY() {
        super.nextXY();
        if (!super.collect && super.isMaxY) {
            super.collect = true;
            super.frames.add(new Bullet.Frame(super.frame, super.bX, super.bY, super.bX - super.xOld, super.bY - super.yOld));
            if (super.isCanCollision) {
                this.collision();
            }
        }
        if (super.collect) {
            super.gun.bullets.add(new Bullet(super.gun, 29, super.att / 3,  super.bX+18, super.bY - 20,  2,  -1, gun.windX * 15 / 100, gun.windY * 15 / 100, 60));
            super.gun.bullets.add(new Bullet(super.gun, 29, super.att / 3,  super.bX-19, super.bY - 20,  -3, -1, gun.windX * 15 / 100, gun.windY * 15 / 100, 60));
            super.gun.bullets.add(new Bullet(super.gun, 29, super.att / 3,  super.bX+16, super.bY - 23,  3,  -2, gun.windX * 15 / 100, gun.windY * 15 / 100, 60));
            super.gun.bullets.add(new Bullet(super.gun, 29, super.att / 3,  super.bX-17, super.bY - 23,  4,  -2, gun.windX * 15 / 100, gun.windY * 15 / 100, 60));
            super.gun.bullets.add(new Bullet(super.gun, 29, super.att / 3,  super.bX+14, super.bY - 26,  3,  -3, gun.windX * 15 / 100, gun.windY * 15 / 100, 60));
            super.gun.bullets.add(new Bullet(super.gun, 29, super.att / 3,  super.bX-15, super.bY - 26,  -4, -3, gun.windX * 15 / 100, gun.windY * 15 / 100, 60));
            super.gun.bullets.add(new Bullet(super.gun, 29, super.att / 3,  super.bX+11, super.bY - 28,  3,  -4, gun.windX * 15 / 100, gun.windY * 15 / 100, 60));
            super.gun.bullets.add(new Bullet(super.gun, 29, super.att / 3,  super.bX-12, super.bY - 28,  -4, -4, gun.windX * 15 / 100, gun.windY * 15 / 100, 60));
            super.gun.bullets.add(new Bullet(super.gun, 29, super.att / 3,  super.bX+8,  super.bY - 30,  2,  -5, gun.windX * 15 / 100, gun.windY * 15 / 100, 60));
            super.gun.bullets.add(new Bullet(super.gun, 29, super.att / 3,  super.bX-9,  super.bY - 30,  -3, -5, gun.windX * 15 / 100, gun.windY * 15 / 100, 60));
            super.gun.bullets.add(new Bullet(super.gun, 29, super.att / 3,  super.bX+5,  super.bY - 31,  1,  -6, gun.windX * 15 / 100, gun.windY * 15 / 100, 60));
            super.gun.bullets.add(new Bullet(super.gun, 29, super.att / 3,  super.bX-6,  super.bY - 31,  -2, -6, gun.windX * 15 / 100, gun.windY * 15 / 100, 60));
            super.gun.bullets.add(new Bullet(super.gun, 29, super.att / 3,  super.bX-2,  super.bY - 31,  1,  -7, gun.windX * 15 / 100, gun.windY * 15 / 100, 60));
        }
    }
    
}
