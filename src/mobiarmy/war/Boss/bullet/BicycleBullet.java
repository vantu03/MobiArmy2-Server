package mobiarmy.war.Boss.bullet;

import mobiarmy.war.Gun;
import mobiarmy.war.Player;

/**
 *
 * @author Văn Tú
 */
public class BicycleBullet extends Bullet {
    
    public BicycleBullet(Gun gun, int att, int x, int y) {
        super(gun, 35, att, x, y, 0, 0, 0, 0, 0);
    }
    
    @Override
    public void nextXY() {
        super.collect = true;
        super.frame++;
        super.frames.add(new Bullet.Frame(super.frame, super.bX, super.bY, 0, 0));
        if (super.isCanCollision) {
            super.gun.mapData.makeHole(super.bX, super.bY, super.bulletId);
            for (Player player : super.gun.mapData.players) {
                if (player != null && !player.isDie) {
                    if (player.isCollision(this.bX, this.bY, this.radius) && player.countInvisible2 == 0 && player.typePlayer == 0) {
                        this.gun.setEff.add(player);
                        player.attBullet(this);
                    }
                }
            }
        }
    }
    
}
