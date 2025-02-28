package mobiarmy.server;

import com.google.gson.Gson;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Văn Tú
 */
public class SessionManager {
    
    // Sử dụng HashMap để lưu trữ session, user và character
    private static final ArrayList<Session> sessions = new ArrayList<>();
    private static final ArrayList<User> users = new ArrayList<>();
    private static final HashMap<Integer, User> users_id = new HashMap<>();
    private static final HashMap<String, User> users_name = new HashMap<>();
    
    // Đối tượng khóa cho các thao tác đồng bộ hóa
    private static final Object lock = new Object();

    // Thêm session mới
    public static void addSession(Session session) {
        synchronized (lock) {
            sessions.add(session);
        }
    }

    // Thêm user mới
    public static void addUser(User user) {
        synchronized (lock) {
            users_id.put(user.id, user);
            users_name.put(user.name, user);
            users.add(user);
        }
    }
    
    public static ArrayList<User> generateUsers() {
        synchronized (lock) {
            return new ArrayList<>(users);
        }
    }


    // Xóa session theo ID
    public static void removeSession(Session session) {
        synchronized (lock) {
            sessions.remove(session);
        }
    }

    // Xóa user theo ID hoặc username
    public static void removeUserById(int id) {
        synchronized (lock) {
            User user = users_id.remove(id);
            if (user != null) {
                users_name.remove(user.name);
                users.remove(user);
            }
        }
    }

    public static void removeUserByName(String username) {
        synchronized (lock) {
            User user = users_name.remove(username);
            if (user != null) {
                users_id.remove(user.id);
                users.remove(user);
            }
        }
    }

    // Tìm session theo ID
    public static Session findSessionById(int sessionId) {
        synchronized (lock) {
            return sessions.get(sessionId);
        }
    }

    // Tìm user theo ID hoặc username
    public static User findUserById(int userId) {
        synchronized (lock) {
            return users_id.get(userId);
        }
    }

    public static User findUserByName(String username) {
        synchronized (lock) {
            return users_name.get(username);
        }
    }
    
    // Phương thức trả về kích thước của sessions
    public static int getSessionsSize() {
        return sessions.size();
    }

    // Phương thức trả về kích thước của users_id
    public static int getUsersIdSize() {
        return users_id.size();
    }

    // Phương thức trả về kích thước của users_username
    public static int getUsersUsernameSize() {
        return users_name.size();
    }
    
