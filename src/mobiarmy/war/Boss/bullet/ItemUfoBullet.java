package mobiarmy.war.Boss.bullet;

import mobiarmy.war.Gun;

/**
 *
 * @author TGDD
 */
public class ItemUfoBullet extends Bullet {
    
    private final int hp;
    private final int team;
    
    public ItemUfoBullet(Gun gun, int att, int hp, int team, int x, int y) {
        super(gun, 53, att, x, y, 0, 0, 0, 0, 0);
        this.hp = hp;
        this.team = team;
    }
    
    @Override
    public void nextXY() {
        super.collect = true;
//        super.frames.add(new Bullet.Frame(super.frame, super.bX, super.bY, super.bX - super.xOld, super.bY - super.yOld));
//        if(this.isCanCollision) {
//            this.collision();
//        }
        super.gun.mapData.addBoss(new ItemUfo(this.team, super.gun.index, this.hp, super.att, super.bX, super.bY));
    }
    
}
