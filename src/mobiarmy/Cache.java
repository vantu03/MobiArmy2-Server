package mobiarmy;

import com.google.gson.Gson;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import mobiarmy.server.DBManager;
import mobiarmy.server.GameData;

/**
 *
 * @author Văn Tú
 */
public class Cache {
    public static void main(String[] args) {
        
        
            // Initialize the database connection
            DBManager army = new DBManager("jdbc:mysql://localhost:3306/army", "root", "vantu");
            DBManager dbarmy2 = new DBManager("jdbc:mysql://localhost:3306/dbarmy2", "root", "vantu");

            try {
                if (false) {
                    HashMap<String, Integer> map = new HashMap<>();
                    DataInputStream msg = new DataInputStream(new ByteArrayInputStream(GameData.getCache("equipdata2.rs")));


                    // Bỏ qua các byte đầu tiên
                    for (int i = 0; i < 36; i++) {
                        byte b = msg.readByte();
                        if (b == 10) {
                            // Nếu byte là 10 thì xử lý gì đó (chưa xác định)
                        }
                        //System.out.println("Byte " + i + ": " + b); // In giá trị byte ra màn hình
                    }

                    int nglass = msg.readByte();
                    System.out.println("nglass: " + nglass);

                    for (int i = 0; i < nglass; i++) {
                        int glass = msg.readByte();
                        int maxDame = msg.readShort();
                        System.out.println("glass: " + glass + ", maxDame: " + maxDame);

                        // Insert glass data into MySQL
                        HashMap<String, Object> glassData = new HashMap<>();
                        glassData.put("id", glass);
                        glassData.put("att", maxDame);
                        //army.insertWithMap("glass", glassData);
                        army.updateWithMap("UPDATE glass _SET_ WHERE id = ?", glassData, glass);

                        int ntype = msg.readByte();
                        System.out.println("ntype: " + ntype);

                        for (int j = 0; j < ntype; j++) {
                            int type = msg.readByte();
                            byte nid = msg.readByte();
                            System.out.println("type: " + type + ", nid: " + nid);

                            for (int k = 0; k < nid; k++) {
                                int equipId = msg.readShort();
                                int bullet = type == 0 ? msg.readByte() : -1;
                                int icon = msg.readShort();
                                int level = msg.readByte();
                                System.out.println("equipId: " + equipId + ", icon: " + icon + ", level: " + level);

                                // Build dimensions JSON
                                short x[] = new short[6];
                                short y[] = new short[6];
                                byte w[] = new byte[6];
                                byte h[] = new byte[6];
                                byte dx[] = new byte[6];
                                byte dy[] = new byte[6];
                                for (int l = 0; l < 6; l++) {
                                    x[l] = msg.readShort();
                                    y[l] = msg.readShort();
                                    w[l] = msg.readByte();
                                    h[l] = msg.readByte();
                                    dx[l] = msg.readByte();
                                    dy[l] = msg.readByte();
                                }

                                // Build abilities JSON
                                byte[] inv_ability = new byte[5];
                                byte[] inv_percen = new byte[5];
                                for (int m = 0; m < 5; m++) {
                                    inv_ability[m] = msg.readByte();
                                    inv_percen[m] = msg.readByte();
                                }

                                // Insert equip data into MySQL
                                HashMap<String, Object> equipData = new HashMap<>();
                                equipData.put("glassId", glass);
                                equipData.put("id", equipId);
                                equipData.put("type", type);
                                equipData.put("bullet", bullet);
                                equipData.put("icon", icon);
                                equipData.put("level", level);
                                equipData.put("x", new Gson().toJson(x));
                                equipData.put("y", new Gson().toJson(y));
                                equipData.put("w", new Gson().toJson(w));
                                equipData.put("h", new Gson().toJson(h));
                                equipData.put("dx", new Gson().toJson(dx));
                                equipData.put("dy", new Gson().toJson(dy));
                                equipData.put("inv_ability", new Gson().toJson(inv_ability));
                                equipData.put("inv_percen", new Gson().toJson(inv_percen));
                                equipData.put("vip", 0);
                                equipData.put("date", 30);
                                equipData.put("luong", -1);
                                equipData.put("xu", -1);
                                equipData.put("name", null);

                                army.insertWithMap("equip", equipData);
                                //army.updateWithMap("UPDATE equip _SET_ WHERE glassID = ? and type = ? and id = ?", equipData, glass, type, equipId);
                                if (map.containsKey(glass+" "+equipId)) {
                                    System.err.println("Không có dòng nào được tác động\n"+ equipData.toString());
                                    System.exit(0);
                                }
                                map.put(glass+" "+equipId, glass);
                            }
                        }
                    }
                } else {
                    ArrayList<DBManager.DataRow> rows;
                    rows = dbarmy2.selectColumnName("SELECT * FROM equip WHERE onSale = 1");
                    for (DBManager.DataRow row : rows) {
//                        HashMap<String, Object> equip = new HashMap<>();
//                        equip.put("glassID", row.getByte("nv"));
//                        equip.put("equipId", row.getShort("equipId"));
//                        equip.put("type", row.getByte("equipType"));
//                        equip.put("name", row.getString("name"));
//                        equip.put("xu", row.getInt("giaXu"));
//                        equip.put("luong", row.getInt("giaLuong"));
//                        equip.put("vip", row.getBoolean("isSet"));
//                        equip.put("date", row.getByte("hanSD"));
//                        equip.put("slot", 3);
//                        equip.put("inv_ability", row.getString("addPN"));
//                        equip.put("inv_percen", row.getString("addPN100"));
//                        if (row.getBoolean("isSet")) {
//                            equip.put("data", new Gson().toJson(new short[]{(short)(row.getShort("equipId") + (short)2), (short)row.getShort("equipId"), (short)(row.getShort("equipId") +(short)1), (short)-1, (short)-1}));
//                        }
//                        army.insertWithMap(
//                                "shop_equipment",
//                                equip
//                        );
                        army.update("UPDATE equip SET name = ?, date = ?, luong = ?, xu = ?, vip = ? WHERE glassID  = ? AND type = ? AND id = ? ",
                                row.getString("name"),
                                row.getByte("hanSD"),
                                row.getInt("giaLuong"),
                                row.getInt("giaXu"),
                                row.getBoolean("isSet"),
                                row.getByte("nv"),
                                row.getByte("equipType"),
                                row.getShort("equipId"));
                    }
//                    rows = dbarmy2.selectColumnName("SELECT * FROM nhanvat");
//                    for (DBManager.DataRow row : rows) {
//                        army.update("UPDATE glass SET name = ? WHERE id  = ?", row.getString("name"), row.getByte("nhanvat_id ") - 1);
//                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}
