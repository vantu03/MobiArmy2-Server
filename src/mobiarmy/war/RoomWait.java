package mobiarmy.war;

import java.io.IOException;
import java.util.ArrayList;
import mobiarmy.Util;
import mobiarmy.server.Bot;
import mobiarmy.server.Map;
import mobiarmy.server.SessionManager;
import static mobiarmy.server.Text.__;
import mobiarmy.server.User;

/**
 *
 * @author Văn Tú
 */
public class RoomWait {
    
    public byte roomID;
    public byte type;
    public byte boardID;
    public byte mapID;
    public ArrayList<Byte> maps;
    public int minMoney;
    public int maxMoney;
    public int money;
    public String name = "";
    public String pass = "";
    private final byte maxPlayer;
    public byte playerLimit;
    public byte numPlayer;
    public byte mode;
    public boolean started;
    public int userID;
    public User[] players;
    public long waitStart;
    public MapData mapData;
    public long noWaitTime = -1;
    public boolean lock;

    public RoomWait(byte roomID, byte type, byte boardID, byte[] map, int minMoney, int maxMoney, byte maxPlayer) {
        this.roomID = roomID;
        this.type = type;
        this.boardID = boardID;
        this.maps = new ArrayList<>();
        for (int i = 0; i < map.length; i++) {
            this.maps.add(map[i]);
        }
        this.mapID = this.maps.get(0);
        this.minMoney = this.money = minMoney;
        this.maxMoney = maxMoney;
        this.playerLimit = this.maxPlayer = maxPlayer;
        this.players = new User[Room.MAX_PLAYER];
        this.userID = -1;
        this.mapData = null;
        this.lock = false;
    }
    
    public void reset() {
        this.money = this.minMoney;
        this.name = "";
        this.pass = "";
        this.playerLimit = this.maxPlayer;
    }
    
    public void join(User user, String pass) {
        if (!this.pass.isEmpty() && !this.pass.equals(pass)) {
            if (user.session != null) {
                user.session.sessionHandler.log(__("Mật khẩu không đúng"));
            }
        } else if (this.numPlayer >= this.playerLimit) {
            if (user.session != null) {
                user.session.sessionHandler.log(__("Khu vực đã đầy"));
            }
        } else if (this.money > user.xu) {
            if (user.session != null) {
                user.session.sessionHandler.log(__("Bạn không đủ tiền cược"));
            }
        } else if (this.started) {
            if (user.session != null) {
                user.session.sessionHandler.log(__("Khu vực đã bắt đầu"));
            }
        } else {
            byte index = generateIndex();
            if (index != -1) {
                for (User player : this.players) {
                    if (player != null && player.session != null) {
                        player.session.sessionHandler.addPlayer(index, user);
                    }
                }
                this.players[index] = user;
                this.numPlayer++;
                user.roomWait = this;
                user.index = index;
                user.team = (byte) (index % 2 == 0 ? 1 : 2);
                if (this.userID == -1) {
                    this.nextOwner();
                }
            }
            if (user.session != null) {
                user.session.sessionHandler.updateRoomWait(this);
                user.session.sessionHandler.changeMap(this.mapID);
                user.session.sessionHandler.loadRoomWait(this);
            }
        }
    }
    
    public void nextOwner() {
        for (User player : this.players) {
            if (player != null) {
                player.ready = false;
                this.userID = player.id;
                this.noWaitTime = System.currentTimeMillis();
                return;
            }
        }
        this.noWaitTime = -1;
        this.userID = -1;
    }
    
    public byte generateIndex() {
        for (byte i = 0; i < this.players.length; i++) {
            if (this.players[i] == null) {
                return i;
            }
        }
        return -1;
    }
    
    public User getUser(int userID) {
        for (User player : this.players) {
            if (player != null && player.id == userID) {
                return player;
            }
        }
        return null;
    }
    
    public void leave(User user) {
        if (this.started) {
            this.mapData.leave(user.index);
        }
        for (int i = 0; i < this.players.length; i++) {
            if (this.players[i] != null && this.players[i].id == user.id) {
                this.players[i].ready = false;
                this.players[i].roomWait = null;
                this.players[i].index = -1;
                this.players[i] = null;
                this.numPlayer--;
            }
        }
        if (this.numPlayer == 0) {
            this.reset();
        }
        if (this.userID == user.id) {
            this.nextOwner();
        }
        for (User player : this.players) {
            if (player != null && player.session != null) {
                player.session.sessionHandler.leaveRoomWait(user.id, this);
            }
        }
    }
    
