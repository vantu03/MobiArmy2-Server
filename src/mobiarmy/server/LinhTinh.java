package mobiarmy.server;

import com.google.gson.Gson;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Văn Tú
 */
public class LinhTinh {
    
    public byte id;
    public String name;
    public String detail;
    public short[] ability;
    public int xu;
    public int luong;
    public int num;
    public byte date;
    public boolean isSelectNum;
    
    public LinhTinh(){};
    
    public static LinhTinh[] entrys;
    
    public static void loadLinhTinh() throws SQLException {
        ArrayList<DBManager.DataRow> rows = Server.dbManager.selectColumnName("SELECT * FROM linhtinh");
        entrys = new LinhTinh[rows.size()];
        for (int i = 0; i < rows.size(); i++) {
            entrys[i] = new LinhTinh();
            entrys[i].id = rows.get(i).getByte("id");
            entrys[i].name = rows.get(i).getString("name");
            entrys[i].detail = rows.get(i).getString("detail");
            entrys[i].ability = new Gson().fromJson(rows.get(i).getString("ability"), short[].class);
            entrys[i].xu = rows.get(i).getInt("xu");
            entrys[i].luong = rows.get(i).getInt("luong");
            entrys[i].date = rows.get(i).getByte("date");
        }
    }
    
    public static LinhTinh get(int id) {
        for (LinhTinh entry : entrys) {
            if (entry.id == id) return entry;
        }
        return null;
    }
    
    public LinhTinh deepCopy() {
        LinhTinh copy = new LinhTinh();
        copy.id = this.id;
        copy.name = this.name;
        copy.detail = this.detail;
        copy.ability = this.ability.clone();
        copy.xu = this.xu;
        copy.luong = this.luong;
        copy.num = this.num;
        copy.date = this.date;
        copy.isSelectNum = this.isSelectNum;
        return copy;
    }
}
