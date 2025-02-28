package mobiarmy.war.Boss.bullet;

import mobiarmy.war.Gun;

/**
 *
 * @author Văn Tú
 */
public class BigLaserBullet extends Bullet {
    
    public BigLaserBullet(Gun gun, int att, int x, int y, int vx, int vy) {
        super(gun, 40, att, x, y, vx, vy, gun.windX * 10 / 100, gun.windY * 10 / 100, 50);
    }
    
    @Override
    public void nextXY() {
        super.nextXY();
    }
    
}
