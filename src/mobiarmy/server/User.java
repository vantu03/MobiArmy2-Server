package mobiarmy.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import mobiarmy.Util;
import static mobiarmy.server.Text.__;
import mobiarmy.war.RoomInfo;
import mobiarmy.war.RoomWait;

/**
 *
 * @author Văn Tú
 */
public class User {
    
    public Session session;
    public int id;
    public short clan;
    public String name;
    public Glass[] glass;
    public long xu;
    public long luong;
    public int cup;
    public byte selectGlass;
    public ArrayList<Item> items;
    public ArrayList<Equip> equips;
    public ArrayList<LinhTinh> linhtinhs;
    public ArrayList<Mission> missions;
    public ArrayList<Integer> friends;
    public ArrayList<Equip> generates;
    private Select select;
    private Confirm confirm;
    public RoomWait roomWait;
    public byte team;
    public boolean ready;
    public byte index;
    public byte[] setItems = new byte[]{0, 0, 1, 1, -1, -1, -1, -1};
    public boolean hasBeenInvited;
    public boolean lock;
    
    public User(int id, String name) {
        this.id = id;
        this.name = name;
        this.lock = false;
        this.session = null;
        this.glass = new Glass[10];
        this.equips = new ArrayList<>();
        this.missions = new ArrayList<>();
        this.friends = new ArrayList<>();
        this.linhtinhs = new ArrayList<>();
        for (int i = 0; i < this.glass.length; i++) {
            this.glass[i] = Glass.entrys[i].deepCopy();
            this.glass[i].user = User.this;
            this.glass[i].isOpen = this.glass[i].xu == 0 && this.glass[i].luong == 0;
            this.glass[i].updateAll();
        }
        this.items = new ArrayList<>();
        for (Item entry : Item.entrys) {
               entry = entry.deepCopy();
            if (entry.id < 2) {
                entry.num = 99;
            }
            this.items.add(entry);
        }
        for (Mission entry : Mission.entrys) {
            if (entry.level == 1) {
                entry = entry.deepCopy();
                entry.have = 0;
                this.missions.add(entry);
            }
        }
        this.xu = 1000;
        this.luong = 1000;
    }
    
    public synchronized boolean connect(Session session) {
        if (this.session != null) {
            return false;
        }
        this.session = session;
        session.user = this;
        return  true;
    }

    public Glass glass() {
        return this.glass[this.selectGlass];
    }
    
    public void addXu(int add, boolean flag) {
        if (this.xu + add > Integer.MAX_VALUE) {
            this.xu = Integer.MAX_VALUE;
        } else {
            this.xu = this.xu + add;
        }
        if (flag) {
            this.session.sessionHandler.setXuLuong(this.xu, this.luong);
        }
    }
    
    public void addCup(int add, boolean flag) {
        if (this.cup + add > Integer.MAX_VALUE) {
            this.cup = Integer.MAX_VALUE;
        } else {
            this.cup = this.cup + add;
        }
        if (flag) {
            this.session.sessionHandler.addCup(this.cup, add);
        }
    }

    public void addLuong(int add, boolean flag) {
        if (this.luong + add > Integer.MAX_VALUE) {
            this.luong = Integer.MAX_VALUE;
        } else {
            this.luong = this.luong + add;
        }

        if (flag) {
            this.session.sessionHandler.setXuLuong(this.xu, this.luong);
        }
    }
    
    public Glass getGlass(byte glassID) {
        for (Glass g : this.glass) {
            if (g != null && g.id == glassID) {
                return g;
            }
        }
        return null;
    }

    public void buyGlass(int select, int type) {
        if (select >= 0 && select < this.glass.length && !this.glass[select].isOpen) {
            if (type == 0) {
                if (this.glass[select].xu <= this.xu) {
                    this.addXu(- this.glass[select].xu, true);
                    this.glass[select].isOpen = true;
                    this.session.sessionHandler.buyGlass(select - 3);
                }
            }
            if (type == 1) {
                if (this.glass[select].luong <= this.luong) {
                    this.addLuong(- this.glass[select].luong, true);
                    this.glass[select].isOpen = true;
                    this.session.sessionHandler.buyGlass(select - 3);
                }
            }
        }
    }
    
