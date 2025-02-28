package mobiarmy.war.Boss.bullet;

import mobiarmy.war.Gun;

/**
 *
 * @author Văn Tú
 */
public class ItemPoisonBullet extends Bullet {
    
    public ItemPoisonBullet(Gun gun, int att, int x, int y, int vx, int vy) {
        super(gun, 55, att, x, y, vx, vy, gun.windX * 6 / 100, gun.windY * 6 / 100, 60);
    }
    
    @Override
    public void nextXY() {
        super.nextXY();
    }
    
}
