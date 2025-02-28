package mobiarmy.server;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Văn Tú
 */
public class Exp {

    private int level;
    public int exp;
    
    public Exp(int level, int exp) {
        this.level = level;
        this.exp = exp;
    }
    
    public static Exp[] entrys;
    
    public static void loadExp() throws SQLException {
        ArrayList<DBManager.DataRow> rows = Server.dbManager.selectColumnName("SELECT * FROM exp");
        entrys = new Exp[rows.size()];
        for (int i = 0; i < rows.size(); i++) {
            Exp o = new Exp(rows.get(i).getInt("level"), rows.get(i).getInt("exp"));
            entrys[i] = o;
        }
    }
    
    public static int getLevelExp(int exp) {
        for (Exp entry : entrys) {
            if (entry.exp > exp) {
                return entry.level;
            }
        }
        return entrys[entrys.length - 1].level;
    }
    
    public static Exp get(int level) {
        for (Exp entry : entrys) {
            if (entry.level == level) return entry;
        }
        return null;
    }
    
    public static int getPercentExp(int level, int exp) {
        return (int) (100f / (float)get(level).exp * (float)exp);
    }
}