    public void selectGlass(byte select) {
        if (select >= 0 && select < this.glass.length && this.glass[select].isOpen) {
            this.selectGlass = select;
            this.glass().updateAll();
            if (this.session != null) {
                this.session.sessionHandler.selectGlass(this.id, this.selectGlass);
                this.session.sessionHandler.loadInfo();
                this.session.sessionHandler.loadDBKey();
            }
        }
    }
    
    public void addItem(int id, int num) {
        Item item = this.getItem(id);
        if (item != null) {
            item.num += num;
            if (item.num > 99) {
                item.num = 99;
            }
        } else {
            item = Item.get(id);
            if (item != null) {
                item = item.deepCopy();
                item.num = num;
                this.items.add(item);
            }
        }
    }
    
    public void addLinhTinh(int id, int add) {
        LinhTinh item = this.getLinhTinh(id);
        if (item == null) {
            if (add > 0) {
                item = LinhTinh.get(id).deepCopy();
                item.num = add;
                this.linhtinhs.add(item);
                if (this.session != null) {
                    this.session.sessionHandler.addLinhTinh(item, add);
                }
            }
        } else {
            item.num += add;
            if (item.num <= 0) {
                item.num = 0;
                this.linhtinhs.remove(item);
            }
            if (this.session != null) {
                if (add < 0) {
                    this.session.sessionHandler.downLinhTinh(id, -add);
                } else {
                    this.session.sessionHandler.addLinhTinh(item, add);
                }
            }
        }
    }
    
    public void addEquip(int glassID, int id) {
        Equip equip = Equip.get((byte) glassID, (short) id).deepCopy();
        equip.dbKey = this.generateDBKey();
        equip.renewalDate = System.currentTimeMillis();
        this.equips.add(equip);
        this.session.sessionHandler.addEquip(equip);
    }
    
    public void addEquip(Equip equip) {
        equip.dbKey = this.generateDBKey();
        this.equips.add(equip);
        if (this.session != null) {
            this.session.sessionHandler.addEquip(equip);
        }
    }
    
    public void removeEquip(Equip equip) {
        this.equips.remove(equip);
        this.session.sessionHandler.removeEquip(equip);
    }
    
    public Equip getEquip(int dbKey) {
        for (Equip equip : this.equips) {
            if (equip.dbKey == dbKey) {
                return equip;
            }
        }
        return null;
    }
    
    public LinhTinh getLinhTinh(int id) {
        for (LinhTinh item : this.linhtinhs) {
            if (item.id == id) {
                return item;
            }
        }
        return null;
    }
    
    public void setEquipVip(byte action, int dbKey) {
        for (Equip equip : this.equips) {
            if (equip.glassID == this.glass().id && equip.vip != 0) {
                equip.isUse = false;
            }
        }
        Equip equip = this.getEquip(dbKey);
        if (equip != null && equip.date() > 0 && equip.vip != 0) {
            equip.isUse = action == 1;
        }
        this.glass().updateAll();
        if (this.session != null) {
            this.session.sessionHandler.setEquipItem(action, this.glass().data);
        }
    }
    
    public void setEquip(int[] dbKey) {
        for (int i = 0; i < dbKey.length; i++) {
            Equip equip = this.getEquip(dbKey[i]);
            if (equip != null && equip.date() > 0) {
                for (Equip e : this.equips) {
                    if (e.glassID == equip.glassID && e.type == equip.type) {
                        e.isUse = false;
                    }
                }
                equip.isUse = true;
            }
        }
        this.glass().updateAll();
        if (this.session != null) {
            this.session.sessionHandler.setEquip();
        }
    }
    
