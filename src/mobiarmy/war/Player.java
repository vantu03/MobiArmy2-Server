package mobiarmy.war;

import java.util.ArrayList;
import mobiarmy.Util;
import mobiarmy.server.User;
import mobiarmy.war.Boss.bullet.Bullet;
import mobiarmy.war.Boss.bullet.BulletTrajectory;

/**
 *
 * @author Văn Tú
 */
public class Player {
    
    public User user;
    public int userID;
    public MapData mapData;
    public String name;
    public short x;
    public short y;
    public byte index;
    public int level;
    public int hpMax;
    public int hp;
    public int att;
    public int def;
    public int luck;
    public int teaP;
    public boolean isDie;
    public boolean isBoss;
    public boolean isFly;
    public int theluc;
    public int buocdi;
    public short width;
    public short height;
    public Item[] items;
    public boolean isUseItem;
    public int bulletId;
    public int glassID;
    public int angry;
    public int pixel;
    public boolean isCollision;
    public boolean isTurn;
    public int team;
    public boolean isRunSpeed;
    public int countInvisible;
    public int countNgungGio;
    public boolean isShoot;
    public byte nshoot;
    public int attAffect;
    public int countBlind;
    public boolean nextBlind;
    public int typePlayer;
    public int leadIndex;
    public int countFreeze;
    public boolean nextFreeze;
    public boolean isPow;
    public int countSuck;
    public int suckHP;
    public int countInvisible2;
    public boolean nextPoison;
    public boolean isPoison;
    public boolean isFaction;
    public ArrayList<Poison> listPoison;
    public BulletTrajectory trajectory;
    public int nTest;
    public int expAdd;
    public int expRemove;
    public boolean lucky;
    
    public class Item {
        
        public int itemId;
        public boolean isUse;
        
        public Item(int itemId) {
            this.itemId = itemId;
            this.isUse = false;
        }
        
    }
    
    public Player(MapData mapData, User user, String name, int glassID, int level, int hp, int att, int def, int luck, int teaP, int width, int height, byte[] items) {
        this.mapData = mapData;
        this.user = user;
        this.userID = user != null ? user.id : -1;
        this.name = name;
        this.index = -1;
        this.glassID = glassID;
        this.level = level;
        this.hp = this.hpMax = hp;
        this.pixel = this.hp * 25 / this.hpMax;
        this.att = att;
        this.def = def;
        this.luck = luck;
        this.teaP = teaP;
        this.theluc = 60;
        this.width = (short) width;
        this.height = (short) height;
        if (items != null) {
            this.items = new Item[items.length];
            for (int i = 0; i< items.length; i++) {
                this.items[i] = new Item(items[i]);
            }
        } else {
            this.items = new Item[0];
        }
        this.isUseItem = false;
        this.isCollision = true;
        this.isTurn = true;
        this.isRunSpeed = false;
        this.nshoot = 1;
        this.countInvisible = 0;
        this.attAffect = 0;
        this.countBlind = 0;
        this.nextBlind = false;
        this.typePlayer = 0;
        this.leadIndex = -1;
        this.countFreeze = 0;
        this.nextFreeze = false;
        this.isPow = false;
        this.countSuck = 0;
        this.suckHP = 0;
        this.countInvisible2 = 0;
        this.nextPoison = false;
        this.isPoison = false;
        this.isFaction = true;
        this.listPoison = new ArrayList<>();
        this.isDie = false;
        this.trajectory = null;
    }
    
    public byte bulletIdByGlassID() {
        if (this.glassID == 0) {
            return 0;
        }
        if (this.glassID == 1) {
            return 1;
        }
        if (this.glassID == 2) {
            return 2;
        }
        if (this.glassID == 3) {
            return 9;
        }
        if (this.glassID == 4) {
            return 10;
        }
        if (this.glassID == 5) {
            return 11;
        }
        if (this.glassID == 6) {
            return 19;
        }
        if (this.glassID == 7) {
            return 21;
        }
        if (this.glassID == 8) {
            return 17;
        }
        if (this.glassID == 9) {
            return 49;
        }
        return -1;
    }

    public void updateHP(int addHP) {
        this.hp += addHP;
        if(this.hp <= 0) {
            this.hp = 0;
        } else if(this.hp < 10) {
            this.hp = 10;
        } else if(this.hp > this.hpMax) {
            this.hp = this.hpMax;
        }
        int oldPixel = this.pixel;
        this.pixel = this.hp * 25 / this.hpMax;
        this.updateAngry(Math.abs(oldPixel - this.pixel) * 4);
        if(this.hp <= 0) {
            this.die();
        }
    }

    public void updateAngry(int addAngry) {
        this.angry += addAngry;
        if(this.angry < 0) {
            this.angry = 0;
        }
        if(this.angry > 100) {
            this.angry = 100;
        }
    }

