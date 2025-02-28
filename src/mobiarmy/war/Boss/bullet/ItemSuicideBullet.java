package mobiarmy.war.Boss.bullet;

import mobiarmy.war.Gun;

/**
 *
 * @author Văn Tú
 */
public class ItemSuicideBullet extends Bullet {
    
    public ItemSuicideBullet(Gun gun, int att, int x, int y) {
        super(gun, 50, att, x, y, 0, 0, 0, 0, 0);
    }
    
    @Override
    public void nextXY() {
        super.frame++;
        super.collect = true;
        super.frames.add(new Bullet.Frame(super.frame, super.bX, super.bY, super.bX - super.xOld, super.bY - super.yOld));
        if(this.isCanCollision) {
            this.collision();
        }
    }
    
}
