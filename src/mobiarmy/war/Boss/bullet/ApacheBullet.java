package mobiarmy.war.Boss.bullet;

import mobiarmy.server.GameData;
import mobiarmy.war.Gun;

/**
 *
 * @author Văn Tú
 */
public class ApacheBullet extends Bullet {
    
    private int x00;
    private int y00;
    
    public ApacheBullet(Gun gun, int att, int x, int y, int vx, int vy, int x0, int y0) {
        super(gun, 17, att, x, y, vx, vy, gun.windX * 30 / 100, gun.windY * 30 / 100, 100);
        this.x0 = x0;
        this.y0 = y0;
    }
    
    @Override
    public void nextXY() {
        super.nextXY();
        if (super.frame == super.gun.force2) {
            
            int att2 = super.att;
            super.att = 10;
            if (!super.collect) {
                super.frames.add(new Bullet.Frame(super.frame, super.bX, super.bY, super.bX - super.xOld, super.bY - super.yOld));
                super.collect = true;
                if(super.isCanCollision) {
                    super.collision();
                }
            }
            
            int ang = super.gun.ang + GameData.toArg0_360(GameData.getArg(super.gun.gunX - super.xOld, super.gun.gunY - super.yOld));
            if (super.gun.ang < 90) {
                ang = 180 - ang;
            }
            for (int i = ang - 15, j = 0; j < 3; j++, i += 15) {
                int bx = super.xOld + (20 * GameData.cos(i) >> 10);
                int by = super.yOld - 12 - (20 * GameData.sin(i) >> 10);
                vx = super.gun.force * GameData.cos(i) >> 11;
                vy = -(super.gun.force * GameData.sin(i) >> 11);
                super.gun.bullets.add(new Bullet(super.gun, 18, att2, bx, by, vx, vy, super.ax100, super.ay100, super.g100));
            }
        }
    }
    
}