    public void die() {
        this.hp = 0;
        this.isDie = true;
        this.countInvisible = 0;
        this.countInvisible2 = 0;
        this.isRunSpeed = false;
    }
    
    public void chuanHoaXY() {
        if (this.isFly) {
            return;
        }
        while(this.y < this.mapData.height + 200) {
            if(this.mapData.isCollisionMap(this.x, this.y)) {
                return;
            } else {
                this.y++;
            }
        }
    }
    
    public void addPoison(int leadIndex, int att) {
        this.listPoison.add(new Poison(leadIndex, att));
    }
    
    public int calcAtt(int x, int y, int d, int att) {
        if (!this.isCollision(x, y, d)) {
            return 0;
        }
        int dX = Math.abs(x - this.x);
        int dY = Math.abs(y - (this.y - (this.height / 2)));
        
        int pX = (int) (100F / (float) (d + this.width) * (float)dX);
        if (pX > 100) {
            pX = 100;
        }
        int pY = (int) (100F / (float) (d + this.height) * (float)dY);
        if (pY > 100) {
            pY = 100;
        }
        int down = 0;
        if (pX > 0) {
            down += att * (pX) / 100;
        }
        if (pY > 0) {
            down += att * (pY) / 100;
        }
        if (this.isCollision(x, y)) {
            down /= 20;
        }
        return att - down;
    }

    public void updateAllAffect(Player player) {
        this.chuanHoaXY();
        if (this.isDie) {
            return;
        }
        //mu
        if (this.nextBlind) {
            this.nextBlind = false;
            this.countBlind = 5;
            this.mapData.blind(this.index, 0);
        }
        //dong bang
        if (this.nextFreeze) {
            this.nextFreeze = false;
            this.countFreeze = 5;
            this.mapData.freeze(this.index, 0);
        }
        //khoi doc
        if (this.nextPoison) {
            this.nextPoison = false;
            this.isPoison = true;
            this.addPoison(player.index, 150);
            this.mapData.poison(this.index);
        }
        //- hp
        int hpOld = this.hp;
//        if (this.attAffect) {
//            
//        }
        
        if (this.attAffect > 0 || this.y > this.mapData.height + 100) {
            if (this.y > this.mapData.height + 100) {
                this.attAffect = this.hp;
            } else {
                if(player.lucky) {
                    this.attAffect *= 2;
                }
                int def = this.def;
                if (this.lucky) {
                    def += 3000;
                }
                float reductionFactor = 1 - (float) Math.exp(-def / 6000.0);
                int reducedDamage = Math.round(this.attAffect * reductionFactor);
                this.attAffect = Math.max(this.attAffect - reducedDamage, 0);
                if (this.hp - this.attAffect < 10 && this.lucky) {
                    this.attAffect = this.hp - 10;
                }
            }
            this.mapData.addTimeUntilAction(1000);
            this.updateHP(-this.attAffect);
            
            this.mapData.updateHP(this.index, this.hp, this.pixel);
            this.mapData.updateAngry(this.index, this.angry);
        }
        if (player.countSuck > 0) {
            player.suckHP += ((hpOld - this.hp) / 2);
        }
        this.attAffect = 0;
        //xp
        if (this.isDie) {
            if (player.isBoss) {
                return;
            }
            if (this.typePlayer == 1 && this.isBoss) {
                player.expAdd += 2;
            }
            if (player.leadIndex != -1) {
                player = this.mapData.players[player.leadIndex];
            }
            if (player.index != this.index) {
                //cong exp neu khac team
                if (player.team != this.team) {
                    if (this.typePlayer == 0) {
                        player.expAdd += (this.level / 2) + 2;
                    }
                }
                //-xp neu ban dong doi
                if (player.team != 3 && player.team == this.team) {
                    player.expRemove += 5;
                }
            }
        }
    }

    public void updateXY(int x, int y) {
        while(x != this.x || y != this.y) {
            int preX = this.x;
            int preY = this.y;
            if(x < this.x) {
                this.move(false);
            } else if(x > this.x) {
                this.move(true);
            }
            // if ko di chuyen dc
            if(preX == this.x && preY <= this.y) {
                break;
            }
            this.mapData.addTimeUntilAction(50);
            if (this.y > this.mapData.height + 100) {
                this.mapData.addTimeUntilAction(1000);
                break;
            }
        }
        this.mapData.changeLocation(this.index, this.x, this.y);
        if (this.y > this.mapData.height + 100) {
            this.updateHP(-this.hp);
            this.mapData.updateHP(this.index, this.hp, this.pixel);
            this.mapData.updateAngry(this.index, this.angry);
            //this.mapData.setXY(this.index, this.x, this.y);
            this.mapData.isTurn = true;
        }
    }
    