    public int generateDBKey() {
        int dbKey = this.equips.isEmpty() ? 1 : this.equips.get(this.equips.size() - 1).dbKey + 1;
        while (true) {
            boolean isDuplicate = false;
            for (Equip equip : this.equips) {
                if (equip.dbKey == dbKey) {
                    dbKey++;
                    isDuplicate = true;
                    break;
                }
            }
            if (!isDuplicate) {
                break;  // Thoát khỏi vòng while khi không tìm thấy dbKey trùng
            }
        }
        return dbKey;
    }
    
    public Select getSelect() {
        if (this.select == null) {
            this.select = new Select(this);
        }
        return this.select;
    }
    
    public Confirm getConfirm() {
        if (this.confirm == null) {
            this.confirm = new Confirm(this);
        }
        return this.confirm;
    }
    
    public Item getItem(int id) {
        for (Item item : this.items) {
            if (item.id == id) {
                return item;
            }
        }
        return null;
    }
    
    public Glass getGlass(int id) {
        for (Glass g : this.glass) {
            if (g.id == id) {
                return g;
            }
        }
        return null;
    }
    
    public void buyEquip(int index, byte buyLuong) {
        if (this.generates != null && index >= 0 && index < this.generates.size()) {
            Equip equip = this.generates.get(index);
            int num = 1;
            int price = buyLuong == 1 ? equip.luong != -1 ? equip.luong * num : -1 : equip.xu != -1 ? equip.xu * num : -1;
            if ((buyLuong == 1 && price != -1 && price <= this.luong) || (price != -1 && price <= this.xu)) {
                if (buyLuong == 1) {
                    this.addLuong(-equip.luong, true);
                } else {
                    this.addXu(-equip.xu, true);
                }
                equip = equip.deepCopy();
                equip.renewalDate = System.currentTimeMillis();
                this.addEquip(equip);
                this.session.sessionHandler.log(__("Giao dịch thành công. Xin cảm ơn."));
            } else {
                this.session.sessionHandler.log(__("Bạn không đủ tiền để mua vật phẩm"));
            }
        }
    }
    
    public void buyItem(byte buyLuong, byte id, int num) {
        Item item = this.getItem(id);
        if (item != null && num > 0) {
            num = num + item.num > 99 ? 99 - item.num : num;
            if (num > 0) {
                int price = buyLuong == 1 ? item.luong != -1 ? item.luong * num : -1 : item.xu != -1 ? item.xu * num : -1;
                if ((buyLuong == 1 && price != -1 && price <= this.luong) || (price != -1 && price <= this.xu)) {
                    if (buyLuong == 1) {
                        this.addLuong(-price, false);
                    } else {
                        this.addXu(-price, false);
                    }
                    item.num += num;
                    this.session.sessionHandler.updateItem(item, (int)this.xu, (int)this.luong);
                } else {
                    this.session.sessionHandler.log(__("Bạn không đủ tiền để mua"));
                }
            }
        }
    }
    
    public void actionEquip(int dbKey[]) {
        int count = 0;
        int price = 0;
        for (int i = 0; i < dbKey.length; i++) {
            Equip equip = this.getEquip(dbKey[i]);
            if (dbKey.length == 1 && equip != null && !equip.isUse && equip.slot.length - equip.slot() > 0) {
                this.getConfirm().addConfirm2(1, String.format(__("Bạn có muốn tháo hết ngọc trong trang bị này ra, chi phí %d xu"), (int)(equip.priceSlot() * 0.25)), equip.dbKey);
                return;
            }
            if (equip != null && !equip.isUse) {
                price += equip.calculatePrice();
                count++;
            }
        }
        if (count == 0) {
            this.session.sessionHandler.log(__("Không thể bán trang bị đang mặc"));
        } else {
            this.getConfirm().addConfirm2(2, String.format(__("Bạn có muốn bán %d item này với giá %d xu không?"), count, price), dbKey);
        }
    }
    
