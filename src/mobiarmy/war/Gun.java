package mobiarmy.war;

import java.util.ArrayList;
import java.util.HashSet;
import mobiarmy.server.GameData;
import mobiarmy.war.Boss.bullet.*;


/**
 *
 * @author Văn Tú
 */
public class Gun {
    
    public MapData mapData;
    public int index;
    public int typeshoot;
    public int isPow;
    public int bulletId;
    public int gunX;
    public int gunY;
    public int ang;
    public int force;
    public int force2;
    public int nshoot;
    public ArrayList<Bullet> bullets;
    public int att;
    public byte isSuper;
    public HashSet<Player> setEff;
    public int radius;
    public int windX;
    public int windY;
    public boolean test;
    
    public Gun(MapData mapData, int index, boolean isPow) {
        this.mapData = mapData;
        this.index = index;
        this.bullets = new ArrayList<>();
        this.isSuper = 0;
        this.setEff = new HashSet();
        this.isPow = isPow ? 1 : 0;
        this.windX = mapData.windX;
        this.windY = mapData.windY;
        this.test = false;
    }
    
    public void shootBullet(int bulletId, int x, int y, int width, int height, int ang, int force, int force2, int nshoot, int att, int radius) {
        this.bulletId = bulletId;
        this.gunX = x;
        this.gunY = y;
        this.ang = ang;
        this.force = force;
        this.force2 = force2;
        this.nshoot = nshoot;
        this.radius = radius;
        this.bullets.clear();

        // Tính toán ban đầu cho tọa độ và vận tốc đạn
        int bx = this.gunX + ((width - 4) * GameData.cos(ang) >> 10);
        int by = this.gunY - (height / 2) - ((height - 4) * GameData.sin(ang) >> 10);
        int vx = force * GameData.cos(ang) >> 10;
        int vy = -(force * GameData.sin(ang) >> 10);

        for (int num = 0; num < this.nshoot; num++) {
            switch (bulletId) {
                case 0 -> // Gunner
                    this.bullets.add(new Bullet(this, 0, this.isPow == 1 ? att * 2 : att, bx, by, vx, vy, this.windX * 80 / 100, this.windY * 80 / 100, 100));

                case 1 -> {
                    // Miss 6
                    for (int i = 0; i < (this.isPow == 1 ? 5 : 2); i++) {
                        this.bullets.add(new Bullet(this, 1, att / (this.isPow == 1 ? 3 : 2), bx, by, vx, vy, this.windX * 50 / 100, this.windY * 50 / 100, 50));
                    }
                }

                case 2 -> {
                    // Electric
                    int lent = this.isPow == 1 ? 6 : 3;
                    int startAngle = this.ang - (lent / 2) * 5; 
                    for (int i = 0; i < lent; i++) {
                        int currAng = startAngle + i * 5;
                        int bxNew = this.gunX + ((width - 4) * GameData.cos(currAng) >> 10);
                        int byNew = this.gunY - (height / 2) - ((height - 4) * GameData.sin(currAng) >> 10);
                        int vxNew = force * GameData.cos(currAng) >> 10;
                        int vyNew = -(force * GameData.sin(currAng) >> 10);
                        this.bullets.add(new Bullet(this, 2, att / 3, bxNew, byNew, vxNew, vyNew, this.windX * 80 / 100, this.windY * 80 / 100, 60));
                    }
                }

                case 4 -> // Bom b52
                    this.bullets.add(new ItemB52Bullet(this, 100 + (att * 2), bx, by, vx, vy));

                case 5 -> {
                    // Item bay
                    this.mapData.disableLuck = true;
                    this.bullets.add(new ItemTeleportBullet(this, 0, bx, by, vx, vy));
                }

                case 6 -> {
                    // Bom phá đất
                    for (int i = 0; i < 3; i++) {
                        this.bullets.add(new ItemLandBullet(this, att / 3 - i, bx, by, vx, vy));
                    }
                }

                case 7 -> // Lựu đạn
                    this.bullets.add(new Bullet(this, 7, att * 2, bx, by, vx, vy, this.windX * 70 / 100, this.windY * 70 / 100, 80));

                case 8 -> // Tơ nhện
                    this.bullets.add(new ItemSilkBullet(this, att, bx, by, vx, vy));

                case 9 -> {
                    // Kingkong
                    for (int i = this.ang - 6, j = 0; j < 4; i += 4, j++) {
                        int bxNew = this.gunX + ((width - 4) * GameData.cos(i) >> 10);
                        int byNew = this.gunY - (height / 2) - ((height - 4) * GameData.sin(i) >> 10);
                        int vxNew = force * GameData.cos(i) >> 10;
                        int vyNew = -(force * GameData.sin(i) >> 10);
                        this.bullets.add(new Bullet(this, 9, att / 4 * (this.isPow == 1 ? 2 : 1), bxNew, byNew, vxNew, vyNew, this.windX * 40 / 100, this.windY * 40 / 100, 90));
                    }
                }

                case 10 -> {
                    // Rocketer
                    for (int i = 0; i < 3; i++) {
                        this.bullets.add(new Bullet(this, 10, att / 3 * (this.isPow == 1 ? 2 : 1), bx, by, vx, vy, this.windX * 50 / 100, this.windY * 50 / 100, 80));
                    }
                }

                case 11 -> {
                    // Granos
                    for (int i = 0; i < 5; i++) {
                        this.bullets.add(new Bullet(this, 11, att / 5 * (this.isPow == 1 ? 2 : 1), bx, by, vx, vy, this.windX * 30 / 100, this.windY * 30 / 100, 90));
                    }
                }

                case 13 -> {
                    // Voi rồng
                    this.mapData.disableLuck = true;
                    this.bullets.add(new ItemTornadoBullet(this, bx, by, vx, vy));
                }

                case 14 -> // Laser
                    this.bullets.add(new ItemLaserBullet(this, att, bx, by, vx, vy));

                case 16 -> // Đạn trái phá
                    this.bullets.add(new ItemBomBullet(this, att, bx, by, vx, vy));

                case 17 -> // Apache
                    this.bullets.add(new ApacheBullet(this, att / 4 * (this.isPow == 1 ? 2 : 1), bx, by, vx, vy, x, y));

                case 19 -> // Chicky
                    this.bullets.add(new ChickyBullet(this, 19, att / 2 * (this.isPow == 1 ? 2 : 1), bx, by, vx, vy));

                case 21 -> // Tarzan
                    this.bullets.add(new TarzanBullet(this, att * (this.isPow == 1 ? 2 : 1), bx, by, vx, vy));

                case 22 -> // Bom gần chuột
                    this.bullets.add(new ItemMouseBullet(this, 100 + att + (att / 2), bx, this.gunY - (height / 2), vx, vy));

                case 23 -> // Sao băng
                    this.bullets.add(new ItemMeteorBullet(this, att, bx, by, vx, vy));

                case 25 -> // Xuyên đất
                    this.bullets.add(new ItemNoCollisionMap(this, (int) (att * 1.5F), bx, by, vx, vy));

                case 26 -> // Tên lửa
                    this.bullets.add(new ItemRocketBullet(this, att, bx, by, vx, vy));

                case 28 -> // Mưa đạn
                    this.bullets.add(new ItemRainBullet(this, att, bx, by, vx, vy));

                case 30 -> {
                    // Khoáng đất
                    this.mapData.disableLuck = true;
                    this.bullets.add(new ItemEarthHoleBullet(this, this.gunX, this.gunY));
                }

                case 31 -> // Bom to nổ
                    this.bullets.add(new BigBoomBullet(this, att, this.gunX, this.gunY));

                case 32 -> // Bom bé nổ
                    this.bullets.add(new SmallBoomBullet(this, att, this.gunX, this.gunY));
                    
                case 33 -> {
                    //Tên lửa nhỏ
                    int lent = 5;
                    for (int i = 0; i < lent; i++) {
                        this.bullets.add(new Bullet(this, bulletId, att / lent, bx, by, vx, vy, this.windX * 80 / 100, this.windY * 80 / 100, 40));
                    }
                }

                case 34 -> {
                    // Add boom
                    this.mapData.disableLuck = true;
                    this.bullets.add(new AddBoomBullet(this, 0, bx, by, vx, vy));
                }

                case 35 -> // Giẫm đạp
                    this.bullets.add(new BicycleBullet(this, att, this.gunX, this.gunY));

                case 36 -> {
                    // Bay 2
                    this.mapData.disableLuck = true;
                    this.bullets.add(new ItemTeleportBullet2(this, 0, bx, by, vx, vy));
                }

                case 37 -> // Big Rocket
                    this.bullets.add(new BigRocketBullet(this, att, bx, by, force2));

                case 40 -> // Big Laser
                    this.bullets.add(new BigLaserBullet(this, att, bx, by, vx, vy));

                case 42 -> // Big Laser (UFO)
                    this.bullets.add(new UfoLaserBullet(this, att * 2, this.gunX, this.gunY));

                case 49 -> {
                    // Magenta
                    vx = (force + 5) * GameData.cos(ang) >> 10;
                    vy = -((force + 5) * GameData.sin(ang) >> 10);
                    this.bullets.add(new MirrorBullet(this, att * (this.isPow == 1 ? 2 : 1), bx, by, vx, vy));
                }

                case 50 -> // Tự sát
                    this.bullets.add(new ItemSuicideBullet(this, 1500, this.gunX, this.gunY));

                case 51 -> // Bom mù
                    this.bullets.add(new ItemBlindBullet(this, att, bx, by, vx, vy));

                case 52 -> // Khoáng đất 2
                    this.bullets.add(new ItemEarthHole2Bullet(this, 100 + att + (att / 2), bx, by, vx, vy));

                case 53 -> {
                    // UFO
                    this.mapData.disableLuck = true;
                    this.bullets.add(new ItemUfoBullet(this, att, this.mapData.players[this.index].hp, this.mapData.players[this.index].team, 100, 100));
                }

                case 54 -> // Đóng băng
                    this.bullets.add(new ItemFreezeBullet(this, att, bx, by, vx, vy));

                case 55 -> // Khói độc
                    this.bullets.add(new ItemPoisonBullet(this, att, bx, by, vx, vy));

                case 56 -> {
                    // Tơ nhện 2
                    for (int i = this.ang - 5, j = 0; j < 3; i += 5, j++) {
                        int bxNew = this.gunX + ((width - 4) * GameData.cos(i) >> 10);
                        int byNew = this.gunY - (height / 2) - ((height - 4) * GameData.sin(i) >> 10);
                        int vxNew = force * GameData.cos(i) >> 10;
                        int vyNew = -(force * GameData.sin(i) >> 10);
                        this.bullets.add(new ItemSilkBullet2(this, att, bxNew, byNew, vxNew, vyNew));
                    }
                }

                case 57 -> {
                    // Bom hẹn giờ
                    this.mapData.disableLuck = true;
                    this.bullets.add(new ItemTimeBombBullet(this, att * 2, bx, by, vx, vy));
                }

                default -> this.bullets.add(new Bullet(this, bulletId, this.isPow == 1 ? att * 2 : att, bx, by, vx, vy, this.windX * 80 / 100, this.windY * 80 / 100, 100));
            }
        }
    }


    public void fillXY() {
        boolean hasNext;
        do {
            hasNext = false;
            Bullet[] bs = this.bullets.toArray(Bullet[]::new);
            for (Bullet bullet : bs) {
                if (bullet.collect) {
                    continue;
                }
                hasNext = true;
                bullet.nextXY();
            }
        } while(hasNext);
    }
    
}
