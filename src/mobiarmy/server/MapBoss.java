package mobiarmy.server;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Văn Tú
 */
public class MapBoss {

    public int mapID;
    public int glassID;
    
    public static MapBoss[] entrys;
    
    public static void loadMapBoss() throws SQLException {
        ArrayList<DBManager.DataRow> rows = Server.dbManager.selectColumnName("SELECT * FROM map_boss");
        entrys = new MapBoss[rows.size()];
        for (int i = 0; i < rows.size(); i++) {
            entrys[i] = new MapBoss();
            entrys[i].mapID = rows.get(i).getByte("mapID");
            entrys[i].glassID = rows.get(i).getByte("glassID");
        }
    }
}