    public static void loadUser() throws SQLException {
        ArrayList<DBManager.DataRow> dbusers = Server.dbManager.selectColumnName("SELECT * FROM user");
        for (DBManager.DataRow dbuser : dbusers) {
            ArrayList<DBManager.DataRow> user_ = Server.dbManager.selectColumnName("SELECT * FROM user_ WHERE user_id = ?", dbuser.getInt("id"));
            ArrayList<DBManager.DataRow> user_glass = Server.dbManager.selectColumnName("SELECT * FROM user_glass WHERE user_id = ?", dbuser.getInt("id"));
            ArrayList<DBManager.DataRow> user_item = Server.dbManager.selectColumnName("SELECT * FROM user_item WHERE user_id = ?", dbuser.getInt("id"));
            ArrayList<DBManager.DataRow> user_equip = Server.dbManager.selectColumnName("SELECT * FROM user_equip WHERE user_id = ?", dbuser.getInt("id"));
            ArrayList<DBManager.DataRow> user_linhtinh = Server.dbManager.selectColumnName("SELECT * FROM user_linhtinh WHERE user_id = ?", dbuser.getInt("id"));
            ArrayList<DBManager.DataRow> user_mission = Server.dbManager.selectColumnName("SELECT * FROM user_mission WHERE user_id = ?", dbuser.getInt("id"));
            ArrayList<DBManager.DataRow> user_friend = Server.dbManager.selectColumnName("SELECT * FROM user_friend WHERE user_id = ?", dbuser.getInt("id"));
            
            User user = new User(dbuser.getInt("id"), dbuser.getString("username"));
            for (DBManager.DataRow row : user_) {
                user.name =row.getString("name");
                user.xu = row.getInt("xu");
                user.luong = row.getInt("luong");
                user.cup = row.getInt("cup");
                user.selectGlass = row.getByte("glass");
            }
            if (!user_glass.isEmpty()) {
                for (DBManager.DataRow row : user_glass) {
                    Glass glass = user.getGlass(row.getByte("glassID"));
                    if (glass != null) {
                        glass.isOpen = true;
                        glass.ability = new Gson().fromJson(row.getString("ability"), int[].class);
                        glass.equipID = new Gson().fromJson(row.getString("equipID"), short[].class);
                        glass.data = new Gson().fromJson(row.getString("data"), short[].class);
                        glass.point = row.getShort("point");
                        glass.level = row.getByte("level") & 0xFF;
                        glass.exp = row.getInt("exp");
                    }
                }
            }
            if (!user_item.isEmpty()) {
                user.items.clear();
                for (DBManager.DataRow row : user_item) {
                    Item item = Item.get(row.getByte("item_id")).deepCopy();
                    item.num = row.getByte("num");
                    user.items.add(item);
                }
            }
            if (!user_equip.isEmpty()) {
                user.equips.clear();
                for (DBManager.DataRow row : user_equip) {
                    Equip equip = Equip.get(row.getByte("glassID"), row.getShort("equipID")).deepCopy();
                    equip.level2 = row.getByte("level2");
                    equip.inv_ability = new Gson().fromJson(row.getString("inv_ability"), byte[].class);
                    equip.inv_percen = new Gson().fromJson(row.getString("inv_percen"), byte[].class);
                    equip.slot = new Gson().fromJson(row.getString("slot"), short[].class);
                    equip.dbKey = row.getInt("dbKey");
                    equip.isUse = row.getBoolean("isUse");
                    equip.renewalDate = row.getLong("renewalDate");
                    user.equips.add(equip);
                }
            }
            if (!user_linhtinh.isEmpty()) {
                user.linhtinhs.clear();
                for (DBManager.DataRow row : user_linhtinh) {
                    LinhTinh item = LinhTinh.get(row.getByte("linhtinh_id")).deepCopy();
                    item.num = row.getInt("num");
                    user.linhtinhs.add(item);
                }
            }
            if (!user_mission.isEmpty()) {
                user.missions.clear();
                for (DBManager.DataRow row : user_mission) {
                    Mission mission = Mission.get(row.getByte("mission_id"), row.getByte("level"));
                    mission.have = row.getInt("have");
                    mission.isComplete = row.getBoolean("isComplete");
                    mission.isGetReward = row.getBoolean("isGetReward");
                    user.missions.add(mission);
                }
            }
            if (!user_friend.isEmpty()) {
                user.friends.clear();
                for (DBManager.DataRow row : user_friend) {
                    Integer friend_id = row.getInt("friend_id");
                    user.friends.add(friend_id);
                }
            }
            SessionManager.addUser(user);
        }
    }
    
    
    public static void saveUsers() throws SQLException {
        // Make a copy of all users from SessionManager to avoid thread blockage
        ArrayList<User> usersCopy;

        synchronized (lock) {
            // Copy all users to prevent blocking the main thread
            usersCopy = new ArrayList<>(SessionManager.users);
        }

        // Iterate through the copied user list and save each user to the database
        for (User user : usersCopy) {
            // First, delete the existing data for this user
            Server.dbManager.update("DELETE FROM user_ WHERE user_id = ?", user.id);
            Server.dbManager.update("DELETE FROM user_glass WHERE user_id = ?", user.id);
            Server.dbManager.update("DELETE FROM user_item WHERE user_id = ?", user.id);
            Server.dbManager.update("DELETE FROM user_equip WHERE user_id = ?", user.id);
            Server.dbManager.update("DELETE FROM user_linhtinh WHERE user_id = ?", user.id);
            Server.dbManager.update("DELETE FROM user_mission WHERE user_id = ?", user.id);
            Server.dbManager.update("DELETE FROM user_friend WHERE user_id = ?", user.id);

            // Now, insert the main user details into 'user_' table
            HashMap<String, Object> userValues = new HashMap<>();
            userValues.put("user_id", user.id);
            userValues.put("name", user.name);
            userValues.put("xu", user.xu);
            userValues.put("luong", user.luong);
            userValues.put("cup", user.cup);
            userValues.put("glass", user.selectGlass);

            // Insert user data into 'user_' table
            Server.dbManager.insertWithMap("user_", userValues);

            // Insert user glasses into 'user_glass' table
            for (Glass glass : user.glass) {
                if (glass.isOpen) {
                    HashMap<String, Object> glassValues = new HashMap<>();
                    glassValues.put("user_id", user.id);
                    glassValues.put("glassID", glass.id);
                    glassValues.put("ability", new Gson().toJson(glass.ability));
                    glassValues.put("equipID", new Gson().toJson(glass.equipID));
                    glassValues.put("data", new Gson().toJson(glass.data));
                    glassValues.put("point", glass.point);
                    glassValues.put("level", glass.level);
                    glassValues.put("exp", glass.exp);

                    // Insert into 'user_glass' table
                    Server.dbManager.insertWithMap("user_glass", glassValues);
                }
            }

            // Insert user items into 'user_item' table
            for (Item item : user.items) {
                HashMap<String, Object> itemValues = new HashMap<>();
                itemValues.put("user_id", user.id);
                itemValues.put("item_id", item.id);
                itemValues.put("num", item.num);

                // Insert into 'user_item' table
                Server.dbManager.insertWithMap("user_item", itemValues);
            }

            // Insert user equips into 'user_equip' table
            for (Equip equip : user.equips) {
                HashMap<String, Object> equipValues = new HashMap<>();
                equipValues.put("user_id", user.id);
                equipValues.put("glassID", equip.glassID);
                equipValues.put("equipID", equip.id);
                equipValues.put("level2", equip.level2);
                equipValues.put("inv_ability", new Gson().toJson(equip.inv_ability));
                equipValues.put("inv_percen", new Gson().toJson(equip.inv_percen));
                equipValues.put("slot", new Gson().toJson(equip.slot));
                equipValues.put("dbKey", equip.dbKey);
                equipValues.put("isUse", equip.isUse);
                equipValues.put("renewalDate", equip.renewalDate);

                // Insert into 'user_equip' table
                Server.dbManager.insertWithMap("user_equip", equipValues);
            }

            // Insert user linh tinh into 'user_linhtinh' table
            for (LinhTinh linhtinh : user.linhtinhs) {
                HashMap<String, Object> linhTinhValues = new HashMap<>();
                linhTinhValues.put("user_id", user.id);
                linhTinhValues.put("linhtinh_id", linhtinh.id);
                linhTinhValues.put("num", linhtinh.num);

                // Insert into 'user_linhtinh' table
                Server.dbManager.insertWithMap("user_linhtinh", linhTinhValues);
            }

            // Insert user missions into 'user_mission' table
            for (Mission mission : user.missions) {
                HashMap<String, Object> missionValues = new HashMap<>();
                missionValues.put("user_id", user.id);
                missionValues.put("mission_id", mission.id);
                missionValues.put("level", mission.level);
                missionValues.put("have", mission.have);
                missionValues.put("isComplete", mission.isComplete);
                missionValues.put("isGetReward", mission.isGetReward);

                // Insert into 'user_mission' table
                Server.dbManager.insertWithMap("user_mission", missionValues);
            }

            // Insert user friend into 'user_friend' table
            for (Integer friend : user.friends) {
                HashMap<String, Object> friendValues = new HashMap<>();
                friendValues.put("user_id", user.id);
                friendValues.put("friend_id", friend);

                // Insert into 'user_friend' table
                Server.dbManager.insertWithMap("user_friend", friendValues);
            }
            SessionManager.removeUserById(user.id);
        }
    }
    
    public static void messageWorld(String str) {
        ArrayList<Session> list;
        synchronized (lock) {
            list = new ArrayList<>(sessions);
        }
        for (Session s : list) {
            s.sessionHandler.messageWorld(str);
        }
    }

}
