package mobiarmy.war.Boss.bullet;

import mobiarmy.war.Gun;

/**
 *
 * @author Văn Tú
 */
public class ItemEarthHoleBullet extends Bullet {
    
    public ItemEarthHoleBullet(Gun gun, int x, int y) {
        super(gun, 30, 0, x, y, 0, 0, 0, 0, 0);
    }
    
    @Override
    public void nextXY() {
        super.frame++;
        super.frames.add(new Bullet.Frame(super.frame, super.bX, super.bY, 0, 2));
        super.bY += 2;
        if (super.isCanCollision) {
            if (super.gun.mapData.isCollisionMap(super.bX, super.bY)) {
                super.gun.mapData.makeHole(super.bX, super.bY - 5, super.bulletId);
            }
        }
        if (super.frame == super.gun.force * 2) {
            super.collect = true;
        }
    }
    
}
