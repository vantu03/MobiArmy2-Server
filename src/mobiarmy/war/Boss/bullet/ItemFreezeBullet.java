package mobiarmy.war.Boss.bullet;

import mobiarmy.war.Gun;

/**
 *
 * @author Văn Tú
 */
public class ItemFreezeBullet extends Bullet {
    
    public ItemFreezeBullet(Gun gun, int att, int x, int y, int vx, int vy) {
        super(gun, 54, att, x, y, vx, vy, 0, 0, 80);
    }
    
    @Override
    public void nextXY() {
        super.nextXY();
    }
    
}
