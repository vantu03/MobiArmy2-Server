package mobiarmy.war.Boss.bullet;

import mobiarmy.war.Gun;

/**
 *
 * @author Văn Tú
 */
public class ItemMouseBullet extends Bullet {

    private final int dLeg = 5;

    public ItemMouseBullet(Gun gun, int att, int x, int y, int vx, int vy) {
        super(gun, 22, att, x, y, vx, vy, 0, 0, 10);
        super.vx = super.vx >= 0 ? 4 : -4;
        super.bY -= this.dLeg;
        super.isCanCollisionPlayer = true;
    }
    
    @Override
    public void nextXY() {
        //kiem tra roi
        kiemtraY:
        {
            if(!super.gun.mapData.isCollisionMap(super.bX, super.bY + this.dLeg)) {
                while(super.bY + this.dLeg < super.gun.mapData.height + 200) {
                    int num1 = super.bY;
                    int num2 = num1 + super.g100;
                    int num3 = super.bY;
                    while(num1 < num2) {
                        num1++;
                        if(super.gun.mapData.isCollisionMap(super.bX, num1 + this.dLeg)) {
                            super.bY = num1;
                            super.frames.add(new Bullet.Frame(super.frame, super.bX, super.bY, 0, num1 - num3));
                            break kiemtraY;
                        }
                    }
                    super.bY += super.g100;
                    super.frames.add(new Bullet.Frame(super.frame, super.bX, super.bY, 0, super.g100));
                }
            }
        }
        //kiem tra x
        int num4 = super.bX;
        int num5 = super.bY;
        kiemtraX: {
            super.bX += super.vx;
            if(super.gun.mapData.isCollisionMap(super.bX, (super.bY + this.dLeg) - 5)) {
                super.bX -= super.vx;
                break kiemtraX;
            }
            for(int i = 4; i >= 0; i--) {
                if(super.gun.mapData.isCollisionMap(super.bX, (super.bY + this.dLeg) - i)) {
                    super.bY -= i;
                    break kiemtraX;
                }
            }
        }
        super.frames.add(new Bullet.Frame(super.frame, super.bX, super.bY, super.bX - num4, super.bY - num5));
        if(super.bY + this.dLeg > super.gun.mapData.height + 100) {
            super.collect = true;
            return;
        }
        if (super.frame == super.gun.force * 3) {
            super.collect = true;
            if(super.isCanCollision) {
                super.collision();
            }
        }
        super.frame++;
    }
    
}
