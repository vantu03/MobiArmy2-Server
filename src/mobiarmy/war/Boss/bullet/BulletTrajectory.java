package mobiarmy.war.Boss.bullet;

import java.util.ArrayList;
import mobiarmy.war.Gun;
import mobiarmy.war.MapData;
import mobiarmy.war.Player;

/**
 *
 * @author Văn Tú
 */
public class BulletTrajectory extends Gun {
    
    private final int x0;
    private final int y0;
    private final int w0;
    private final int h0;
    public final int tX;
    public final int tY;
    private final int tW;
    private final int tH;
    private final boolean isLand;
    private final boolean isPlayer;
    private final int minAng;
    private final int force0;
    public boolean place;
    public boolean complate;
    public int fAdd = 1;
    public int aAdd = 1;
    
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            calculateTrajectory();
            complate = true;
        }
    };

    public BulletTrajectory(MapData mapData, int index, int bulletId, int x0, int y0, int w0, int h0, int tX, int tY, int tW, int tH, int minAng, int force0, boolean isLand, boolean isPlayer) {
        super(mapData, index, false);
        super.test = true;
        this.x0 = x0;
        this.y0 = y0;
        this.w0 = w0;
        this.h0 = h0;
        this.tX = tX;
        this.tY = tY;
        this.tW = tW;
        this.tH = tH;
        this.minAng = minAng;
        this.force0 = force0;
        this.isLand = isLand;
        this.isPlayer = isPlayer;
        super.bulletId = bulletId;
        mapData.players[index].nTest++;
    }
    
    public void start() {
        this.complate = false;
        new Thread(this.runnable).start();
    }
    
    public void calculateTrajectory() {
        
        this.place = false;
        super.force = this.force0;
        super.force2 = 30;
        super.ang = this.minAng;
        
        while (true) {
            int ang1 = this.minAng, ang2 = 180 - this.minAng, i = 0;
            while (ang1 <= 90 && ang2 >= 90) {
                super.ang = (i % 2 == 0) ? ang1 : ang2;
                if (i % 2 == 0) ang1 += this.aAdd;
                else ang2 -= this.aAdd;

                super.shootBullet(bulletId, this.x0, this.y0, this.w0, this.h0, super.ang, super.force, super.force2, 1, 0, -1);

                for (Bullet bullet : super.bullets) {
                    bullet.isCanCollisionMap = this.isLand;
                    bullet.isCanCollisionPlayer = this.isPlayer;
                }

                boolean hasNext;
                do {
                    hasNext = false;
                    ArrayList<Bullet> bs = new ArrayList<>(super.bullets);

                    for (Bullet bullet : bs) {
                        bullet.isCanCollision = false;
                        if (this.mapData.inRegion(bullet.bX, bullet.bY, this.tX, this.tY, this.tW, this.tH)) {
                            bullet.collect = true;
                            super.force2 = bullet.frame;
                            this.place = true;
                            return;
                        }
                        if (!bullet.collect) {
                            hasNext = true;
                            bullet.nextXY();
                        }
                    }
                } while(hasNext);

                i++;
                if ((super.ang < 90 && super.ang < this.minAng) || (super.ang >= 90 && super.ang > 180 - this.minAng)) {
                    break;
                }
            }
            super.force += this.fAdd;
            if (super.force > 30 || super.force < 1) {
                break;
            }
        }
    }
    
}
