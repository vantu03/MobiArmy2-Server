package mobiarmy.war.Boss.bullet;

import mobiarmy.war.Gun;

/**
 *
 * @author Văn Tú
 */
public class ItemTeleportBullet2 extends Bullet {
    
    public ItemTeleportBullet2(Gun gun, int att, int x, int y, int vx, int vy) {
        super(gun, 36, att, x, y, vx, vy, 0, 0, 80);
        super.isCanCollision = false;
        super.isCanCollisionPlayer = false;
    }
    
    @Override
    public void nextXY() {
        super.nextXY();
        if (super.collect && super.gun.mapData.isCollisionMap(super.bX, super.bY)) {
            super.gun.mapData.players[super.gun.index].x = (short) super.bX;
            super.gun.mapData.players[super.gun.index].y = (short) super.bY;
        }
    }
    
}