    private void move(boolean addX) {
        if (this.countFreeze > 0) {
            return;
        }
        byte step = (byte) (this.isRunSpeed ? 2 : 1);
        if(this.buocdi > this.theluc) {
            return;
        }
        this.buocdi++;
        if(addX) {
            this.x += step;
        } else {
            this.x -= step;
        }
        if(this.mapData.isCollisionMap(this.x, this.y - 5)) {
            this.buocdi--;
            if(addX) {
                this.x -= step;
            } else {
                this.x += step;
            }
            return;
        }
        for(int i = 4; i >= 0; i--) {
            if(this.mapData.isCollisionMap(this.x, this.y - i)) {
                this.y -= i;
                return;
            }
        }
        this.chuanHoaXY();
    }

    public boolean isCollision(int x, int y) {
        return this.mapData.inRegion(x, y, this.x - this.width / 2, this.y - this.height, this.width, this.height);
    }
    
    public boolean isCollision(int x, int y, int d) {
        return this.mapData.inRegion(x, y, this.x - this.width / 2 - d, this.y - this.height - d, this.width + 2 * d, this.height + 2 * d);
    }
    
    public void attBullet(Bullet bullet) {
        if (this.glassID == 23 || this.glassID == 24) {
            this.isCollision = false;
            this.attAffect = this.hp;
        }
        if (bullet.bulletId == 51) {
            this.nextBlind = true;
        }
        if (bullet.bulletId == 54) {
            this.nextFreeze = true;
        }
        if (bullet.bulletId == 55) {
            this.nextPoison = true;
        }
        if (bullet.radius > 0) {
            int att = this.calcAtt(bullet.bX, bullet.bY, bullet.radius, bullet.att);
            if (bullet.isSuper == 1) {
                att = (int) (att + (att * 0.05F));
            }
            if (bullet.isSuper == 2) {
                att = (int) (att + (att * 0.1F));
            }
            this.attAffect += att;
        }
    }
    
    public void changeLocationFly(int x, int y) {
        this.mapData.addTimeUntilAction(Math.abs(x - this.x) + Math.abs(y - this.y) * 5);
        this.x = (short) x;
        this.y = (short) y;
        this.mapData.changeLocationFly(this.index, this.x, this.y);
    }
    
    public Player.Item findUnusedItemById(int itemId) {
        for (Item item : this.items) {
            if (item.itemId == itemId && !item.isUse) {
                return item;
            }
        }
        return null;
    }
    
    public void nextLuck() {
        double probability = 1 - Math.exp(-this.luck / 6000.0);
        this.lucky = !this.mapData.disableLuck && Util.nextDouble() < probability && !this.isDie;
    }
    
    public void updateTurn() {
        this.buocdi = 0;
        this.nTest = 0;
        this.isUseItem = false;
        this.bulletId = this.bulletIdByGlassID();
        this.nshoot = 1;
        this.trajectory = null;
        if (this.countInvisible > 0) {
            this.countInvisible--;
            if (this.countInvisible == 0 && this.countInvisible2 == 0) {
                this.mapData.cancelInvisible(this.index);
            }
        }
        if (this.countNgungGio > 0) {
            this.countNgungGio--;
            if (this.countNgungGio == 0) {
                
            }
        }
        if (this.user != null && this.user.session != null) {
            this.user.session.sessionHandler.setXY(this.index, this.x, this.y);
        }
        this.isShoot = false;
        if (this.countBlind > 0) {
            this.countBlind--;
            if (this.countBlind == 0) {
                this.mapData.blind(this.index, 1);
            }
        }
        if (this.countFreeze > 0) {
            this.countFreeze--;
            if (this.countFreeze == 0) {
                this.mapData.freeze(this.index, 1);
            }
        }
        if (this.mapData.nTurn > 0 && this.typePlayer == 0) {
            this.updateAngry(10);
            this.mapData.updateAngry(this.index, this.angry);
        }
        if (this.countSuck > 0) {
            this.countSuck--;
        }
        this.suckHP = 0;
        if (this.countInvisible2 > 0) {
            this.countInvisible2--;
            if (this.countInvisible == 0 && this.countInvisible2 == 0) {
                this.mapData.cancelInvisible(this.index);
            }
        }
        if (!this.listPoison.isEmpty()) {
            for (int i = 0; i < this.listPoison.size(); i++) {
                this.attAffect = this.listPoison.get(i).att;
                this.mapData.updateAffect(this.listPoison.get(i).leadIndex);
                if (this.isDie) {
                    this.mapData.isTurn = true;
                    break;
                }
            }
        }
    }
    
    public void update() {
    }
}