    public void changeTeam(User user) {
        if (!user.ready) {
            for (int i = 0; i < this.players.length; i++) {
                if (this.players[i] == null) {
                    if ((user.team == 1 && i % 2 != 0) || (user.team == 2 && i % 2 == 0)) {
                        this.players[user.index] = null;
                        this.players[user.index = (byte) i] = user;
                        user.team = (byte) (i % 2 == 0 ? 1 : 2);
                        for (User player : this.players) {
                            if (player != null && player.session != null) {
                                player.session.sessionHandler.changeTeam(user.id, user.index);
                            }
                        }
                        return;
                    }
                }
            }
        }
        if (user.session != null) {
            user.session.sessionHandler.log(__("Không thể đổi phe"));
        }
    }
    
    public void ready(User user) {
        if (this.userID != user.id) {
            if (this.waitStart > System.currentTimeMillis()) {
                if (user.session != null) {
                    user.session.sessionHandler.log(String.format(__("Vui lòng chờ %d giây."), (this.waitStart - System.currentTimeMillis()) / 1000L + 1));
                }
                return;
            }
            //co loi slot item khong
            int slot = user.getSlotItemError();
            if (slot != -1) {
                if (user.session != null) {
                    user.session.sessionHandler.log(String.format(__("Lỗi item slot %d xin chọn lại!."), slot));
                }
                return;
            }
            user.ready = !user.ready;
            for (User player : this.players) {
                if (player != null && player.session != null) {
                    player.session.sessionHandler.ready(user);
                }
            }
        }
    }
    
    public void kick(User user, int userID, String str) {
        if (user == null || (user.id == user.id && user.id != userID)) {
            User player = this.getUser(userID);
            if (player != null && (!player.ready || user == null)) {
                for (User player2 : this.players) {
                    if (player2 != null && player2.session != null) {
                        player2.session.sessionHandler.kickRoomWait(player.index, userID, str);
                    }
                }
                this.leave(player);
            } 
        }
    }
    
    public void setPlayerLimit(User user, byte limit) {
        if (user == null || user.id == user.id) {
            if (limit > 0 && limit <= this.maxPlayer && limit % 2 == 0) {
                //if (this.numPlayer <= limit) {
                    this.playerLimit = limit;
                //}
            }
        }
    }
    
    public void changeMoney(User user, int money) {
        if (user == null || user.id == user.id) {
            if (this.minMoney == this.maxMoney) {
                if (user != null && user.session != null) {
                    user.session.sessionHandler.log(__("Không thể đặt tiền cược trong khu vực này"));
                }
            } else if (this.minMoney == 0 && this.maxMoney < money) {
                if (user != null && user.session != null) {
                    user.session.sessionHandler.log(String.format(__("Tiền cược phải nhỏ hơn %s xu. Nếu muốn cược lớn hơn mời qua khu vực cấp cao hơn."), Util.formatNum(this.maxMoney)));
                }
            } else if (this.minMoney > money || this.maxMoney < money) {
                if (user != null && user.session != null) {
                    user.session.sessionHandler.log(String.format(__("Tiền cược phải từ %s xu đến %s xu."), Util.formatNum(this.minMoney), Util.formatNum(this.maxMoney)));
                }
            } else {
                this.money = money;
                for (User player : this.players) {
                    if (player != null) {
                        player.ready = false;
                        if (player.session != null) {
                            player.session.sessionHandler.setMoneyRoomWait(this.money);
                        }
                    }
                }
            }
        }
    }
    
    public void changePass(User user, String pass) {
        if (user == null || user.id == user.id) {
            if (pass.length() > 100) {
                if (user != null && user.session != null) {
                    user.session.sessionHandler.log(__("Mật khẩu quá dài"));
                }
            } else {
                this.pass = pass;
            }
        }
    }
    
    public void changeName(User user, String name) {
        if (user == null || user.id == user.id) {
            if (name.length() > 100) {
                if (user != null && user.session != null) {
                    user.session.sessionHandler.log(__("Tên phòng quá dài"));
                }
            } else {
                this.name = name;
            }
        }
    }
    
