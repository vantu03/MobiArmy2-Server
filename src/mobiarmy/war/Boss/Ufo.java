package mobiarmy.war.Boss;

import mobiarmy.Util;
import mobiarmy.war.Player;

/**
 *
 * @author Văn Tú
 */
public class Ufo extends Boss {
    
    private boolean isLod;
    
    public Ufo(int hp, int att, int x, int y) {
        super("UFO", (byte)16, hp, att, 0, 0, 0, 51, 46, x, y);
        super.isFly = true;
        this.isLod = false;
    }
    
    @Override
    public void update() {
        super.update();
        //Đến lượt chưa
        if (super.index == super.mapData.getTurn() && System.currentTimeMillis() > super.mapData.timeUntilAction2) {
            //Bắn
            if (!super.isShoot) {
                if (this.isLod) {
                    int yfake = super.y;
                    conguoichoi:
                    {
                        while(true) {
                            if (super.mapData.isCollisionMap(super.x, yfake)) {
                                break conguoichoi;
                            }
                            if (yfake > super.mapData.height + 100) {
                                this.isLod = false;
                                break conguoichoi;
                            }
                            yfake++;
                        }
                    }
                }
                if (!this.isLod) {
                    //Tìm đối thủ gần
                    Player player = super.mapData.getPlayerNear(super.index);
                    if (player != null) {
                        this.isLod = true;
                        short toX = player.x;
                        short toY = super.y;
                        if (super.y > player.y - 200 || super.y < player.y + 200) {
                            toY = (short) (player.y - Util.nextInt(200, 350));
                        }
                        while(true) {
                            if (!super.mapData.isCollisionMap(toX, toY)) {
                                break;
                            }
                            toY--;
                        }
                        super.changeLocationFly(toX, toY);
                    }
                    super.mapData.isTurn = true;
                } else {
                    this.isLod = false;
                    super.mapData.shootBullet(super.index, false, 42, super.x, super.y, super.width, super.height, 270, 10, 0, 1, 600, -1);
                    super.mapData.updateAffect(super.index);
                    super.isShoot = true;
                    super.mapData.isTurn = true;
                }
            }
        }
    }
    
    @Override
    public void die() {
        super.die();
        super.isCollision = false;
    }
    
}

