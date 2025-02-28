package mobiarmy.war.Boss.bullet;

import mobiarmy.war.Gun;
import mobiarmy.war.Tornado;

/**
 *
 * @author Văn Tú
 */
public class ItemTornadoBullet extends Bullet {
    
    public ItemTornadoBullet(Gun gun, int x, int y, int vx, int vy) {
        super(gun, 13, 0, x, y, vx, vy, gun.windX * 50 / 100, gun.windY * 50 / 100, 120);
        super.isCanCollision = false;
    }
    
    @Override
    public void nextXY() {
        super.nextXY();
        if(super.collect) {
            super.gun.mapData.tornados.add(new Tornado(super.gun.index, super.bX, super.bY, 3, super.gun.mapData));
        }
    }
    
}