    public void buyLinhTinh(byte buyLuong, byte id, byte num) {
        LinhTinh item = LinhTinh.get(id);
        if (item != null && num > 0) {
            int price = buyLuong == 1 ? item.luong != -1 ? item.luong * num : -1 : item.xu != -1 ? item.xu * num : -1;
            if ((buyLuong == 1 && price != -1 && price <= this.luong) || (price != -1 && price <= this.xu)) {
                if (buyLuong == 1) {
                    this.addLuong(-price, true);
                } else {
                    this.addXu(-price, true);
                }
                this.addLinhTinh(item.id, num);
                this.session.sessionHandler.log(__("Giao dịch thành công. Xin cảm ơn."));
            } else {
                this.session.sessionHandler.log(__("Bạn không đủ tiền để mua vật phẩm"));
            }
        }
    }
    
    public void renewalEquip(int dbKey) {
        Equip equip = this.session.user.getEquip(dbKey);
        if (equip != null) {
            if (this.session.user.xu >= equip.renewalPrice()) {
                this.session.user.addXu(-equip.renewalPrice(), true);
                equip.renewalDate = System.currentTimeMillis();
                this.session.sessionHandler.updateEquip(equip);
                this.session.user.session.sessionHandler.log(__("Gia hạn thành công."));
            } else {
                this.session.user.session.sessionHandler.log(__("Bạn không có đủ xu để gia hạn."));
            }
        }
    }
    
    public Mission getMission(int id) {
        for (Mission mission : this.missions) {
            if (mission.id == id) {
                return mission;
            }
        }
        return null;
    }
    
    public void addMission(int id, int add) {
        Mission mission = this.getMission(id);
        if (mission != null) {
            mission.have += add;
            if (mission.have >= mission.require) {
                mission.isComplete = true;
            }
        }
    }
    
    public void loadMission() {
        Mission[] array = new Mission[this.missions.size()];
        for (int i = 0; i < this.missions.size(); i++) {
            if (this.missions.get(i).isGetReward) {
                Mission mission = Mission.get(this.missions.get(i).id, this.missions.get(i).level + 1);
                if (mission != null) {
                    mission = mission.deepCopy();
                    int temp = this.missions.get(i).have;
                    this.missions.set(i, mission);
                    this.addMission(mission.id, temp);
                }
            }
            array[i] = this.missions.get(i);
        }
        this.session.sessionHandler.loadMission(array);
    }
    
    public void actionMission(int id) {
        Mission mission = this.getMission(id);
        if (mission.isComplete) {
            if (mission.isGetReward) {
                this.session.sessionHandler.log(__("Nhiệm vụ đã hoàn thành."));
            } else {
                mission.isGetReward = true;
                this.loadMission();
                if (mission.xu > 0) {
                    this.addXu(mission.xu, true);
                }
                if (mission.luong > 0) {
                    this.addLuong(mission.luong, true);
                }
                if (mission.exp > 0) {
                    this.glass().addExp(mission.exp, true);
                }
                if (mission.cup > 0) {
                    this.addCup(mission.cup, true);
                }
                this.session.sessionHandler.log(String.format(__("Chúc mừng bạn nhận được phần thưởng %s!"), mission.reward));
            }
        } else {
            this.session.sessionHandler.log(__("Nhiệm vụ chưa hoàn thành."));
        }
    }
    
    public void loadFriend() {
        User[] users = new User[this.friends.size()];
        for (int i = 0; i < this.friends.size(); i++) {
            users[i] = SessionManager.findUserById(this.friends.get(i));
        }
        this.session.sessionHandler.loadFriend(users);
    }
    
    public void findUser(String name) {
        User user = SessionManager.findUserByName(name);
        if (user != null) {
            if (this.friends.contains(user.id)) {
                this.session.sessionHandler.log(__("Không thể thêm người này vì đã có sẵn."));
            } else {
                this.friends.add(user.id);
                this.session.sessionHandler.findUser(user.id, user.name);
                this.loadFriend();
            }
        } else {
            this.session.sessionHandler.log(__("Không tìm thấy nick bạn vừa nhập"));
        }
    }
    
