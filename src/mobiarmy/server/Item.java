package mobiarmy.server;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Văn Tú
 */
public class Item {
    
    public int id;
    public String name;
    public int xu;
    public int luong;
    public int carryable;
    public int num;
    
    public static Item[] entrys;
    
    public static void loadItem() throws SQLException {
        ArrayList<DBManager.DataRow> rows = Server.dbManager.selectColumnName("SELECT * FROM item");
        entrys = new Item[rows.size()];
        for (int i = 0; i < rows.size(); i++) {
            Item o = new Item();
            o.id = rows.get(i).getInt("id");
            o.name = rows.get(i).getString("name");
            o.xu = rows.get(i).getInt("xu");
            o.luong = rows.get(i).getInt("luong");
            o.carryable = rows.get(i).getInt("carryable");
            entrys[i] = o;
        }
    }
    
    public static Item get(int id) {
        for (Item entry : entrys) {
            if (entry.id == id) return entry;
        }
        return null;
    }
    public Item deepCopy() {
        Item copy = new Item();
        copy.id = this.id;
        copy.name = this.name;
        copy.xu = this.xu;
        copy.luong = this.luong;
        copy.carryable = this.carryable;
        copy.num = this.num;
        return copy;
    }
}
