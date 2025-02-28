package mobiarmy.server;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import mobiarmy.Util;
import mobiarmy.io.Message;
import mobiarmy.server.DBManager.DataRow;
import static mobiarmy.server.Text.__;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author Văn Tú
 */
public class ControlHandler {
    
    private Session session;
    
    public ControlHandler(Session session) {
        this.session = session;
    }

    public static final HashSet<Integer> syncCommands = new HashSet<>();

    // Khởi tạo các command cần đồng bộ
    static {
        syncCommands.add(-25);  // Gia hạn đồ
        syncCommands.add(-23);  // Nhiệm vụ
        syncCommands.add(-18);  // Hộp trang bị
        syncCommands.add(-4);   // Thoát game
        syncCommands.add(-3);   // Đồ đặc biệt shop
        syncCommands.add(-2);   // Mặc set trang bị
        syncCommands.add(6);    // Đến khu vực
        syncCommands.add(7);    // Vào phòng
        syncCommands.add(8);    // Tham gia khu vực
        syncCommands.add(9);    // Chat message
        syncCommands.add(11);   // Kick message
        syncCommands.add(15);   // Rời khu vực
        syncCommands.add(16);   // Sẵn sàng
        syncCommands.add(19);   // Đặt cược
        syncCommands.add(20);   // Bắt đầu game
        syncCommands.add(21);   // Thay đổi vị trí
        syncCommands.add(22);   // Shoot message
        syncCommands.add(26);   // Dùng item
        syncCommands.add(28);   // Chơi ngay (quick play)
        syncCommands.add(42);   // Kiểm tra độ trễ
        syncCommands.add(49);   // Bỏ lượt (skip turn)
        syncCommands.add(53);   // Set XY (vị trí nhân vật)
        syncCommands.add(71);   // Đổi phe
        syncCommands.add(72);   // Mua item
        syncCommands.add(74);   // Mua nhân vật
        syncCommands.add(75);   // Chọn bản đồ
        syncCommands.add(79);   // Remove bull
        syncCommands.add(102);  // Mặc trang bị
        syncCommands.add(104);  // Mua trang bị
        syncCommands.add(110);  // Quay số (random spin)
    }


