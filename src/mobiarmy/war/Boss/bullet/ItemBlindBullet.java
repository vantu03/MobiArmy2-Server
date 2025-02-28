package mobiarmy.war.Boss.bullet;

import mobiarmy.war.Gun;

/**
 *
 * @author Văn Tú
 */
public class ItemBlindBullet extends Bullet {
    
    public ItemBlindBullet(Gun gun, int att, int x, int y, int vx, int vy) {
        super(gun, 51, att, x, y, vx, vy, gun.windX * 5 / 100, gun.windY * 5 / 100, 60);
    }
    
    @Override
    public void nextXY() {
        super.nextXY();
    }
    
}
