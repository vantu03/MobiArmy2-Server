package mobiarmy.war.Boss.bullet;

import mobiarmy.war.Gun;

/**
 *
 * @author Văn Tú
 */
public class ItemSilkBullet extends Bullet {
    
    public ItemSilkBullet(Gun gun, int att, int x, int y, int vx, int vy) {
        super(gun, 8, att, x, y, vx, vy, gun.windX * 70 / 100, gun.windY * 70 / 100, 70);
        super.isCanCollision = false;
    }
    
    @Override
    public void nextXY() {
        super.nextXY();
        if (super.collect && !super.gun.test) {
            super.gun.mapData.addSilk(super.bX - 21, super.bY - 20);
        }
    }
    
}