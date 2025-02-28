package mobiarmy.war.Boss;

import mobiarmy.war.Player;

/**
 *
 * @author Văn Tú
 */
public class SmallBoom extends Boss {
    
    public SmallBoom(int hp, int att, int x, int y) {
        super("boss", (byte)11, hp, att, 0, 0, 0, 18, 18, x, y);
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
                        //di chuyen
                        int xOld = super.x;
                        super.updateXY(player.x, player.y);
                        if (Math.abs(super.x - player.x) < 30 && Math.abs(super.y - player.y) < 30) {
                            super.mapData.shootBullet(super.index, false, 32, super.x, super.y, super.width, super.height, 89, 1, 0, 1, 600, -1);
                            super.mapData.updateAffect(super.index);
                            super.mapData.isTurn = true;
                            super.isShoot = true;
                        } else if (super.buocdi < super.theluc) {
                            super.mapData.shootBullet(super.index, false, 5, super.x, super.y, super.width, super.height, super.x < xOld ? 70 : 110, 5, 0, 1, 0, -1);
                            super.mapData.updateAffect(super.index);
                            super.mapData.isTurn = true;
                            super.isShoot = true;
                        } else {
                            super.mapData.isTurn = true;
                        }
                    }
                }
            }
        }
    }
    
}