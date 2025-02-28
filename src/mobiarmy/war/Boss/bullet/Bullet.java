package mobiarmy.war.Boss.bullet;

import java.util.ArrayList;
import mobiarmy.server.GameData;
import mobiarmy.war.Gun;
import mobiarmy.war.Player;

/**
 *
 * @author Văn Tú
 */
public class Bullet {
    
    public Gun gun;
    public int bulletId;
    public int att;
    public int x0;
    public int y0;
    public int bX;
    public int bY;
    public int vx;
    public int vy;
    public int vxTemp;
    public int vyTemp;
    public int vyTemp2;
    public boolean isMaxY;
    public int maxYX;
    public int maxY;
    public int ax100;
    public int ay100;
    public int g100;
    public boolean collect;
    public int frame;
    public ArrayList<Frame> frames;
    public int lastX;
    public int lastY;
    public int xOld;
    public int yOld;
    public boolean isCanCollisionPlayer;
    public boolean isCanCollisionMap;
    public boolean isCanCollision;
    public int isPow;
    public int radius;
    public byte isSuper;

    public Bullet(Gun gun, int bulletId, int att, int x, int y, int vx, int vy, int ax100, int ay100, int g100) {
        this.gun = gun;
        this.bulletId = bulletId;
        this.att = att;
        this.x0 = this.lastX = this.xOld = this.bX = x;
        this.y0 = this.lastY = this.yOld = this.bY = y;
        this.vx = vx;
        this.vy = vy;
        this.vxTemp = 0;
        this.vyTemp = 0;
        this.vyTemp2 = 0;
        this.ax100 = ax100;
        this.ay100 = ay100;
        this.g100 = g100;
        this.frames = new ArrayList<>();
        this.isCanCollision = true;
        this.isCanCollisionPlayer = true;
        this.isCanCollisionMap = true;
        if (this.gun.radius != -1) {
            this.radius = this.gun.radius;
        } else {
            this.radius = GameData.radius(bulletId);
        }
        this.isSuper = 0;
    }

    public class Frame {

        public int frame;
        public int fX;
        public int fY;
        public int jumpX;
        public int jumpY;

        public Frame(int frame, int fx, int fy, int jumpX, int jumpY) {
            this.frame = frame;
            this.fX = fx;
            this.fY = fy;
            this.jumpX = jumpX;
            this.jumpY = jumpY;
        }

    }

    public void collision() {
        this.gun.mapData.makeHole(this.bX, this.bY, this.bulletId);
        for (Player player : this.gun.mapData.players) {
            if (player != null && !player.isDie) {
                if (player.isCollision(this.bX, this.bY, this.radius) && player.countInvisible2 == 0) {
                    this.gun.setEff.add(player);
                    player.attBullet(this);
                }
            }
        }
    }

    public void nextXY() {
        this.frame++;
        this.frames.add(new Bullet.Frame(this.frame, this.bX, this.bY, this.bX - this.xOld, this.bY - this.yOld));
        if((this.bX < -100) || (this.bX > this.gun.mapData.width + 100) || (this.bY > this.gun.mapData.height + 100) || this.bY < -9999999) {
            this.collect = true;
        } else {
            this.xOld = this.bX;
            this.yOld = this.bY;
            this.lastX = this.bX = this.bX + this.vx;
            this.lastY = this.bY = this.bY + this.vy;
            //dung map
            int[] collision = this.getCollision(this.xOld, this.yOld, this.bX, this.bY);
            if(collision != null) {
                this.bX = collision[0];
                this.bY = collision[1];
                this.frames.add(new Bullet.Frame(this.frame, this.bX, this.bY, this.bX - this.xOld, this.bY - this.yOld));
                this.collect = true;
                if(this.isCanCollision) {
                    this.collision();
                }
            } else {
                if(this.isMaxY && this.gun.isPow == 0 && (this.bulletId == 0 || this.bulletId == 1 || this.bulletId == 2 || this.bulletId == 9 || this.bulletId == 10 || this.bulletId == 11 || this.bulletId == 19)) {
                    if (Math.abs(this.lastX - this.frames.get(0).fX) > 375) {
                        this.isSuper = this.gun.isSuper = 1;
                    }
                    if (this.bY - this.maxY > 350) {
                        this.isSuper = this.gun.isSuper = 2;
                    }
                }
                this.vxTemp  += Math.abs(this.ax100);
                this.vyTemp  += Math.abs(this.ay100);
                this.vyTemp2 += this.g100;
                if(Math.abs(this.vxTemp) >= 100) {
                    if(this.ax100 > 0) {
                        this.vx += this.vxTemp / 100;
                    } else {
                        this.vx -= this.vxTemp / 100;
                    }
                    this.vxTemp %= 100;
                }
                if(Math.abs(this.vyTemp) >= 100) {
                    if(this.ay100 > 0) {
                        this.vy += this.vyTemp / 100;
                    } else {
                        this.vy -= this.vyTemp / 100;
                    }
                    this.vyTemp %= 100;
                }
                if(Math.abs(this.vyTemp2) >= 100) {
                    this.vy += this.vyTemp2 / 100;
                    this.vyTemp2 %= 100;
                }
                if(this.vy > 0 && !this.isMaxY) {
                    this.isMaxY = true;
                    this.maxYX  = this.bX;
                    this.maxY   = this.bY;
                }
                if (this.gun.mapData.isIntoTornado(this.bX, this.bY)) {
                    this.vy = this.vy - 2;
                }
            }
        }
    }

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
            if((this.isCanCollisionMap && this.gun.mapData.isCollisionMap(X, Y)) || (this.isCanCollisionPlayer && this.gun.mapData.isCollisionPlayer(X, Y))) {
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

