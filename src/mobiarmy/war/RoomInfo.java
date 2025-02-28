package mobiarmy.war;

import com.google.gson.Gson;
import java.sql.SQLException;
import java.util.ArrayList;
import mobiarmy.server.DBManager;
import mobiarmy.server.Server;

/**
 *
 * @author Văn Tú
 */
public class RoomInfo {

    public byte type;
    public byte id;
    public byte lv;
    public String name = "";
    public String playerMax = "";
    public byte roomFree;
    public byte roomWait;
    public int stat;
    public byte[] map;
    public ArrayList<RoomWait> roomWaits;
    
    public static RoomInfo[] entrys;
    
    public static class RoomInfoName {
        public int start;
        public int index;
        public String name;

        public RoomInfoName(int start, int index, String name) {
            this.start = start;
            this.index = index;
            this.name = name;
        }
    }
    
    public static final ArrayList<RoomInfo> rooms = new ArrayList<>();
    
    public static final ArrayList<RoomInfoName> roomNames = new ArrayList<>();
    
    
    public static void loadRoomInfo() throws SQLException {
        ArrayList<DBManager.DataRow> rows = Server.dbManager.selectColumnName("SELECT * FROM room_info ORDER BY type ASC;");
        entrys = new RoomInfo[rows.size()];
        rooms.clear();
        roomNames.clear();
        ArrayList<Byte> types = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            entrys[i] = new RoomInfo();
            entrys[i].type = rows.get(i).getByte("type");
            entrys[i].id = (byte) i;//rows.get(i).getByte("boardID");
            entrys[i].name = rows.get(i).getString("name");
            entrys[i].map = new Gson().fromJson(rows.get(i).getString("map"), byte[].class);
            entrys[i].roomWait = rows.get(i).getByte("roomWait");
            entrys[i].roomWaits = new ArrayList<>();
            for (int j = 0; j < entrys[i].roomWait; j++) {
                entrys[i].roomWaits.add(new RoomWait(
                        entrys[i].id,
                        entrys[i].type,
                        (byte) j,
                        entrys[i].map,
                        rows.get(i).getInt("minMoney"),
                        rows.get(i).getInt("maxMoney"),
                        rows.get(i).getByte("maxPlayer")
                ));
            }
            rooms.add(entrys[i]);
            if (!types.contains(entrys[i].type)) {
                types.add(entrys[i].type);
            }
            if (entrys[i].name != null) {
                roomNames.add(new RoomInfoName(types.size() > 1 ? types.size() - 1 : 0, i, entrys[i].name));
            }
        }
    }
    
    public static RoomInfo get(byte id) {
        for (RoomInfo roomInfo : entrys) {
            if (roomInfo.id == id) {
                return roomInfo;
            }
        }
        return null;
    }
    
    public static RoomWait get(byte roomID, byte boardID) {
        for (RoomInfo roomInfo : entrys) {
            if (roomInfo.id == roomID) {
                for (RoomWait roomWait : roomInfo.roomWaits) {
                    if (roomWait.boardID == boardID) {
                        return roomWait;
                    }
                }
            }
        }
        return null;
    }
    
    public static void update() {
        for (RoomInfo roomInfo : entrys) {
            int nPlayer = 0;
            int nMax = 0;
            for (RoomWait wait : roomInfo.roomWaits) {
                if (!wait.started) {
                    nPlayer += wait.numPlayer;
                } else {
                    nPlayer += wait.playerLimit;
                }
                nMax += wait.playerLimit;
                if (!wait.lock) {
                    wait.update();
                }
            }
            int percent = (int) (((float)nPlayer / (float)nMax) * 100F);
            roomInfo.stat = percent > 70 ? 0 : percent > 50 ? 1 : 2;
        }
    }
    
}
