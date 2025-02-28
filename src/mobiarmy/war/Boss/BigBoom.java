package mobiarmy.war.Boss;

import mobiarmy.Util;
import mobiarmy.war.Boss.bullet.BulletTrajectory;
import mobiarmy.war.Player;
import mobiarmy.war.Room;

/**
 *
 * @author Văn Tú
 */
public class BigBoom extends Boss {
    
    public BigBoom(int hp, int att, int x, int y) {
        super("BigBoom", (byte)12, hp, att, 0, 0, 0, 28, 28, x, y);
        super.theluc = 100;
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
                        if (Util.nextInt(100) < 40 && super.mapData.nPlayer < Room.MAX_E_PLAYER) {
                            //tha bom
                            int tX = player.x - player.width / 2, tY = player.y - player.height;
                            this.trajectory = new BulletTrajectory(super.mapData, super.index, 34, super.x, super.y, super.width, super.height, tX > super.x ? Util.nextInt(tX, tX + 150) : Util.nextInt(tX - 150, tX), tY, player.width, player.height, 70, 15, false, false);
                            this.trajectory.start();
                        } else {
                            //di chuyen
                            super.updateXY(player.x, player.y);
                            //nếu gần sẽ phát nổ
                            if (Math.abs(super.x - player.x) < 50 && Math.abs(super.y - player.y) < 40) {
                                super.mapData.shootBullet(super.index, false, 31, super.x, super.y, super.width, super.height, 0, 1, 1, 1, 1500, -1);
                                super.mapData.updateAffect(super.index);
                                super.mapData.isTurn = true;
                                super.isShoot = true;
                            }
                            //nếu x gần y xa sẽ bắn lưu đạn
                            else if (super.buocdi < super.theluc) {
                                this.trajectory = new BulletTrajectory(super.mapData, super.index, 7, super.x, super.y, super.width, super.height, player.x - player.width / 2, player.y - player.height, player.width, player.height, 60, 25, false, false);
                                this.trajectory.start();
                            } else {
                                super.mapData.isTurn = true;
                            }
                        }
                    }
                } else if (super.trajectory.complate) {
                    if (super.trajectory.place) {
                        super.mapData.shootBullet(super.index, false, super.trajectory.bulletId, super.x, super.y, super.width, super.height, super.trajectory.ang, super.trajectory.force, super.trajectory.force2, 1, super.att, -1);
                        super.mapData.updateAffect(super.index);
                        super.mapData.isTurn = true;
                        super.isShoot = true;
                    } else {
                        super.mapData.shootBullet(super.index, false, 34, super.x, super.y, super.width, super.height, new short[]{70, 110}[Util.nextInt(2)], 1, 0, 1, super.att, -1);
                        super.mapData.updateAffect(super.index);
                        super.mapData.isTurn = true;
                        super.isShoot = true;
                        super.mapData.bossAdds.clear();
                    }
                }
            }
        }
    }
    
}
