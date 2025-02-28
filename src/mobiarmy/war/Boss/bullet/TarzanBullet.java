package mobiarmy.war.Boss.bullet;

import mobiarmy.war.Gun;

/**
 *
 * @author TGDD
 */
public class TarzanBullet extends Bullet {

    private byte    quayLai;
    private final boolean addTZ;
    
    public TarzanBullet(Gun gun, int att, int x, int y, int vx, int vy) {
        super(gun, 21, att, x, y, vx, vy, gun.windX * 10 / 100, gun.windY * 10 / 100, 50);
        this.quayLai = -1;
        this.addTZ = (vx <= 0);
    }

    @Override
    public void nextXY() {
        super.nextXY();
        if(this.quayLai == 0) {
            if(this.addTZ) {
                this.vx += 1;
            } else {
                this.vx -=1;
            }
            this.quayLai = 1;
        } else if(this.quayLai == 1) {
            if(this.addTZ) {
                this.vx += 2;
            } else {
                this.vx -= 2;
            }
        } else if(super.isMaxY) {
            this.quayLai = 0;
        }
    }
    
}
