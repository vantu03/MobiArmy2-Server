package mobiarmy.server;

import com.google.gson.Gson;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;


public class Equip {
    
    public byte glassID;
    public short id;
    public byte type;
    public byte bullet;
    public short icon;
    public byte level;
    public byte level2;
    public short[] x;
    public short[] y;
    public byte[] w;
    public byte[] h;
    public byte[] dx;
    public byte[] dy;
    public byte[] inv_ability;
    public byte[] inv_percen;
    public byte date;
    public int luong;
    public int xu;
    public short[] slot;
    public String name;
    public byte vip;
    public short[] data;
    public int dbKey;
    public boolean isUse;
    public long renewalDate;
    
    public Equip() {
        this.slot = new short[]{-1, -1, -1};
    }
    
    public int slot() {
        int num = this.slot.length;
        for (int i = 0; i < this.slot.length; i++) {
            if (this.slot[i] != -1) {
                num--;
            }
        }
        return num;
    }
    
    public int priceSlot() {
        int sum = 0;
        for (int i = 0; i < this.slot.length; i++) {
            if (this.slot[i] != -1) {
                sum += LinhTinh.get(this.slot[i]).xu;
            }
        }
        return sum;
    }
    
    public int calculatePrice() {
        int sum = 0;
        if (this.xu != -1) {
            sum = this.xu / 2;
        } else if (this.luong != -1) {
            sum = this.luong * 500;
        }
        if (sum > 0) {
            sum = (int) ((float)sum / (float)this.date * (float)this.date());
        }
        return sum;
    }
    
    public int renewalPrice() {
        int sum = (int) (this.priceSlot() * 0.05F);
        if (this.xu != -1) {
            sum += this.xu;
        } else if (this.luong != -1) {
            sum += this.luong * 1000;
        }
        return sum;
    }
    
    public int date() {
        int d = (int) (this.date - ((System.currentTimeMillis() - this.renewalDate) / (1000 * 60 * 60 * 24)));
        if (d < 0) {
            d = 0;
        }
        return d;
    }

    public static Equip[] entrys;
    public static HashMap<Byte, HashMap<Byte, ArrayList<Equip>>> equipsByGlassIDAndType = new HashMap<>();
    
    public static void loadEquip() throws SQLException {
        ArrayList<DBManager.DataRow> rows = Server.dbManager.selectColumnName("SELECT * FROM equip");
        entrys = new Equip[rows.size()];
        equipsByGlassIDAndType.clear();
    
        for (int i = 0; i < rows.size(); i++) {
            entrys[i] = new Equip();
            entrys[i].glassID = rows.get(i).getByte("glassID");
            entrys[i].id = rows.get(i).getShort("id");
            entrys[i].type = rows.get(i).getByte("type");
            entrys[i].bullet = rows.get(i).getByte("bullet");
            entrys[i].icon = rows.get(i).getShort("icon");
            entrys[i].level = rows.get(i).getByte("level");
            entrys[i].x = new Gson().fromJson(rows.get(i).getString("x"), short[].class);
            entrys[i].y = new Gson().fromJson(rows.get(i).getString("y"), short[].class);
            entrys[i].w = new Gson().fromJson(rows.get(i).getString("w"), byte[].class);
            entrys[i].h = new Gson().fromJson(rows.get(i).getString("h"), byte[].class);
            entrys[i].dx = new Gson().fromJson(rows.get(i).getString("dx"), byte[].class);
            entrys[i].dy = new Gson().fromJson(rows.get(i).getString("dy"), byte[].class);
            entrys[i].inv_ability = new Gson().fromJson(rows.get(i).getString("inv_ability"), byte[].class);
            entrys[i].inv_percen = new Gson().fromJson(rows.get(i).getString("inv_percen"), byte[].class);
            entrys[i].date = rows.get(i).getByte("date");
            entrys[i].luong = rows.get(i).getInt("luong");
            entrys[i].xu = rows.get(i).getInt("xu");
            entrys[i].name = rows.get(i).getString("name");
            entrys[i].vip = rows.get(i).getByte("vip");
            entrys[i].data = new Gson().fromJson(rows.get(i).getString("data"), short[].class);
            equipsByGlassIDAndType
                .computeIfAbsent(entrys[i].glassID, k -> new HashMap<>())  // Tạo HashMap cho glassID nếu chưa có
                .computeIfAbsent(entrys[i].type, k -> new ArrayList<>())   // Tạo ArrayList cho type nếu chưa có
                .add(entrys[i]);                                           // Thêm trang bị vào danh sách
        }
    }
        
    public static Equip get(byte glassID, short id) {
        for (Equip equip : entrys) {
            if (equip.glassID == glassID && equip.id == id) {
                return equip;
            }
        }
        return null;
    }
        
    public static Equip get(byte glassID, int id, int type) {
        for (Equip equip : entrys) {
            if (equip.glassID == glassID && equip.id == id && equip.type == type) {
                return equip;
            }
        }
        return null;
    }
    
    public Equip deepCopy() {
        Equip copy = new Equip();

        // Copying primitive fields
        copy.glassID = this.glassID;
        copy.id = this.id;
        copy.type = this.type;
        copy.bullet = this.bullet;
        copy.icon = this.icon;
        copy.level = this.level;
        copy.level2 = this.level2;
        copy.date = this.date;
        copy.luong = this.luong;
        copy.xu = this.xu;
        copy.slot = this.slot != null ? this.slot.clone() : null;
        copy.name = this.name; // Assuming String is immutable, so shallow copy is fine
        copy.vip = this.vip;
        copy.dbKey = this.dbKey;
        copy.isUse = this.isUse;

        // Deep copying arrays
        copy.x = this.x != null ? this.x.clone() : null;
        copy.y = this.y != null ? this.y.clone() : null;
        copy.w = this.w != null ? this.w.clone() : null;
        copy.h = this.h != null ? this.h.clone() : null;
        copy.dx = this.dx != null ? this.dx.clone() : null;
        copy.dy = this.dy != null ? this.dy.clone() : null;
        copy.inv_ability = this.inv_ability != null ? this.inv_ability.clone() : null;
        copy.inv_percen = this.inv_percen != null ? this.inv_percen.clone() : null;
        copy.data = this.data != null ? this.data.clone() : null;

        return copy;
    }

    
}