    public void handleControlMessage(Message msg) throws IOException {
        switch (msg.getCommand()) {
            case -28 -> {
                
                if (this.session.user != null && this.session.user.roomWait == null) {
                    this.session.sessionHandler.loadRoomInfo();
                    this.session.user.hasBeenInvited = false;
                }
            }
            case -27 -> {
                this.session.messageHandler.setKey();
            }
            case -25 -> {
                if (this.session.user != null && this.session.user.roomWait == null) {
                    byte action = msg.reader().readByte();
                    int dbKey = msg.reader().readInt()&0xFFFF;
                    if (action == 0) {
                        Equip equip = this.session.user.getEquip(dbKey);
                        if (equip != null) {
                            this.session.sessionHandler.requestRenewal(dbKey, String.format(__("Bạn có muốn gia hạn trang bị này với giá %d xu?"), equip.renewalPrice()));
                        }
                    }
                    if (action == 1) {
                        this.session.user.renewalEquip(dbKey);
                    }
                }
            }
            case -23 -> {
                if (this.session.user != null && this.session.user.roomWait == null) {
                    byte action = msg.reader().readByte();
                    if (action == 0) {
                        this.session.user.loadMission();
                    }
                    if (action == 1) {
                        this.session.user.actionMission(msg.reader().readByte());
                    }
                }
            }
            case -19 -> {
                if (this.session.user != null && this.session.user.roomWait == null) {
                    this.session.sessionHandler.loadRoomInfoName();
                }
            }
            case -14 -> {
                if (this.session.user != null && this.session.user.roomWait == null) {
                    this.session.sessionHandler.topInfo();
                }
            }
            case -4 -> {
                this.session.disconnect();
            }
            case -3 -> {
                if (this.session.user != null && this.session.user.roomWait == null) {
                    byte type = msg.reader().readByte();
                    if (type == 0) {
                        this.session.sessionHandler.shopLinhTinh(ShopLinhTinh.entrys);
                    } else {
                        this.session.user.buyLinhTinh(msg.reader().readByte(), msg.reader().readByte(), msg.reader().readByte());
                    }
                }
            }
            case -2 -> {
                if (this.session.user != null && this.session.user.roomWait == null) {
                    this.session.user.setEquipVip(msg.reader().readByte(), msg.reader().readInt()&0xFFFF);
                }
            }
            case 1 -> {
                if (this.session.user == null) {
                    String name = msg.reader().readUTF();
                    String pass = msg.reader().readUTF();
                    this.session.version = msg.reader().readUTF();
                    try {
                        ArrayList<DataRow> rows = Server.dbManager.selectColumnName("SELECT * FROM user WHERE LOWER(username) = LOWER(?)", name);
                        if (rows.isEmpty() || !BCrypt.checkpw(pass, rows.get(0).getString("password").replaceFirst("^\\$2[aby]\\$", "\\$2a\\$"))) {
                            this.session.sessionHandler.log(__("Thông tin tài khoản hoặc mật khẩu không chính xác."));
                        } else {
                            User user = SessionManager.findUserById(rows.get(0).getInt("id"));
                            if (user == null) {
                                user = new User(rows.get(0).getInt("id"), rows.get(0).getString("username"));
                                SessionManager.addUser(user);
                            }
                            if (user.connect(this.session)) {
                                this.session.user.glass().updateAll();
                                this.session.sessionHandler.loadInfoAll();
                                this.session.sessionHandler.layerData();
                                this.session.sessionHandler.idNotColision();
                            } else {
                                this.session.sessionHandler.log(__("Tài khoản đang được đăng nhập trên máy khác hãy thử lại sau."));
                                user.session.requestDisconnect();
                            }
                        }

                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            case 5 -> {
                if (this.session.user != null) {
                    this.session.user.sendMessage(msg.reader().readInt(), msg.reader().readUTF());
                }
            }
            case 6 -> {
                if (this.session.user != null && this.session.user.roomWait == null) {
                    this.session.sessionHandler.loadRoomInfo();
                    this.session.user.hasBeenInvited = false;
                }
            }
            case 7 -> {
                if (this.session.user != null && this.session.user.roomWait == null) {
                    this.session.user.loadRoomWaits(msg.reader().readByte());
                }
            }
            case 8 -> {
                if (this.session.user != null && this.session.user.roomWait == null) {
                    this.session.user.joinRoomWait(msg.reader().readByte(), msg.reader().readByte(), msg.reader().readUTF());
                }
            }
            case 9 -> {
                if (this.session.user != null && this.session.user.roomWait != null) {
                    this.session.user.chat(msg.reader().readUTF());
                }
            }
            case 11 -> {
                if (this.session.user != null && this.session.user.roomWait != null && !this.session.user.roomWait.started) {
                    this.session.user.kickRoomWait(msg.reader().readInt());
                }
            }
            case 15 -> {
                if (this.session.user != null && this.session.user.roomWait != null) {
                    this.session.user.leaveRoomWait();
                }
            }
            case 16 -> {
                if (this.session.user != null && this.session.user.roomWait != null && !this.session.user.roomWait.started) {
                    this.session.user.ready();
                }
            }
            case 17 -> {
                if (this.session.user != null && this.session.user.roomWait == null) {
                    byte action = msg.reader().readByte();
                    if (action == 0) {
                        this.session.user.getSelect().reset();
                        byte lent = msg.reader().readByte();
                        for (int i = 0; i < lent; i++) {
                            int id = msg.reader().readInt();
                            int num = msg.reader().readUnsignedByte();
                            if((id & 0x10000) > 0) {
                                int dbKey = id&0xFFFF;
                                this.session.user.getSelect().addElement(0, dbKey, num);
                            } else {
                                this.session.user.getSelect().addElement(1, id, num);
                            }
                        }
                        this.session.user.getSelect().make();
                    }
                    if (action == 1) {
                        this.session.user.getConfirm().confirm();
                    }
                }
            }
            case 18 -> {
                if (this.session.user != null && this.session.user.roomWait != null && !this.session.user.roomWait.started) {
                    this.session.user.changePassRoomWait(msg.reader().readUTF());
                }
            }
            case 19 -> {
                if (this.session.user != null && this.session.user.roomWait != null && !this.session.user.roomWait.started) {
                    this.session.user.changeMoneyRoomWait(msg.reader().readInt());
                }
            }
            case 20 -> {
                if (this.session.user != null && this.session.user.roomWait != null && !this.session.user.roomWait.started) {
                    this.session.user.startGame();
                }
            }
            case 21 -> {
                if (this.session.user != null && this.session.user.roomWait != null && this.session.user.roomWait.started) {
                    this.session.user.moveLocation(msg.reader().readShort(), msg.reader().readShort());
                }
            }
            case 22 -> {
                if (this.session.user != null && this.session.user.roomWait != null && this.session.user.roomWait.started) {
                    // id dan
                    byte bulletId = msg.reader().readByte();
                    short gunX = msg.reader().readShort();
                    short gunY = msg.reader().readShort();
                    short ang = msg.reader().readShort();
                    // 2 luc
                    byte force = msg.reader().readByte();
                    byte force2 = 0;
                    // Neu la apa or chicky -> 2 luc
                    if(bulletId == 17 || bulletId == 19) {
                        force2 = msg.reader().readByte();
                    }
                    // so lan ban
                    byte nshoot = msg.reader().readByte();
                    this.session.user.shoot(bulletId, gunX, gunY, ang, force, force2, nshoot);
                }
            }
            case 26 -> {
                if (this.session.user != null && this.session.user.roomWait != null && this.session.user.roomWait.started) {
                    this.session.user.useItem(msg.reader().readByte());
                }
            }
            case 28-> {
                if (this.session.user != null && this.session.user.roomWait == null) {
                    this.session.user.joinAnyBoard(msg.reader().readByte());
                }
            }
            case 29 -> {
                if (this.session.user != null) {
                    this.session.user.loadFriend();
                }
            }
            case 32 -> {
                if (this.session.user != null) {
                    this.session.user.addFriend(msg.reader().readInt());
                }
            }
            case 33 -> {
                if (this.session.user != null) {
                    this.session.user.deleteFriend(msg.reader().readInt());
                }
            }
            case 34 -> {
                if (this.session.user != null) {
                    int id = msg.reader().readInt();
                    if(id < -1) {
                        User user = Bot.findById(id);
                        if (user != null) {
                            this.session.sessionHandler.loadInnfo(user);
                        }
                    } else {
                        User user = SessionManager.findUserById(id);
                        if (user != null) {
                            this.session.sessionHandler.loadInnfo(user);
                        }
                    }
                }
            }
            case 36 -> {
                if (this.session.user != null) {
                    this.session.user.findUser(msg.reader().readUTF());
                }
            }
            case 42 -> {
                if (this.session.user != null) {
                    int gameTick = this.session.isGE("2.4.0") ? msg.reader().readInt() : msg.reader().readByte();
                    this.session.sessionHandler.pingPaint();
                }
            }
            case 49 -> {
                if (this.session.user != null && this.session.user.roomWait != null && this.session.user.roomWait.started) {
                    this.session.user.skipTurn();
                }
            }
            case 54 -> {
                if (this.session.user != null && this.session.user.roomWait != null && !this.session.user.roomWait.started) {
                    this.session.user.changeNameRoomWait(msg.reader().readUTF());
                }
            }
            case 56 -> {
                if (this.session.user != null && this.session.user.roomWait != null && !this.session.user.roomWait.started) {
                    this.session.user.setPlayerLimitRoomWait(msg.reader().readByte());
                }
            }
            case 58 -> {
                msg.reader().readByte();
            }
            case 68 -> {
                if (this.session.user != null && this.session.user.roomWait != null && !this.session.user.roomWait.started) {
                    byte[] array = new byte[msg.reader().available()];
                    msg.reader().read(array);
                    this.session.user.setItem(array);
                }
            }
            case 69 -> {
                if (this.session.user != null && this.session.user.roomWait == null) {
                    this.session.user.selectGlass(msg.reader().readByte());
                }
            }
            case 71 -> {
                if (this.session.user != null && this.session.user.roomWait != null && !this.session.user.roomWait.started) {
                    this.session.user.changeTeam();
                }
            }
            case 72 -> {
                if (this.session.user != null && this.session.user.roomWait == null) {
                    this.session.user.buyItem(msg.reader().readByte(), msg.reader().readByte(), msg.reader().readByte());
                }
            }
            case 74 -> {
                if (this.session.user != null && this.session.user.roomWait == null) {
                    this.session.user.buyGlass(msg.reader().readByte() + 3, msg.reader().readByte());
                }
            }
            case 75 -> {
                if (this.session.user != null && this.session.user.roomWait != null && !this.session.user.roomWait.started) {
                    this.session.user.changeMapRoomWait(msg.reader().readByte());
                }
            }
            case 78 -> {
                if (this.session.user != null && this.session.user.roomWait != null && !this.session.user.roomWait.started) {
                    this.session.user.findPlayerToRoomWait(msg.reader().readBoolean(), msg.reader().readInt());
                }
            }
            case 90 -> {
                if (this.session.user != null && this.session.user.roomWait == null) {
                    byte type = msg.reader().readByte();
                    byte version = msg.reader().readByte();
                    switch (type) {
                        case 1 -> this.session.sessionHandler.iconInfo(12, version);
                        case 2 -> this.session.sessionHandler.mapInfo(12, version);
                        case 3 -> this.session.sessionHandler.layerInfo(12, version);
                        case 4 -> this.session.sessionHandler.dataInfo(12, version);
                        case 5 -> this.session.sessionHandler.captionInfo(12, version);
                        case 6 -> {
                            this.session.sessionHandler.loadInfo();
                            this.session.sessionHandler.updateRuong();
                            this.session.sessionHandler.loadRoomName();
                        }
                    }
                }
            }
            case 98 -> {
                if (this.session.user != null && this.session.user.roomWait == null) {
                    int[] pointAdd = new int[this.session.user.glass().ability.length];
                    for (int i = 0; i < pointAdd.length; i++) {
                        pointAdd[i] = msg.reader().readShort();
                    }
                    this.session.user.glass().upadtePoint(pointAdd);
                }
            }
            case 99 -> {
                if (this.session.user != null && this.session.user.roomWait == null) {
                    this.session.sessionHandler.loadInfo();
                }
            }
            case 102 -> {
                if (this.session.user != null && this.session.user.roomWait == null) {
                    int[] dbKey = new int[this.session.user.glass().dbKey.length];
                    for(int i = 0; i < dbKey.length; i++) {
                        dbKey[i] = msg.reader().readInt()&0xFFFF;
                    }
                    this.session.user.setEquip(dbKey);
                }
            }
            case 103 -> {
                if (this.session.user != null && this.session.user.roomWait == null) {
                    this.session.sessionHandler.equipShop(this.session.user.glass().id);
                }
            }
            case 104 -> {
                if (this.session.user != null && this.session.user.roomWait == null) {
                    byte type = msg.reader().readByte();
                    switch (type) {
                        case 0 -> this.session.user.buyEquip(msg.reader().readShort(), msg.reader().readByte());
                        case 1 -> {
                            int dbKey[] = new int[msg.reader().readByte()];
                            for (int i = 0; i < dbKey.length; i++) {
                                dbKey[i] = msg.reader().readInt() & 0xFFFF;
                            }
                            this.session.user.actionEquip(dbKey);
                        }
                        case 2 -> this.session.user.getConfirm().confirm();
                    }
                }
            }
            case 114 -> {
                msg.reader().readUTF();
            }
            case 120 -> {
                if (this.session.user != null) {
                    this.session.sessionHandler.getBigIcon(msg.reader().readByte());
                }
            }
            case 126 -> {
                if (this.session.user != null) {
                    byte typeIcon = msg.reader().readByte();
                    byte idIcon = msg.reader().readByte();
                    byte indexIcon = -1;
                    if (typeIcon == 3 || typeIcon == 4) {
                        indexIcon = msg.reader().readByte();
                    }
                    this.session.sessionHandler.getMaterialIconMessage(typeIcon, idIcon, indexIcon);
                }
            }
            case 127 -> {
                msg.reader().readUTF();
            }
            default-> {
                //System.out.println("cmd: "+ msg.getCommand());
            }
        }
        System.out.println("cmd: "+ msg.getCommand());
    }
    
}
