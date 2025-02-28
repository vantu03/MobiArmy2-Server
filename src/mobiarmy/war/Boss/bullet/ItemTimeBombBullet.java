package mobiarmy.war.Boss.bullet;

import mobiarmy.war.Gun;
import mobiarmy.war.TimeBomb;

/**
 *
 * @author Văn Tú
 */
public class ItemTimeBombBullet extends Bullet {
    
    public ItemTimeBombBullet(Gun gun, int att, int x, int y, int vx, int vy) {
        super(gun, 57, att, x, y, vx, vy, 0, 0, 120);
        this.isCanCollision = false;
    }
    
    @Override
    public void nextXY() {
        super.nextXY();
        if (super.collect) {
            TimeBomb e = new TimeBomb(super.gun.mapData, super.gun.mapData.countTimeBomb++, super.gun.index, super.att, super.bX, super.bY, 5, super.radius);
            super.gun.mapData.timeBombs.add(e);
            super.gun.mapData.timeBomb = e;
        }
    }
    
}
