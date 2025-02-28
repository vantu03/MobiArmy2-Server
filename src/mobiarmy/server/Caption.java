package mobiarmy.server;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Văn Tú
 */
public class Caption {
    
    public byte level;
    public String caption;
    
    public static Caption[] entrys;
    
    public static void loadCaption() throws SQLException {
        ArrayList<DBManager.DataRow> rows = Server.dbManager.selectColumnName("SELECT * FROM caption");
        entrys = new Caption[rows.size()];
        for (int i = 0; i < rows.size(); i++) {
            entrys[i] = new Caption();
            entrys[i].caption = rows.get(i).getString("caption");
            entrys[i].level = rows.get(i).getByte("level");
        }
    }
    
}
