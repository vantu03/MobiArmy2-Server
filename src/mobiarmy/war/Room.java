package mobiarmy.war;

import java.sql.SQLException;
import java.util.ArrayList;
import mobiarmy.server.DBManager;
import mobiarmy.server.Server;

/**
 *
 * @author Văn Tú
 */
public class Room {
    
    public byte type;
    public String name;
    
    public ArrayList<RoomInfo> roomInfos;
    
    public static final byte MAX_E_PLAYER = 100;
    public static final byte MAX_PLAYER = 8;
    public static final int INDEX_START = 8;
    
    public static class RoomBoss {
        
        public int mapId;
        public int glass;
        
        public RoomBoss(int mapId, int glass) {
            this.mapId = mapId;
            this.glass = glass;
        }
        
    }
    
    public static Room[] entrys;
    
    public static void loadRoom() throws SQLException {
        ArrayList<DBManager.DataRow> rows = Server.dbManager.selectColumnName("SELECT * FROM room");
        entrys = new Room[rows.size()];
        for (int i = 0; i < rows.size(); i++) {
            entrys[i] = new Room();
            entrys[i].type = rows.get(i).getByte("type");
            entrys[i].name = rows.get(i).getString("name");
        }
    }
    
}
