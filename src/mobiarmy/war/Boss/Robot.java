package mobiarmy.war.Boss;

import mobiarmy.Util;
import mobiarmy.server.GameData;
import mobiarmy.war.Boss.bullet.BulletTrajectory;
import mobiarmy.war.Player;

/**
 *
 * @author Văn Tú
 */
public class Robot extends Boss {

    
    public Robot(int hp, int att, int x, int y) {
        super("Robot", (byte)14, hp, att, 0, 0, 0, 24, 25, x, y);
    }
    
    @Override
    public void update() {
        super.update();
        //Đến lượt chưa
        if (super.index == super.mapData.getTurn() && System.currentTimeMillis() > super.mapData.timeUntilAction2) {
            //Bắn
            if (!super.isShoot) {
                if (super.trajectory == null) {
                    //Tìm đối thủ gần
                    Player player = super.mapData.getPlayerNear(super.index);
                    if (player != null) {
                        if (Math.abs(super.x - player.x) < 40 && Math.abs(super.y - player.y) < 40) {
                            super.mapData.shootBullet(super.index, false, 35, super.x, super.y, super.width, super.height, 0, 1, 0, 1, 500, 70);
                            super.mapData.shootBullet(super.index, false, 36, super.x, super.y, super.width, super.height, Util.nextInt(45, 135), Util.nextInt(5, 10), 0, 1, 0, 0);
                            super.mapData.updateAffect(super.index);
                            super.mapData.isTurn = true;
                            super.isShoot = true;
                        } else {
                            int arrBulletId[] = new int[]{36, 36, 0, 1, 2, 6, 7, 10, 11};
                            int bID = arrBulletId[Util.nextInt(arrBulletId.length)];
                            int w = player.width + GameData.radius(bID);
                            int h = player.height + GameData.radius(bID);
                            int toX = player.x - w / 2;
                            int toY = player.y - h;
                            this.trajectory = new BulletTrajectory(super.mapData, super.index, bID, super.x, super.y, super.width, super.height, toX, toY, w, h, 60, 10, true, true);
                            this.trajectory.start();
                        }
                    } else {
                        super.mapData.isTurn = true;
                    }
                } else if (super.trajectory.complate) {
                    if (super.trajectory.place) {
                        super.mapData.shootBullet(super.index, false, super.trajectory.bulletId, super.x, super.y, super.width, super.height, super.trajectory.ang, super.trajectory.force, super.trajectory.force2, 1, super.att, -1);
                        super.mapData.updateAffect(super.index);
                        super.isShoot = true;
                        super.mapData.isTurn = true;
                    } else {
                        super.mapData.shootBullet(super.index, false, 36, super.x, super.y, super.width, super.height, Util.nextInt(45, 135), Util.nextInt(5, 10), super.trajectory.force2, 1, 0, -1);
                        super.mapData.updateAffect(super.index);
                        super.isShoot = true;
                        super.mapData.isTurn = true;
                    }
                }
            }
        }
    }
}
