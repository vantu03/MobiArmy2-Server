package mobiarmy.war.Boss.bullet;

import mobiarmy.war.Gun;
import mobiarmy.war.Player;

/**
 *
 * @author Văn Tú
 */
public class UfoLaserBullet extends Bullet {
    
    public UfoLaserBullet(Gun gun, int att, int x, int y) {
        super(gun, 42, att, x, y + 20, 0, 50, 0, 0, 50);
    }
    
    @Override
    public void nextXY() {
        super.nextXY();
    }
    
    public boolean isCollisionPlayer(int x, int y) {
        for (Player player : super.gun.mapData.players) {
            if (player != null &&player.isCollision && player.isCollision(x, y) && player.countInvisible2 == 0 && player.typePlayer == 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int[] getCollision(int X1, int Y1, int X2, int Y2) {
        int  Dx = X2 - X1;
        int  Dy = Y2 - Y1;
        byte x_unit  = 0;
        byte y_unit  = 0;
        byte x_unit2 = 0;
        byte y_unit2 = 0;
        if(Dx < 0) {
            x_unit = x_unit2 = -1;
        } else if(Dx > 0) {
            x_unit = x_unit2 = 1;
        }
        if(Dy < 0) {
            y_unit = y_unit2 = -1;
        } else if(Dy > 0) {
            y_unit = y_unit2 = 1;
        }
        int k1 = Math.abs(Dx);
        int k2 = Math.abs(Dy);
        if(k1 > k2) {
            y_unit2 = 0;
        } else {
            k1 = Math.abs(Dy);
            k2 = Math.abs(Dx);
            x_unit2 = 0;
        }
        int k = k1 >> 1;
        int X = X1, Y = Y1;
        for(int i = 0; i <= k1; i++) {
            if((super.isCanCollisionMap && super.gun.mapData.isCollisionMap(X, Y)) || (super.isCanCollisionPlayer && this.isCollisionPlayer(X, Y))) {
                super.bX = X;
                super.bY = Y;
                return new int[]{X, Y};
            }
            k += k2;
            if(k >= k1) {
                k -= k1;
                X += x_unit;
                Y += y_unit;
            } else {
                X += x_unit2;
                Y += y_unit2;
            }
        }
        return null;
    }
    
}