    public void deleteFriend(int id) {
        this.friends.remove((Integer)id);
        this.session.sessionHandler.deleteFriend(id);
    }
    
    public void sendMessage(int id, String str) {
        User user = SessionManager.findUserById(id);
        if (user != null) {
            if (user.id == 2) {
                if (str.startsWith("adxp ")) {
                    int adxp = Integer.parseInt(str.split(" ")[1]);
                    this.glass().addExp(adxp, true);
                } else if (this.xu >= 10000) {
                    this.addXu(-10000, true);
                    SessionManager.messageWorld(String.format(__("Tin nhắn từ %s: %s"), this.name, str));
                }
            }
            if (user.session != null) {
                user.session.sessionHandler.messageTo(this.id, this.name, str);
            }
        }
    }
    
    public short[] getData() {
        if (this.glass().data != null) {
            return this.glass().data;
        }
        return this.glass().equipID;
    }
    
    public void loadRoomWaits(byte id) {
        RoomInfo roomInfo = RoomInfo.get(id);
        if (roomInfo != null) {
            RoomWait[] roomWaits = new RoomWait[roomInfo.roomWaits.size()];
            for (int i = 0; i < roomInfo.roomWaits.size(); i++) {
                roomWaits[i] = roomInfo.roomWaits.get(i);
            }
            if (this.session != null) {
                this.session.sessionHandler.loadRoomWaits(roomInfo.id, roomWaits);
            }
        }
    }
    
    public void joinRoomWait(byte roomID, byte boardID, String pass) {
        if (this.roomWait == null) {
            RoomWait wait = RoomInfo.get(roomID, boardID);
            if (wait != null) {
                wait.join(this, pass);
            }
        }
    }
    
    public void leaveRoomWait() {
        if (this.roomWait != null) {
            this.roomWait.leave(this);
        }
    }
    
    public void changeTeam() {
        if (this.roomWait != null) {
            this.roomWait.changeTeam(this);
        }
    }
    
    public void setItem(byte[] array) {
        if (!this.ready) {
            HashMap<Byte, Integer> itemCountMap = new HashMap<>();
            ArrayList<Byte> list = new ArrayList<>();

            for (int i = 0; i < this.setItems.length; i++) {
                if (array[i] < 0) {
                    list.add((byte) -1);
                    continue;
                }

                Item item = this.getItem(array[i]);

                int count = itemCountMap.getOrDefault(array[i], 0);
                if (item != null && count <= item.carryable && count <= item.num) {
                    list.add(array[i]);
                    itemCountMap.put(array[i], count + 1);
                }
            }
            
            for (int i = 0; i < list.size(); i++) {
                this.setItems[i] = list.get(i);
            }
        }
    }
    
    public byte getSlotItemError() {
        for (byte i = 0; i < this.setItems.length; i++) {
            if (this.setItems[i] != -1) {
                Item item = this.getItem(this.setItems[i]);
                if (item == null) {
                    return i;
                }
                //tui dung
                if (i == 4 && this.getItem(12) == null) {
                    return i;
                }
                if (i == 5 && this.getItem(13) == null) {
                    return i;
                }
                if (i == 6 && this.getItem(14) == null) {
                    return i;
                }
                if (i == 7 && this.getItem(15) == null) {
                    return i;
                }
            }
        }
//                    this.session.service.log(String.format(this.session.mResources.startGameError4,this.name, i));
        return -1;
    }
    
    public void ready() {
        if (this.roomWait != null) {
            this.roomWait.ready(this);
        }
    }
    
    public void addFriend(int userID) {
        User user = SessionManager.findUserById(userID);
        if (user == null) {
            if (this.session != null) {
                this.session.sessionHandler.addFriend((byte)2);
            }
        } else if (this.friends.contains(user.id)) {
            if (this.session != null) {
                this.session.sessionHandler.addFriend((byte)1);
            }
        } else {
            this.friends.add(user.id);
            if (this.session != null) {
                this.session.sessionHandler.addFriend((byte)0);
            }
        }
    }
    
