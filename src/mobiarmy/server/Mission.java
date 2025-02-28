package mobiarmy.server;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Văn Tú
 */
public class Mission {

    public int id;
    public int level;
    public String name;
    public int require;
    public int have;
    public String reward;
    public int xu;
    public int luong;
    public int exp;
    public int cup;
    
    public boolean isComplete;
    public boolean isGetReward;
    
    public static Mission entrys[];
    
    public static void loadMession() throws SQLException {
        
        ArrayList<DBManager.DataRow> rows = Server.dbManager.selectColumnName("SELECT * FROM mission");
        entrys = new Mission[rows.size()];
        for (int i = 0; i < rows.size(); i++) {
            entrys[i] = new Mission();
            entrys[i].id = rows.get(i).getByte("id");
            entrys[i].level = rows.get(i).getByte("level");
            entrys[i].name = rows.get(i).getString("name");
            entrys[i].require = rows.get(i).getInt("require");
            entrys[i].reward = rows.get(i).getString("reward");
            entrys[i].xu = rows.get(i).getInt("xu");
            entrys[i].luong = rows.get(i).getInt("luong");
            entrys[i].exp = rows.get(i).getInt("exp");
            entrys[i].cup = rows.get(i).getInt("cup");
        }
    }
    
    public static Mission get(int id, int level) {
        for (Mission mission : entrys) {
            if (mission.id == id && (level == -1 || mission.level == level)) {
                return mission;
            }
        }
        return null;
    }
    
    public Mission deepCopy() {
        Mission copy = new Mission();
        copy.id = this.id;
        copy.level = this.level;
        copy.name = this.name;
        copy.require = this.require;
        copy.have = this.have;
        copy.reward = this.reward;
        copy.xu = this.xu;
        copy.luong = this.luong;
        copy.exp = this.exp;
        copy.cup = this.cup;
        
        
        copy.isComplete = this.isComplete;
        copy.isGetReward = this.isGetReward;
        return copy;
    }
    
}
