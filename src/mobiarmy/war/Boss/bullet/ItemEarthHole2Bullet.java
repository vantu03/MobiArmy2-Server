package mobiarmy.war.Boss.bullet;

import mobiarmy.war.Gun;

/**
 *
 * @author TGDD
 */
public class ItemEarthHole2Bullet extends Bullet {
    
    private int nCham;
    
    public ItemEarthHole2Bullet(Gun gun, int att, int x, int y, int vx, int vy) {
        super(gun, 52, att, x, y, vx, vy, gun.windX * 10 / 100, gun.windY * 10 / 100, 100);
        super.isCanCollisionMap = false;
        this.nCham = 0;
    }
    
    @Override
    public void nextXY() {
        if (super.gun.mapData.isCollisionMap(super.bX, super.bY)) {
            this.nCham++;
            if (this.nCham == 10) {
                super.collect = true;
                super.frames.add(new Bullet.Frame(super.frame, super.bX, super.bY, super.bX - super.xOld, super.bY - super.yOld));
                if (super.isCanCollision) {
                    this.collision();
                }
                return;
            }
            if (super.isCanCollision) {
                super.gun.mapData.makeHole(super.bX, super.bY, super.bulletId);
            }
        }
        super.nextXY();
    }
    
}
