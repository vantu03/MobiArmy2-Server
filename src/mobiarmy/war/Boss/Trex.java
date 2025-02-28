package mobiarmy.war.Boss;

import mobiarmy.Util;
import mobiarmy.war.Boss.bullet.BulletTrajectory;
import mobiarmy.war.Player;

/**
 *
 * @author Văn Tú
 */
public class Trex extends Boss {

    public Trex(int hp, int att, int x, int y) {
        super("T-rex", (byte)15, hp, att, 0, 0, 0, 45, 50, x, y);
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
                        if (Math.abs(super.x - player.x) < 90) {
                            super.mapData.shootBullet(super.index, false, 35, super.x, super.y, super.width, super.height, 0, 1, 0, 1, 1200, 400);
                            super.mapData.updateAffect(super.index);
                            super.mapData.isTurn = true;
                            super.isShoot = true;
                        } else {
                            player = super.mapData.popPlayerRand(super.index);
                            if (Util.nextInt(100) < 30) {
                                super.mapData.shootBullet(super.index, false, 37, super.x, super.y, super.width, super.height, 90, 10, player.x, 1, 750, -1);
                                super.mapData.updateAffect(super.index);
                                super.mapData.isTurn = true;
                                super.isShoot = true;
                            } else {
                                int w = player.width * 2;
                                int h = player.height;
                                int toX = player.x - w / 2;
                                int toY = player.y - h;
                                this.trajectory = new BulletTrajectory(super.mapData, super.index, 40, super.x, super.y, super.width, super.height, Util.nextInt(toX - 50, toX + 50), toY, w, h, 60, 15, false, false);
                                this.trajectory.start();
                            }
                        }
                    }
                } else if (super.trajectory.complate) {
                    if (super.trajectory.place) {
                        super.mapData.shootBullet(super.index, false, super.trajectory.bulletId, super.x, super.y, super.width, super.height, super.trajectory.ang, super.trajectory.force, super.trajectory.force2, 1, 300, 50);
                        super.mapData.updateAffect(super.index);
                        super.isShoot = true;
                    }
                    super.mapData.isTurn = true;
                }
            }
        }
    }

}
