package mobiarmy.war.Boss.bullet;

import mobiarmy.war.Gun;

/**
 *
 * @author TGDD
 */
public class ItemRocketBullet extends Bullet {
    
    public ItemRocketBullet(Gun gun, int att, int x, int y, int vx, int vy) {
        super(gun, 26, att, x, y, vx, vy, gun.windX * 30 / 100, gun.windY * 30 / 100, 60);
    }
    
    @Override
    public void nextXY() {
        super.nextXY();
        if (super.collect && !super.gun.setEff.isEmpty()) {
            int distance = (int) Math.sqrt(Math.pow(super.bX - super.gun.gunX, 2) + Math.pow(super.bY - super.gun.gunY, 2));
            super.gun.bullets.add(new RocketExpBullet(super.gun, super.att, super.bX, super.bY, super.vx, super.vy, distance));
            super.gun.bullets.add(new RocketExpBullet(super.gun, super.att, super.bX, super.bY, super.vx, super.vy, distance));
        }
    }
    
}
