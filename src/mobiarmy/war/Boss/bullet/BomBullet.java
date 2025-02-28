package mobiarmy.war.Boss.bullet;

import mobiarmy.war.Gun;

/**
 *
 * @author Văn Tú
 */
public class BomBullet extends Bullet {
    
    private final int toX;
    private final int toY;
    
    public BomBullet(Gun gun, int att, int x, int y, int vx, int vy, int toX, int toY) {
        super(gun, 3, att, x, y, vx, vy, 0, 0, 80);
        super.isCanCollisionMap = false;
        super.isCanCollisionPlayer = false;
        this.toX = toX;
        this.toY = toY;
    }
    
    @Override
    public void nextXY() {
        super.nextXY();
        if (super.bY >= this.toY) {
            super.collect = true;
            super.bX = this.toX;
            super.bY = this.toY;
            super.frames.add(new Bullet.Frame(super.frame, super.bX, super.bY, super.bX - super.xOld, super.bY - super.yOld));
            if(this.isCanCollision) {
                this.collision();
            }
        }
    }
    
}
