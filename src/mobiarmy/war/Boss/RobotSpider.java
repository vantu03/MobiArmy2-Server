package mobiarmy.war.Boss;

import java.util.ArrayList;
import mobiarmy.Util;
import mobiarmy.war.Boss.bullet.BulletTrajectory;
import mobiarmy.war.Player;

/**
 *
 * @author Văn Tú
 */
public class RobotSpider extends Boss {

    public RobotSpider(int hp, int att, int x, int y) {
        super("Robot", (byte)13, hp, att, 0, 0, 0, 42, 42, x, y);
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
                            super.mapData.shootBullet(super.index, false, 8, super.x, super.y, super.width, super.height, Util.nextInt(180, 360), Util.nextInt(1, 5), 0, 1, super.att, -1);
                            super.mapData.shootBullet(super.index, false, 36, super.x, super.y, super.width, super.height, Util.nextInt(45, 135), Util.nextInt(3, 10), 0, 1, super.att, -1);
                            super.mapData.updateAffect(super.index);
                            super.mapData.isTurn = true;
                            super.isShoot = true;
                        } else {
                            int arrBulletId[] = new int[]{1, 8, 10, 33};
                            int bID = arrBulletId[Util.nextInt(arrBulletId.length)];
                            this.trajectory = new BulletTrajectory(super.mapData, super.index, bID, super.x, super.y, super.width, super.height, player.x - player.width / 2, player.y - player.height, player.width, player.height, bID == 8 ? -45 : 60, bID == 8 ? 1 : 10, bID == 8, true);
                            this.trajectory.start();
                        }
                    }
                } else if (super.trajectory.complate) {
                    if (super.trajectory.place) {
                        super.mapData.shootBullet(super.index, false, super.trajectory.bulletId, super.x, super.y, super.width, super.height, super.trajectory.ang, super.trajectory.force, super.trajectory.force2, 1, super.att, -1);
                        if (super.trajectory.bulletId == 8) {
                            super.mapData.shootBullet(super.index, false, 36, super.x, super.y, super.width, super.height, Util.nextInt(45, 135), Util.nextInt(3, 10), 0, 1, super.att, -1);
                        }
                        super.mapData.updateAffect(super.index);
                        super.isShoot = true;
                        super.mapData.isTurn = true;
                    } else {
                        
                    }
                    super.mapData.isTurn = true;
                }
            }
        }
    }

}
