package mobiarmy.war.Boss.bullet;

import mobiarmy.war.Gun;

/**
 *
 * @author TGDD
 */
public class AddBoomBullet extends Bullet {
    
    public AddBoomBullet(Gun gun, int att, int x, int y, int vx, int vy) {
        super(gun, 34, att, x, y, vx, vy, 0, 0, 80);
        super.isCanCollision = false;
        super.isCanCollisionPlayer = false;
    }
    
    @Override
    public void nextXY() {
        super.nextXY();
        if (super.collect && super.gun.mapData.isCollisionMap(super.bX, super.bY)) {
            super.gun.mapData.bossAdds.add(new int[]{11, 1000, 700, super.bX, super.bY});
        }
    }
    
}