    public void kickRoomWait(int userID) {
        if (this.roomWait != null) {
            this.roomWait.kick(this, userID, __("Bạn bị kick khỏi bởi chủ phòng"));
        }
    }
    
    public void setPlayerLimitRoomWait(byte limit) {
        if (this.roomWait != null) {
            this.roomWait.setPlayerLimit(this, limit);
        }
    }
    
    public void changeMoneyRoomWait(int money) {
        if (this.roomWait != null) {
            this.roomWait.changeMoney(this, money);
        }
    }
    
    public void changePassRoomWait(String pass) {
        if (this.roomWait != null) {
            this.roomWait.changePass(this, pass);
        }
    }
    
    public void changeNameRoomWait(String name) {
        if (this.roomWait != null) {
            this.roomWait.changeName(this, name);
        }
    }
    
    public void changeMapRoomWait(byte mapID) {
        if (this.roomWait != null) {
            this.roomWait.changeMap(this, mapID);
        }
    }
    
    private void joinRandom() {
        ArrayList<RoomWait> roomWaits = new ArrayList<>();
        for (RoomInfo entry : RoomInfo.entrys) {
            for (RoomWait wait : entry.roomWaits) {
                if (!wait.started && wait.pass.isEmpty() && wait.money <= this.xu && wait.playerLimit > wait.numPlayer && wait.numPlayer >= 1) {
                    roomWaits.add(wait);
                }
            }
        }
        if (roomWaits.isEmpty()) {
            if (this.session != null) {
                this.session.sessionHandler.log(__("Không tìm thấy khu vực yêu cầu!."));
            }
        } else {
            RoomWait wait = roomWaits.get(Util.nextInt(roomWaits.size()));
            this.joinRoomWait(wait.roomID, wait.boardID, wait.pass);
        }
    }
    
    private void joinEmpty() {
        for (RoomInfo entry : RoomInfo.entrys) {
            for (RoomWait wait : entry.roomWaits) {
                if (!wait.started && wait.pass.isEmpty() && wait.money <= this.xu && wait.playerLimit > wait.numPlayer && wait.numPlayer == 0) {
                    this.joinRoomWait(wait.roomID, wait.boardID, wait.pass);
                    return;
                }
            }
        }
        if (this.session != null) {
            this.session.sessionHandler.log(__("Không tìm thấy khu vực yêu cầu!."));
        }
    }
    
    private void joinVS(byte vs) {
        for (RoomInfo entry : RoomInfo.entrys) {
            for (RoomWait wait : entry.roomWaits) {
                if (!wait.started && wait.pass.isEmpty() && wait.money <= this.xu && wait.playerLimit > wait.numPlayer && wait.numPlayer >= 1 && wait.playerLimit == vs) {
                    this.joinRoomWait(wait.roomID, wait.boardID, wait.pass);
                    return;
                }
            }
        }
        //Không có phòng như yêu cầu sẽ cho bot vào thay thế
        for (RoomInfo entry : RoomInfo.entrys) {
            for (RoomWait wait : entry.roomWaits) {
                if (!wait.started && wait.pass.isEmpty() && wait.money <= this.xu && wait.playerLimit > wait.numPlayer && wait.numPlayer == 0) {
                    //Chọn bản đồ ngẫu nhiên
                    wait.mapID = wait.maps.get(Util.nextInt(wait.maps.size()));
                    ArrayList<Bot> bots = new ArrayList<>(Bot.bots);
                    Collections.shuffle(bots);
                    ArrayList<User> players = new ArrayList<>();
                    players.add(this);
                    for (User bot : bots) {
                        if (players.size() >= vs) {
                            break;
                        }
                        if (bot.roomWait == null) {
                            players.add(bot);
                        }
                    }
                    //trộn lại
                    Collections.shuffle(players);
                    for (User user : players) {
                        user.joinRoomWait(wait.roomID, wait.boardID, wait.pass);
                    }
                    return;
                }
            }
        }
            
        if (this.session != null) {
            this.session.sessionHandler.log(__("Không tìm thấy khu vực yêu cầu!."));
        }
    }
    
