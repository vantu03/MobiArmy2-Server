package mobiarmy.server;

import com.google.gson.Gson;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Văn Tú
 */
public class ShopEquipment {
    
    public static Equip entrys[];
    
    public static void loadShopEquipment() throws SQLException {
        ArrayList<DBManager.DataRow> rows = Server.dbManager.selectColumnName("SELECT * FROM shop_equipment");
        entrys = new Equip[rows.size()];
        for (int i = 0; i < rows.size(); i++) {
            entrys[i] = Equip.get(rows.get(i).getByte("glassID"), rows.get(i).getByte("equipId")).deepCopy();
            entrys[i].name = rows.get(i).getString("name");
            entrys[i].xu = rows.get(i).getInt("xu");
            entrys[i].luong = rows.get(i).getInt("luong");
            entrys[i].inv_ability = new Gson().fromJson(rows.get(i).getString("inv_ability"), byte[].class);
            entrys[i].inv_percen = new Gson().fromJson(rows.get(i).getString("inv_percen"), byte[].class);
        }
    }
    
    public static ArrayList<Equip> generate(byte glassID) {
        ArrayList<Equip> equips = new ArrayList<>();
        for (Equip entry : entrys) {
            if (entry.glassID == glassID) {
                equips.add(entry);
            }
        }
        return equips;
    }
    
}
