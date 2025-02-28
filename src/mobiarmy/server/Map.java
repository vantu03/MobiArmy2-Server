package mobiarmy.server;

import com.google.gson.Gson;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Văn Tú
 */
public class Map {
    
    public byte id;
    public String name;
    public String file;
    public Data data;
    public short bg;
    public short mapAddY;
    public short cl2AddY;
    public short inWaterAddY;
    public short bullEffShower;
    
    public class Data {
        public short width;
        public short height;
        public Brick bricks[];
        public Point points[];
    }
    
    public class Brick {
        public int id;
        public int x;
        public int y;
    }
    
    public class Point {
        public short x;
        public short y;
    }
    
    public static Map[] entrys;
    
    public static void loadMap() throws SQLException {
        ArrayList<DBManager.DataRow> rows = Server.dbManager.selectColumnName("SELECT * FROM map");
        entrys = new Map[rows.size()];
        for (int i = 0; i < rows.size(); i++) {
            entrys[i] = new Map();
            entrys[i].id = rows.get(i).getByte("id");
            entrys[i].name = rows.get(i).getString("name");
            entrys[i].data = new Gson().fromJson(rows.get(i).getString("data"), Data.class);
            entrys[i].file = rows.get(i).getString("file");
            entrys[i].bg = rows.get(i).getShort("bg");
            entrys[i].mapAddY = rows.get(i).getShort("mapAddY");
            entrys[i].bullEffShower = rows.get(i).getShort("bullEffShower");
            entrys[i].inWaterAddY = rows.get(i).getShort("inWaterAddY");
            entrys[i].cl2AddY = rows.get(i).getShort("cl2AddY");
        }
    }
    
    public static Map get(int mapID) {
        for (Map entry : entrys) {
            if (entry.id == mapID) {
                return entry;
            }
        }
        return null;
    }
}