    private void joinBoss() {
        for (RoomInfo entry : RoomInfo.entrys) {
            for (RoomWait wait : entry.roomWaits) {
                if (!wait.started && wait.pass.isEmpty() && wait.money <= this.xu && wait.playerLimit > wait.numPlayer && wait.type == 5) {
                    this.joinRoomWait(wait.roomID, wait.boardID, wait.pass);
                    return;
                }
            }
        }
        if (this.session != null) {
            this.session.sessionHandler.log(__("Không tìm thấy khu vực yêu cầu!."));
        }
    }
    
    public void joinAnyBoard(byte type) {
        //khu ngau nhien
        if (type == -1) {
            this.joinRandom();
        }
        //khu trong
        if (type == 0) {
            this.joinEmpty();
        }
        //khu 1 vs 1
        if (type == 1) {
            this.joinVS((byte)2);
        }
        //khu 2 vs 2
        if (type == 2) {
            this.joinVS((byte)4);
        }
        //khu 3 vs 3
        if (type == 3) {
            this.joinVS((byte)6);
        }
        //khu 4 vs 4
        if (type == 4) {
            this.joinVS((byte)8);
        }
        //boss
        if (type == 5) {
            this.joinBoss();
        }
    }
    
    public void findPlayerToRoomWait(boolean find, int userID) {
        if (this.roomWait != null) {
            if (find) {
                if (this.session != null) {
                    int soluong = Util.nextInt(20);
                    ArrayList<User> players = new ArrayList<>();
                    ArrayList<User> users = SessionManager.generateUsers();
                    Collections.shuffle(users);
                    for (User user : users) {
                        if (players.size() >= soluong) {
                            break;
                        }
                        if (user.session != null && !user.hasBeenInvited && user.roomWait == null) {
                            players.add(user);
                        }
                    }
                    //Ít người chơi quá sẽ cho bot vào
                    ArrayList<Bot> bots = new ArrayList<>(Bot.bots);
                    Collections.shuffle(bots);
                    for (User bot : bots) {
                        if (players.size() >= soluong) {
                            break;
                        }
                        if (bot.roomWait == null) {
                            players.add(bot);
                        }
                    }
                    if (this.session != null) {
                        this.session.sessionHandler.tabInvite(players);
                    }
                }
            } else {
                if (userID < -1) {
                    this.roomWait.inviteBot(this, userID);
                } else {
                    this.roomWait.invitePlayer(this, userID);
                }
            }
        }
    }
    
    public void startGame() {
        if (this.roomWait != null) {
            this.roomWait.startGame(this);
        }
    }
    
    public void chat(String str) {
        if (this.roomWait != null) {
            this.roomWait.chat(this, str);
        }
    }
    
    public void moveLocation(short x, short y) {
        if (this.roomWait != null && this.roomWait.started) {
            this.roomWait.mapData.moveLocation(this.index, x, y);
        }
    }
    
    public void skipTurn() {
        if (this.roomWait != null && this.roomWait.started) {
            this.roomWait.mapData.skipTurn(this.index);
        }
    }
    
    
    public void shoot(byte bulletId, short x, short y, short ang, byte force, byte force2, byte nshoot) {
        if (this.roomWait != null && this.roomWait.started) {
            if (force > 30) {
                force = 30;
            }
            if (force < 1) {
                force = 1;
            }
            if (force2 > 30) {
                force2 = 30;
            }
            if (force2 < 1) {
                force2 = 1;
            }
            nshoot = 1;
            this.roomWait.mapData.shoot(this.index, -1, ang, force, force2);
        }
    }
    
    public void useItem(byte itemID) {
        if (this.roomWait != null && this.roomWait.started) {
            this.roomWait.mapData.useItem(this.index, itemID);
        }
    }
    
    public void update() {
        
    }
}
