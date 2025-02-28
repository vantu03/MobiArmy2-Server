package mobiarmy.war.Boss.bullet;

import mobiarmy.war.Gun;

/**
 *
 * @author Văn Tú
 */
public class ItemB52Bullet extends Bullet {
    
    public ItemB52Bullet(Gun gun, int att, int x, int y, int vx, int vy) {
        super(gun, 4, att, x, y, vx, vy, 0, 0, 80);
        super.isCanCollision = false;
    }
    
    @Override
    public void nextXY() {
        super.nextXY();
        if (super.collect) {
            super.gun.bullets.add(new BomBullet(super.gun, super.att, super.bX - 140, super.bY - 320, 5, 0, super.bX, super.bY));
        }
    }
    
}