    public void changeMap(User user, byte mapID) {
        if (user == null || user.id == user.id) {
            if (user != null && !this.maps.contains(mapID)) {
                if (user.session != null) {
                    if (this.maps.size() < 5) {
                        StringBuilder builder = new StringBuilder();
                        for (int i = 0; i < this.maps.size(); i++) {
                            builder.append(Map.get(this.maps.get(i)).name);
                            if (i < this.maps.size() - 1) {
                                builder.append(", ");
                            }
                        }
                        user.session.sessionHandler.log(String.format(__("Chỉ chọn được map %s"), builder.toString()));
                    } else {
                        user.session.sessionHandler.log(__("Không được chọn map này."));
                    }
                }
            } else {
                this.mapID = mapID;
                for (User player : this.players) {
                    if (player != null) {
                        player.ready = false;
                        if (player.session != null) {
                            player.session.sessionHandler.changeMap(this.mapID);
                            if (this.mapID == 27) {
                                player.session.sessionHandler.loadRoomWait(this);
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void invitePlayer(User user, int userID) {
        if (user == null || user.id == this.userID) {
            User player = SessionManager.findUserById(userID);
            if (player == null || player.session == null) {
                if (user != null && user.session != null) {
                    user.session.sessionHandler.log(__("Người chơi đã offline rồi."));
                }
            } else if (player.roomWait != null) {
                if (user != null && user.session != null) {
                    user.session.sessionHandler.log(__("Người chơi đã tham gia phòng khác"));
                }
            } else {
                player.hasBeenInvited = true;
                if (!player.hasBeenInvited && player.session != null) {
                    player.session.sessionHandler.inviteJoinRoomWait(String.format(__("%s đã mời bạn chơi"), user == null ? "có người" : user.name), this);
                }
            }
        }
    }
    
    public void inviteBot(User user, int userID) {
        if (user == null || user.id == this.userID) {
            Bot bot = Bot.findById(userID);
            if (bot == null) {
                if (user != null && user.session != null) {
                    user.session.sessionHandler.log(__("Người chơi đã offline rồi."));
                }
            } else if (bot.roomWait != null) {
                if (user != null && user.session != null) {
                    user.session.sessionHandler.log(__("Người chơi đã tham gia phòng khác"));
                }
            } else {
                bot.invited = new Object[]{this.roomID, this.boardID, this.pass};
            }
        }
    }
    
    public void startGame(User user ) {
        if (this.started) return;
        if (user == null || user.id == this.userID) {
            if (this.waitStart > System.currentTimeMillis()) {
                if (user != null && user.session != null) {
                    user.session.sessionHandler.log(String.format(__("Vui lòng chờ %d giây."), (this.waitStart - System.currentTimeMillis()) / 1000L + 1));
                }
                return;
            }
            
//            this.waitStart = System.currentTimeMillis() + 5000L;
            int ready = 0, team1 = 0, team2 = 0;
            User notReady = null;
            for (User player : this.players) {
                if (player != null) {
                    if(player.id == this.userID || player.ready) {
                        if (player.ready) {
                            ready++;
                        }
                    } else if (notReady == null) {
                        notReady = player;
                    }
                    if (player.team == 1) {
                        team1++;
                    }
                    if (player.team == 2) {
                        team2++;
                    }
                }
            }
            if (ready == 0 && this.type != 5) {
                if (user != null && user.session != null) {
                    user.session.sessionHandler.log(__("Mọi người chưa sẵn sàng"));
                }
                return;
            }
            if (notReady != null) {
                if (user != null && user.session != null) {
                    user.session.sessionHandler.log(String.format(__("Còn %s chưa sẵn sàng."), notReady.name));
                }
                return;
            }
            if (team1 != team2 && this.type != 5) {
                if (user != null && user.session != null) {
                    user.session.sessionHandler.log(__("Số lượng 2 bên chưa ngang nhau"));
                }
                return;
            }
            for (User player : this.players) {
                if (player != null) {
                    byte slot = player.getSlotItemError();
                    if (slot != -1) {
                        if (player.session != null) {
                            player.session.sessionHandler.log(String.format(__("Lỗi item slot %d xin chọn lại!."), slot));
                        }
                        if (user != null && user.session != null) {
                            user.session.sessionHandler.log(String.format(__("%s lỗi chọn item."), player.name));
                        }
                        return;
                    }
                }
            }
            try {
                byte mapIDLoad = this.mapID;
                if (mapIDLoad == 27) {
                    do {
                        mapIDLoad = this.maps.get(Util.nextInt(this.maps.size()));
                    } while (mapIDLoad == 27);
                    for (User player : this.players) {
                        if (player != null && player.session != null) {
                            player.session.sessionHandler.changeMap(mapIDLoad);
                        }
                    }
                }
                this.mapData = new MapData(this, mapIDLoad);
                this.mapData.startGame(this.players);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void chat(User user, String str) {
        if (!this.started) {
            for (User player : this.players) {
                if (player != null && player.session != null) {
                    player.session.sessionHandler.chat(user.id, str);
                }
            }
        } else {
            this.mapData.chat(user.id, str);
        }
    }
    
    public void endGame() {
        this.started = false;
        this.waitStart = System.currentTimeMillis() + 10000L;
        this.noWaitTime = System.currentTimeMillis();
    }
    
    public void update() {
        if (this.mapData != null) {
            this.mapData.update();
        }
        if (!this.started) {
            if (this.numPlayer > 0 && System.currentTimeMillis() - this.noWaitTime > 120000) {
                this.kick(null, this.userID, __("Không start ván"));
            }
        }
    }
    
}
