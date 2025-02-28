package mobiarmy.war.Boss.bullet;

import mobiarmy.war.Gun;

/**
 *
 * @author TGDD
 */
public class ItemNoCollisionMap extends Bullet {
    
    public ItemNoCollisionMap(Gun gun, int att, int x, int y, int vx, int vy) {
        super(gun, 25, att, x, y, vx, vy, 0, 0, -50);
        super.isCanCollisionMap = false;
    }
    
    @Override
    public void nextXY() {
        if (super.isCanCollision) {
            if (super.gun.mapData.isCollisionMap(super.bX, super.bY)) {
                super.gun.mapData.makeHole(super.bX, super.bY, super.bulletId);
            }
        }
        super.nextXY();
        if(!super.collect) {
            if (super.frame == 30) {
                super.collect = true;
                super.frames.add(new Bullet.Frame(super.frame, super.bX, super.bY, super.bX - super.xOld, super.bY - super.yOld));
                if (super.isCanCollision) {
                    this.collision();
                }
            }
        }
    }
    
}
