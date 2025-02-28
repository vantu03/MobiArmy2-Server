package mobiarmy.server;

import com.google.gson.Gson;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Văn Tú
 */
public class Glass {
    
    public byte id;
    public String name;
    public int xu;
    public int luong;
    public int friction;
    public int angle;
    public int att;
    public int distance;
    public int bullet;
    
    public User         user;
    public int[]        ability;
    public short[]      equipID;
    public short[]      data;
    public int[]        dbKey;
    public int          point;
    public int          level;
    public int          exp;
    public boolean      isOpen;
    
    public static Glass[] entrys;
    
    public static void loadGlass() throws SQLException {
        ArrayList<DBManager.DataRow> rows = Server.dbManager.selectColumnName("SELECT * FROM glass");
        entrys = new Glass[rows.size()];
        for (DBManager.DataRow row : rows) {
            Glass glass = new Glass();
            glass.id = row.getByte("id");
            glass.name = row.getString("name");
            glass.equipID = new Gson().fromJson(row.getString("equipID"), short[].class);
            glass.ability = new Gson().fromJson(row.getString("ability"), int[].class);
            glass.att = row.getInt("att");
            glass.friction = row.getInt("friction");
            glass.angle = row.getInt("angle");
            glass.distance = row.getInt("distance");
            glass.bullet = row.getInt("bullet");
            glass.xu = row.getInt("xu");
            glass.luong = row.getInt("luong");
            entrys[glass.id] = glass;
        }
    }
    
    public Glass() {
        this.dbKey = new int[5];
        this.ability = new int[5];
    }
    
    public void updateAll() {
        this.level = Exp.getLevelExp(this.exp);
        this.equipID = entrys[this.id].equipID.clone();
        this.data = null;
        this.dbKey = new int[]{-1, -1, -1, -1, -1};
        //Trang bị thường
        for (Equip equip : this.user.equips) {
            if (equip.glassID == this.id && equip.isUse) {
                if (equip.date() == 0) {
                    equip.isUse = false;
                } else if (equip.vip != 0) {
                    this.data = equip.data != null ? equip.data.clone() : null;
                } else {
                    this.equipID[equip.type] = equip.id;
                    this.dbKey[equip.type] = equip.dbKey;
                }
            }
        }
    }
    
    public int getPercentLevel() {
        return Exp.getPercentExp(this.level, this.exp);
    }
    
    public int getExpMaxLevel() {
        return Exp.get(this.level).exp;
    }
    
    public void upadtePoint(int[] pointAdd) {
        int totalPoint = 0;
        for (int i = 0; i < pointAdd.length; i++) {
            if (pointAdd[i] < 0) {
                return;
            }
            totalPoint = totalPoint + pointAdd[i];
        }
        if (totalPoint != 0 && totalPoint <= this.point) {
            for (int i = 0; i < pointAdd.length; i++) {
                this.ability[i] = this.ability[i] + pointAdd[i];
            }
            this.updateAll();
            this.point = this.point - totalPoint;
            if (this.user.session != null) {
                this.user.session.sessionHandler.loadInfo();
            }
        }
    }
    
    public void addExp(int add, boolean flag) {
        this.exp = this.exp + add;
        if (this.exp > Exp.entrys[Exp.entrys.length - 1].exp) {
            this.exp = Exp.entrys[Exp.entrys.length - 1].exp;
        }
        int d = Exp.getLevelExp(this.exp) - this.level;
        if (d != 0) {
            this.point = this.point + d * 3;
            this.level = Exp.getLevelExp(this.exp);
            if (flag && this.user != null && this.user.session != null) {
                this.user.session.sessionHandler.updateExp(add, this.exp, this.getExpMaxLevel(), this.level, this.getPercentLevel(), this.point);
            }
        } else {
            if (flag && this.user != null && this.user.session != null) {
                this.user.session.sessionHandler.updateExp(add, this.exp, this.getExpMaxLevel(), this.getPercentLevel());
            }
        }
    }
    
    public int[] createAbility() {
        int iArr[] = new int[5];
        int[] envAdd = new int[5];
        int[] percenAdd = new int[5];
        for (int i = 0; i < envAdd.length; i++) {
            for (Equip equip : this.user.equips) {
                if (equip.glassID == this.id && equip.isUse) {
                    envAdd[i] = envAdd[i] + equip.inv_ability[i];
                    percenAdd[i] = percenAdd[i] + equip.inv_percen[i];
                }
            }
        }
        //HP
        iArr[0] = (1000 + (this.ability[0] * 10)) + (envAdd[0] * 10) + ((1000 + this.ability[0]) * percenAdd[0] / 100);
        //SM
        iArr[1] = this.att * (((this.ability[1] + envAdd[1]) / 3) + 100 + percenAdd[1]) / 100;
        //PT
        iArr[2] = ((this.ability[2] + envAdd[2]) * 10);
        iArr[2] = iArr[2] + (iArr[2] * percenAdd[2] / 100);
        //MM
        iArr[3] = ((this.ability[3] + envAdd[3]) * 10);
        iArr[3] = iArr[3] + (iArr[3] * percenAdd[3] / 100);
        //DD
        iArr[4] = ((this.ability[4] + envAdd[4]) * 10);
        iArr[4] = iArr[4] + (iArr[4] * percenAdd[4] / 100);
        return iArr;
    }

    public Glass deepCopy() {
        Glass glass = new Glass();
        glass.id = this.id;
        glass.name = this.name;
        glass.xu = this.xu;
        glass.luong = this.luong;
        glass.friction = this.friction;
        glass.angle = this.angle;
        glass.att = this.att;
        glass.distance = this.distance;
        glass.bullet = this.bullet;
        glass.point = this.point;
        glass.level = this.level;
        glass.exp = this.exp;
        glass.isOpen = this.isOpen;
        glass.ability = this.ability != null ? this.ability.clone() : null;
        glass.equipID = this.equipID != null ? this.equipID.clone() : null;
        glass.data = this.data != null ? this.data.clone() : null;
        glass.dbKey = this.dbKey != null ? this.dbKey.clone() : null;
        glass.user = this.user;

        return glass;
    }

}