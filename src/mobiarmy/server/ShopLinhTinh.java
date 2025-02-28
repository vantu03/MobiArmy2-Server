package mobiarmy.server;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Văn Tú
 */
public class ShopLinhTinh {

    
    public static LinhTinh entrys[];
    
    public static void loadShopLinhTinh() throws SQLException {
        ArrayList<DBManager.DataRow> rows = Server.dbManager.selectColumnName("SELECT * FROM shop_linhtinh");
        entrys = new LinhTinh[rows.size()];
        for (int i = 0; i < rows.size(); i++) {
            entrys[i] = LinhTinh.get(rows.get(i).getByte("id")).deepCopy();
            entrys[i].isSelectNum = rows.get(i).getBoolean("isSelectNum");
        }
    }
    
    public static LinhTinh get(int id) {
        for (LinhTinh entry : entrys) {
            if (entry.id == id) {
                return entry;
            }
        }
        return null;
    }
}
