package mobiarmy.war.Boss.bullet;

import mobiarmy.war.Boss.Boss;
import mobiarmy.war.Player;

/**
 *
 * @author Văn Tú
 */
public class ItemUfo extends Boss {
    
    private boolean isLod;
    
    public ItemUfo(int team, int leadIndex, int hp, int att, int x, int y) {
        super("UFO", (byte)16, hp, att, 0, 0, 0, 24, 24, x, y);
        super.isFly = true;
        super.isFaction = false;
        super.isTurn = true;
        this.isLod = false;
        super.isBoss = false;
        super.team = team;
        super.leadIndex = leadIndex;
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
                    Player player = null;
                    for (Player player1 : super.mapData.players) {
                        if (player1 != null && !player1.isDie && player1.isFaction && player1.team != this.team && (player == null || (Math.abs(player1.x - super.x) < Math.abs(player.x - super.x) && (Math.abs(player1.y - super.y) < Math.abs(player.y - super.y))))) {
                            player = player1;
                        }
                    }
                    if (player != null) {
                        this.isLod = true;
                        short toX = player.x;
                        short toY = super.y;
                        if (super.y > player.y - 200 || super.y < player.y + 200) {
                            toY = (short) (player.y - 200);
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
                    super.mapData.shootBullet(super.index, false, 42, super.x, super.y, super.width, super.height, 270, 10, 0, 1, super.att, -1);
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
