package mobiarmy.war.Boss.bullet;

import mobiarmy.war.Gun;


/**
 *
 * @author Văn Tú
 */
public class BigRocketBullet extends Bullet {
    
    private final int toX;
    
    public BigRocketBullet(Gun gun, int att, int x, int y, int toX) {
        super(gun, 37, att, x, y, 0, 0, 0, 0, 0);
        super.gun.typeshoot = 1;
        this.toX = toX;
    }
    
    @Override
    public void nextXY() {
        super.collect = true;
        super.frame++;
        for (;super.bY > -1000;) {
            super.bY -= 30;
            super.frames.add(new Bullet.Frame(super.frame, super.bX, super.bY, 0, 0));
        }
        super.bX = toX;
        super.frames.add(new Bullet.Frame(super.frame, super.bX, super.bY, 0, 0));
        while(super.bY < super.gun.mapData.height + 100) {
            int[] collision = super.getCollision(super.bX, super.bY, super.bX, super.bY + 30);
            if(collision != null) {
                super.bX = collision[0];
                super.bY = collision[1];
                super.frames.add(new Bullet.Frame(super.frame, super.bX, super.bY, 0, 0));
                if(super.isCanCollision) {
                    super.collision();
                }
                break;
            } else {
                super.bY += 30;
                super.frames.add(new Bullet.Frame(super.frame, super.bX, super.bY, 0, 0));
            }
        }
    }
    
}
