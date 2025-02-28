package mobiarmy.server;

import java.io.IOException;
import java.util.ArrayList;
import mobiarmy.Util;
import mobiarmy.io.Message;
import mobiarmy.io.Write;
import static mobiarmy.server.GameData.getCache;
import mobiarmy.war.*;

/**
 *
 * @author Văn Tú
 */
public class SessionHandler {
    
    private Session session;
    
    public SessionHandler(Session session) {
        this.session = session;
    }
    
    
    
    public void log(String str) {
        try {
            Message message = new Message(45);
            message.writer().writeUTF(str);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    
    public void loadInfoAll() {
        try {
            Message message = new Message(3);
            if (this.session.isGE("2.4.0")) {
                //message.writer().writeUTF(this.session.user.name);
            }
            message.writer().writeInt(this.session.user.id);
            message.writer().writeInt((int) this.session.user.xu);
            message.writer().writeInt((int) this.session.user.luong);
            message.writer().writeByte(this.session.user.selectGlass);
            message.writer().writeShort(-1);
            message.writer().writeByte(0);
            for (Glass glass : this.session.user.glass) {
                message.writer().writeBoolean(glass.data != null);
                for (int i = 0; glass.data != null && i < glass.data.length; i++) {
                    message.writer().writeShort(glass.data[i]);
                }
                for (int i = 0; i < glass.equipID.length; i++) {
                    message.writer().writeShort(glass.equipID[i]);
                }
            }
            for (Item item : this.session.user.items) {
                message.writer().writeByte(item.num);
                message.writer().writeInt(item.xu);
                message.writer().writeInt(item.luong);
            }
            for (Glass character : this.session.user.glass) {
                if (character.id > 2) {
                    message.writer().writeByte(character.isOpen ? 1 : 0);
                    message.writer().writeShort(character.xu / 1000);
                    message.writer().writeShort(character.luong);
                }
            }
            message.writer().writeUTF("a");
            message.writer().writeUTF("b");
            message.writer().writeUTF("c");
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void layerData() {
        try {
            Message message = new Message(64);
            message.writer().writeByte(this.session.user.glass.length);
            for (Glass character : this.session.user.glass) {
                message.writer().writeByte(character.friction);
            }
            message.writer().writeByte(this.session.user.glass.length);
            for (Glass character : this.session.user.glass) {
                message.writer().writeShort(character.angle);
            }
            message.writer().writeByte(this.session.user.glass.length);
            for (Glass character : this.session.user.glass) {
                message.writer().writeByte(character.distance);
            }
            message.writer().writeByte(this.session.user.glass.length);
            for (Glass character : this.session.user.glass) {
                message.writer().writeByte(character.bullet);
            }
            message.writer().writeByte(Room.MAX_E_PLAYER);
            message.writer().writeByte(MapBoss.entrys.length);
            for (MapBoss mapBoss : MapBoss.entrys) {
                message.writer().writeByte(mapBoss.mapID);
            }
            for (MapBoss mapBoss : MapBoss.entrys) {
                message.writer().writeByte(mapBoss.glassID);
            }
            message.writer().writeByte(Room.MAX_PLAYER);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void topInfo() {
        try {
            Message message = new Message(-14);
            message.writer().writeByte(-1);
            message.writer().writeByte(0);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void iconInfo(int version, int version2) {
        try {
            Message message = new Message(90);
            message.writer().writeByte(1);
            message.writer().writeByte(version);
            if (version != version2) {
                byte[] data = GameData.mapIcon.toByteArray();
                message.writer().writeShort(data.length);
                message.writer().write(data);
            }
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void mapInfo(int version, int version2) {
        try {
            Message message = new Message(90);
            message.writer().writeByte(2);
            message.writer().writeByte(version);
            if (version != version2) {
                Write write = new Write();
                write.writer().writeByte(Map.entrys.length);
                for (Map map : Map.entrys) {
                    write.writer().writeByte(map.id);
                    
                    Write data = new Write();
                    data.writer().writeShort(map.data.width);
                    data.writer().writeShort(map.data.height);
                    data.writer().writeByte(map.data.bricks.length);
                    for (Map.Brick brick : map.data.bricks) {
                        data.writer().writeByte(brick.id);
                        data.writer().writeShort(brick.x);
                        data.writer().writeShort(brick.y);
                    }
                    data.writer().writeByte(map.data.points.length);
                    for (Map.Point point : map.data.points) {
                        data.writer().writeShort(point.x);
                        data.writer().writeShort(point.y);
                    }
                    write.writer().writeShort(data.size());
                    write.writer().write(data.getBytes());
                        
                    write.writer().writeShort(map.bg);
                    write.writer().writeShort(map.mapAddY);
                    write.writer().writeShort(map.bullEffShower);
                    write.writer().writeShort(map.inWaterAddY);
                    write.writer().writeShort(map.cl2AddY);
                    write.writer().writeUTF(map.name);
                    write.writer().writeUTF(map.file);
                }
                message.writer().writeShort(write.size());
                message.writer().write(write.getBytes());
            }
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void layerInfo(int version, int version2) {
        try {
            Message message = new Message(90);
            message.writer().writeByte(3);
            message.writer().writeByte(version);
            if (version != version2) {
                byte[] data = GameData.player.toByteArray();
                message.writer().writeShort(data.length);
                message.writer().write(data);
            }
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void dataInfo(int version, int version2) {
        try {
            Message message = new Message(90);
            message.writer().writeByte(4);
            message.writer().writeByte(version);
            if (version != version2) {
                Write write = new Write();
                write.writer().writeByte(Glass.entrys.length);
                for (Glass entry : Glass.entrys) {
                    write.writer().writeByte(entry.id);
                    write.writer().writeShort(entry.att);
                    write.writer().writeByte(Equip.equipsByGlassIDAndType.get(entry.id).size());
                    Equip.equipsByGlassIDAndType.get(entry.id).forEach((typeKey, equipsList) -> {
                        try {
                            write.writer().writeByte(typeKey);
                            write.writer().writeByte(equipsList.size());
                            equipsList.forEach(equip -> {
                                try {
                                write.writer().writeShort(equip.id);
                                if(equip.type == 0) {
                                    write.writer().writeByte(equip.bullet);
                                }
                                write.writer().writeShort(equip.icon);
                                write.writer().writeByte(equip.level);
                                for(int i = 0; i < equip.x.length; i++) {
                                    write.writer().writeShort(equip.x[i]);
                                    write.writer().writeShort(equip.y[i]);
                                    write.writer().writeByte(equip.w[i]);
                                    write.writer().writeByte(equip.h[i]);
                                    write.writer().writeByte(equip.dx[i]);
                                    write.writer().writeByte(equip.dy[i]);
                                }
                                for(int i = 0; i < equip.inv_ability.length; i++) {
                                    write.writer().writeByte(equip.inv_ability[i]);
                                    write.writer().writeByte(equip.inv_percen[i]);
                                }
                                } catch (IOException ex) {
                                }
                            });
                        } catch (IOException ex) {
                        }
                    });
                    
                }
                byte data[] = getCache("res/item_special.png");
                write.writer().writeShort(data.length);
                write.writer().write(data);
                for (Glass entry : Glass.entrys) {
                    data = getCache("res/bullet"+entry.id+".png");
                    write.writer().writeShort(data.length);
                    write.writer().write(data);
                }
                    
                message.writer().writeInt(write.size());
                message.writer().write(write.getBytes());
            }
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void captionInfo(int version, int version2) {
        try {
            Message message = new Message(90);
            message.writer().writeByte(5);
            message.writer().writeByte(version);
            if (version != version2) {
                Write write = new Write();
                write.writer().writeByte(Caption.entrys.length);
                for (int i = Caption.entrys.length - 1; i >= 0; i--) {
                    write.writer().writeUTF(Caption.entrys[i].caption);
                    write.writer().writeByte(Caption.entrys[i].level);
                }
                message.writer().writeShort(write.size());
                message.writer().write(write.getBytes());
            }
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void loadInfo() {
        try {
            Message message = new Message(99);
            message.writer().writeByte(this.session.user.glass().level);
            message.writer().writeByte(this.session.user.glass().getPercentLevel());
            message.writer().writeShort(this.session.user.glass().point);
            for (int i = 0; i < this.session.user.glass().ability.length; i++) {
                message.writer().writeShort(this.session.user.glass().ability[i]);
            }
            message.writer().writeInt(this.session.user.glass().exp);
            message.writer().writeInt(this.session.user.glass().getPercentLevel());
            message.writer().writeInt(this.session.user.cup);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void updateRuong() {
        try {
            Message message = new Message(101);
            message.writer().writeByte(this.session.user.equips.size());
            for (Equip equip : this.session.user.equips) {
                message.writer().writeInt(equip.dbKey|0x10000);
                message.writer().writeByte(equip.glassID);
                message.writer().writeByte(equip.type);
                message.writer().writeShort(equip.id);
                message.writer().writeUTF(equip.name);
                message.writer().writeByte(equip.inv_ability.length*2);
                for(int i = 0; i < equip.inv_ability.length; i++) {
                    message.writer().writeByte(equip.inv_ability[i]);
                    message.writer().writeByte(equip.inv_percen[i]);
                }
                message.writer().writeByte(equip.date());
                message.writer().writeByte(equip.slot());
                message.writer().writeByte(equip.vip);
                message.writer().writeByte(equip.level2);
            }
            
            // DB Key
            for(int i = 0; i < this.session.user.glass().dbKey.length; i++) {
                message.writer().writeInt(this.session.user.glass().dbKey[i]|0x10000);
            }
            this.session.sendMessage(message);
            message = new Message(125);
            message.writer().writeByte(0);
            message.writer().writeByte(this.session.user.linhtinhs.size());
            for (LinhTinh item : this.session.user.linhtinhs) {
                message.writer().writeByte(item.id);
                message.writer().writeShort(item.num);
                message.writer().writeUTF(item.name);
                message.writer().writeUTF(item.detail);
            }
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void loadRoomName() {
        try {
            Message message = new Message(88);
            message.writer().writeByte(Room.entrys.length);
            for (Room room : Room.entrys) {
                message.writer().writeUTF(room.name);
                message.writer().writeUTF("");
            }
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void loadRoomInfoName() {
        try {
            Message message = new Message(-19);
            message.writer().writeByte(RoomInfo.roomNames.size());
            for (RoomInfo.RoomInfoName infoName : RoomInfo.roomNames) {
                message.writer().writeByte(infoName.start);
                message.writer().writeUTF(infoName.name);
                message.writer().writeByte(infoName.index);
            }
//            message.writer().writeByte(1);
//            message.writer().writeByte(0);
//            message.writer().writeUTF("Boss");
//            message.writer().writeByte(3);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void getBigIcon(int id) {
        try {
            Message message = new Message(120);
            message.writer().writeByte(id);
            byte[] data = getCache("res/bigImage"+id+".png");
            if (data != null) {
                message.writer().writeShort(data.length);
                message.writer().write(data);
            } else {
                message.writer().writeShort(0);
            }
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void updateExp(int expAdd, int exp, int expMax, int percent) {
        try {
            Message message = new Message(97);
            message.writer().writeInt(expAdd);
            message.writer().writeInt(exp);
            message.writer().writeInt(expMax);
            message.writer().writeByte(0);
            message.writer().writeByte(percent);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void updateExp(int expAdd, int exp, int expMax, int level, int percent, int point) {
        try {
            Message message = new Message(97);
            message.writer().writeInt(expAdd);
            message.writer().writeInt(exp);
            message.writer().writeInt(expMax);
            message.writer().writeByte(1);
            message.writer().writeByte(level);
            message.writer().writeByte(percent);
            message.writer().writeShort(point);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void setXuLuong(long xu, long luong) {
        try {
            Message message = new Message(105);
            message.writer().writeInt((int) xu);
            message.writer().writeInt((int) luong);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void buyGlass(int selectNV) {
        try {
            Message message = new Message(74);
            message.writer().writeByte(selectNV);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void selectGlass(int id, int glass) {
        try {
            Message message = new Message(69);
            message.writer().writeInt(id);
            message.writer().writeByte(glass);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void equipShop(byte glassID) {
        try {
            Message message = new Message(103);
            this.session.user.generates = ShopEquipment.generate(glassID);
            message.writer().writeShort(this.session.user.generates.size());
            for (Equip equip : this.session.user.generates) {
                message.writer().writeByte(equip.glassID);
                message.writer().writeByte(equip.type);
                message.writer().writeShort(equip.id);
                message.writer().writeUTF(equip.name);
                message.writer().writeInt(equip.xu);
                message.writer().writeInt(equip.luong);
                message.writer().writeByte(equip.date());
                message.writer().writeByte(equip.level);
            }
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void getMaterialIconMessage(byte typeIcon, byte idIcon, byte indexIcon) throws IOException {
        try {
            byte[] data = null;
            switch(typeIcon) {
                case 0 -> data = getCache("res/icon/item/"+idIcon+".png");
                case 1 -> data = getCache("res/icon/item/"+idIcon+".png");
                case 2 -> data = getCache("res/icon/map/"+idIcon+".png");
                case 3 -> data = getCache("res/icon/item/"+idIcon+".png");
                case 4 -> data = getCache("res/icon/item/"+idIcon+".png");
            }
            if (data != null) {
                Message message = new Message(126);
                message.writer().writeByte(typeIcon);
                message.writer().writeByte(idIcon);
                message.writer().writeShort(data.length);
                message.writer().write(data);
                if(typeIcon==3||typeIcon==4) {
                    message.writer().writeByte(indexIcon);
                }
                this.session.sendMessage(message);
                System.out.println("type: "+ typeIcon + " idIcon: "+ idIcon);
            }
        } catch (IOException ex){}
    }
    
    public void loadDBKey() {
        try {
            Message message = new Message(-7);
            for (int i = 0; i < this.session.user.glass().dbKey.length; i++) {
                message.writer().writeInt(this.session.user.glass().dbKey[i]|0x10000);
            }
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void setEquipItem(int action, short[] equipIds) {
        try {
            Message message = new Message(-2);
            message.writer().writeByte(action);
            if (action == 1) {
                for (int i = 0; i < equipIds.length; i++) {
                    message.writer().writeShort(equipIds[i]);
                }
            }
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void pingPaint() {
        try {
            Message message = new Message(42);
            message.writer().writeByte(0b11111111);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void setEquip() {
        try {
            Message message = new Message(102);
            message.writer().writeByte(1);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void shopLinhTinh(LinhTinh array[]) {
        try {
            Message message = new Message(-3);
            for (LinhTinh item : array) {
                message.writer().writeByte(item.id);
                message.writer().writeUTF(item.name);
                message.writer().writeUTF(item.detail);
                message.writer().writeInt(item.xu);
                message.writer().writeInt(item.luong);
                message.writer().writeByte(item.date);
                message.writer().writeByte(item.isSelectNum ? 0 : 1);
            }
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void confirm(String str) {
        try {
            Message message = new Message(17);
            message.writer().writeByte(0);
            message.writer().writeUTF(str);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void confirm2(String str) {
        try {
            Message message = new Message(104);
            message.writer().writeByte(1);
            message.writer().writeUTF(str);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void addEquip(Equip equip) {
        try {
            Message message = new Message(104);
            message.writer().writeByte(0);
            message.writer().writeInt(equip.dbKey|0x10000);
            message.writer().writeByte(equip.glassID);
            message.writer().writeByte(equip.type);
            message.writer().writeShort(equip.id);
            message.writer().writeUTF(equip.name);
            message.writer().writeByte(equip.inv_ability.length * 2);
            for(int i = 0; i < equip.inv_ability.length; i++) {
                message.writer().writeByte(equip.inv_ability[i]);
                message.writer().writeByte(equip.inv_percen[i]);
            }
            message.writer().writeByte(equip.date());
            message.writer().writeByte(equip.vip);
            message.writer().writeByte(equip.level2);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void updateEquip(Equip... equips) {
        try {
            Message message = new Message(27);
            message.writer().writeByte(equips.length);
            for (Equip equip : equips) {
                message.writer().writeByte(2);
                message.writer().writeInt(equip.dbKey|0x10000);
                message.writer().writeByte(equip.inv_ability.length * 2);
                for(int i = 0; i < equip.inv_ability.length; i++) {
                    message.writer().writeByte(equip.inv_ability[i]);
                    message.writer().writeByte(equip.inv_percen[i]);
                }
                message.writer().writeByte(equip.slot());
                message.writer().writeByte(equip.date());
            }
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void addLinhTinh(LinhTinh item, int add) {
        try {
            Message message = new Message(27);
            message.writer().writeByte(1);
            message.writer().writeByte(item.num > 1 ? 3 : 1);
            message.writer().writeByte(item.id);
            if (item.num > 1) {
                message.writer().writeByte(add);
            }
            message.writer().writeUTF(item.name);
            message.writer().writeUTF(item.detail);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void downLinhTinh(int id, int down) {
        try {
            Message message = new Message(27);
            message.writer().writeByte(1);
            message.writer().writeByte(0);
            message.writer().writeInt(id);
            message.writer().writeByte(down);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void removeEquip(Equip... equips) {
        try {
            Message message = new Message(27);
            message.writer().writeByte(equips.length);
            for (Equip equip : equips) {
                message.writer().writeByte(0);
                message.writer().writeInt(equip.dbKey|0x10000);
                message.writer().writeByte(1);
            }
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void requestRenewal(int dbKey, String str) {
        try {
            Message message = new Message(-25);
            message.writer().writeInt(dbKey|0x10000);
            message.writer().writeUTF(str);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void updateItem(Item item, int xu, int luong) {
        try {
            Message message = new Message(72);
            message.writer().writeByte(1);
            message.writer().writeByte(item.id);
            message.writer().writeByte(item.num);
            message.writer().writeInt(xu);
            message.writer().writeInt(luong);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void loadInnfo(User user) {
        try {
            Message message = new Message(34);
            message.writer().writeInt(user.id);
            message.writer().writeUTF(user.name);
            message.writer().writeInt((int) user.xu);
            message.writer().writeByte(user.glass().level);
            message.writer().writeByte(user.glass().getPercentLevel());
            message.writer().writeInt((int) user.luong);
            message.writer().writeInt(user.glass().exp);
            message.writer().writeInt(user.glass().getExpMaxLevel());
            message.writer().writeInt(user.cup);
            int top = 1;
            if(top > 0) {
                message.writer().writeUTF("Top "+(top<10000?top:((top/1000)+"k+")));
            } else {
                message.writer().writeUTF("Chưa có hạng");
            }
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void loadMission(Mission... missions) {
        try {
            Message message = new Message(-23);
            for (Mission mission : missions) {
                message.writer().writeByte(mission.id);
                message.writer().writeByte(mission.level);
                message.writer().writeUTF(mission.name);
                message.writer().writeUTF(mission.reward);
                message.writer().writeInt(mission.require);
                message.writer().writeInt(mission.have > mission.require ? mission.require : mission.have);
                message.writer().writeBoolean(mission.isComplete);
            }
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void addCup(int cup, int add) {
        try {
            Message message = new Message(-24);
            message.writer().writeByte(add);
            message.writer().writeInt(cup);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void loadFriend(User... users) {
        try {
            Message message = new Message(29);
            for (User user : users) {
                if (user != null) {
                    message.writer().writeInt(user.id);
                    message.writer().writeUTF(user.name);
                    message.writer().writeInt((int) user.xu);
                    message.writer().writeByte(user.glass().id);
                    message.writer().writeShort(user.clan);
                    message.writer().writeByte(user.session != null ? 1 : 0);
                    message.writer().writeByte(user.glass().level);
                    message.writer().writeByte(user.glass().getPercentLevel());
                    for (int i = 0; i  < user.glass().equipID.length; i++) {
                        message.writer().writeShort(user.glass().equipID[i]);
                    }
                }
            }
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void findUser(int id, String name) {
        try {
            Message message = new Message(36);
            message.writer().writeInt(id);
            message.writer().writeUTF(name);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void deleteFriend(int id) {
        try {
            Message message = new Message(33);
            message.writer().writeInt(id);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void messageTo(int id, String name, String str) {
        try {
            Message message = new Message(5);
            message.writer().writeInt(id);
            message.writer().writeUTF(name);
            message.writer().writeUTF(str);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void messageWorld(String str) {
        try {
            Message message = new Message(46);
            message.writer().writeUTF(str);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void loadRoomInfo() {
        try {
            Message message = new Message(6);
            for (RoomInfo room : RoomInfo.rooms) {
                message.writer().writeByte(room.id);
                message.writer().writeByte(room.stat);
                message.writer().writeByte(0);
                message.writer().writeByte(room.type);
            }
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void loadRoomInfo(RoomInfo... roomInfos) {
        try {
            Message message = new Message(-28);
            message.writer().writeByte(0);//raction
            for (RoomInfo room : roomInfos) {
                message.writer().writeByte(0);
                message.writer().writeByte(0);
                message.writer().writeByte(0);
                message.writer().writeByte(0);
                message.writer().writeByte(8);
                message.writer().writeInt(100);
                message.writer().writeUTF("Cái");
            }
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void loadRoomWaits(byte roomId, RoomWait[] roomWaits) {
        try {
            Message message = new Message(7);
            message.writer().writeByte(roomId);
            for (RoomWait roomWait : roomWaits) {
                if (roomWait.numPlayer < roomWait.playerLimit) {
                    if (!roomWait.started) {
                        message.writer().writeByte(roomWait.boardID);
                        message.writer().writeByte(roomWait.numPlayer);
                        message.writer().writeByte(roomWait.playerLimit);
                        message.writer().writeBoolean(!roomWait.pass.isEmpty());
                        message.writer().writeInt(roomWait.money);
                        message.writer().writeBoolean(roomWait.started);
                        message.writer().writeUTF(roomWait.name);
                        message.writer().writeByte(roomWait.mode);
                    }
                }
            }
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void loadRoomWait(RoomWait roomWait) {
        Message message = new Message(8);
        try {
            message.writer().writeInt(roomWait.userID);
            message.writer().writeInt(roomWait.money);
            message.writer().writeByte(0);
            message.writer().writeByte(0);
            for (User player : roomWait.players) {
                if (player != null) {
                    message.writer().writeInt(player.id);
                    message.writer().writeShort(player.clan);
                    message.writer().writeUTF(player.name);
                    message.writer().writeInt(0);//khong biet
                    message.writer().writeByte(player.glass().level);
                    message.writer().writeByte(player.glass().id);
                    for (int i = 0; i < player.getData().length; i++) {
                        message.writer().writeShort(player.getData()[i]);
                    }
                    message.writer().writeBoolean(player.ready);
                } else {
                    message.writer().writeInt(-1);
                }
            }
            this.session.sendMessage(message);
        } catch (IOException ex){
        } finally {
            message.cleanup();
        }
    }
    
    public void addPlayer(int index, User player) {
        try {
            Message message = new Message(12);
            message.writer().writeByte(index);
            message.writer().writeInt(player.id);
            message.writer().writeShort(player.clan);
            message.writer().writeUTF(player.name);
            message.writer().writeByte(player.glass().level);
            message.writer().writeByte(player.glass().id);
            for (int i = 0; i < player.getData().length; i++) {
                message.writer().writeShort(player.getData()[i]);
            }
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void updateRoomWait(RoomWait roomWait) {
        try {
            Message message = new Message(76);
            message.writer().writeByte(roomWait.roomID);
            message.writer().writeByte(roomWait.boardID);
            message.writer().writeUTF(roomWait.name);
            message.writer().writeByte(roomWait.type);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void changeMap(byte mapID) {
        try {
            Message message = new Message(75);
            message.writer().writeByte(mapID);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void leaveRoomWait(int userID, RoomWait roomWait) {
        try {
            Message message = new Message(14);
            message.writer().writeInt(userID);
            message.writer().writeInt(roomWait.userID);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void changeTeam(int userID, int index) {
        try {
            Message message = new Message(71);
            message.writer().writeInt(userID);
            message.writer().writeByte(index);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public static final short[] idNotColision = new short[]{70, 71, 73, 74, 75, 77, 78, 79, 97};
    
    public void idNotColision() {
        try {
            Message message = new Message(92);
            message.writer().writeShort(idNotColision.length);
            for(int i = 0; i < idNotColision.length; i++) {
                message.writer().writeShort(idNotColision[i]);
            }
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void ready(User user) {
        try {
            Message message = new Message(16);
            message.writer().writeInt(user.id);
            message.writer().writeBoolean(user.ready);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void addFriend(byte resule) {
        try {
            Message message = new Message(32);
            message.writer().writeByte(resule);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void kickRoomWait(byte index, int userID, String str) {
        try {
            Message message = new Message(11);
            message.writer().writeShort(index);
            message.writer().writeInt(userID);
            message.writer().writeUTF(str);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void setMoneyRoomWait(int xu) {
        try {
            Message message = new Message(19);
            message.writer().writeShort(0);
            message.writer().writeInt(xu);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void inviteJoinRoomWait(String str, RoomWait roomWait) {
        try {
            Message message = new Message(78);
            message.writer().writeBoolean(false);
            message.writer().writeUTF(str);
            message.writer().writeByte(roomWait.roomID);
            message.writer().writeByte(roomWait.boardID);
            message.writer().writeUTF(roomWait.pass);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void tabInvite(ArrayList<User> users) {
        try {
            Message message = new Message(78);
            message.writer().writeBoolean(true);
            message.writer().writeByte(users.size());
            for (User user : users) {
                message.writer().writeUTF(user.name);
                message.writer().writeInt(user.id);
                message.writer().writeByte(user.glass().id);
                message.writer().writeInt((int) user.xu);
                message.writer().writeByte(user.glass().level);
                message.writer().writeByte(user.glass().getPercentLevel());
                for(int j = 0; j < user.getData().length; j++) {
                    message.writer().writeShort(user.getData()[j]);
                }
            }
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void loadInfoMap(MapData mapData) {
        try {
            Message message = new Message(20);
            //Luyện tập
//            for (int i = 0; i < 5; i++) {
//                message.writer().writeShort(id);
//            }
            message.writer().writeByte(0);
            message.writer().writeByte(mapData.maxSecondTurn);
            message.writer().writeShort(mapData.teamPoint);
            if (mapData.team != -1) {
                message.writer().writeByte(mapData.team);
            }
            for (Player player : mapData.players) {
                if (player != null) {
                    message.writer().writeShort(player.x);
                    message.writer().writeShort(player.y);
                    message.writer().writeShort(player.hpMax);
                } else {
                    message.writer().writeShort(-1);
                }
            }
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void endTheWar(int typeComplete, int money) {
        try {
            Message message = new Message(50);
            message.writer().writeByte(typeComplete);
            message.writer().writeByte(0);
            message.writer().writeInt(money);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void addPlayer(ArrayList<Player> players) {
        try {
            Message message = new Message(89);
            message.writer().writeByte(players.size());
            for (Player player : players) {
                message.writer().writeInt(player.userID);
                message.writer().writeUTF(player.name);
                message.writer().writeInt(player.hpMax);
                message.writer().writeByte(player.glassID);
                message.writer().writeShort(player.x);
                message.writer().writeShort(player.y);
            }
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void setXY(int index, short x, short y) {
        try {
            Message message = new Message(53);
            message.writer().writeByte(index);
            message.writer().writeShort(x);
            message.writer().writeShort(y);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void cancelInvisible(int index) {
        try {
            Message message = new Message(80);
            message.writer().writeByte(index);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void blind(int index, int type) {
        try {
            Message message = new Message(106);
            message.writer().writeByte(type);
            message.writer().writeByte(index);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void freeze(int index, int type) {
        try {
            Message message = new Message(107);
            message.writer().writeByte(type);
            message.writer().writeByte(index);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void poison(int index) {
        try {
            Message message = new Message(108);
            message.writer().writeByte(index);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void updateAngry(int index, int angry) {
        try {
            Message message = new Message(113);
            message.writer().writeByte(index);
            message.writer().writeByte(angry);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void updateHP(int index, int hp, int pixel) {
        try {
            Message message = new Message(51);
            message.writer().writeByte(index);
            message.writer().writeShort(hp);
            message.writer().writeByte(pixel);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void addTimeBomb(int id, int x, int y) {
        try {
            Message message = new Message(109);
            message.writer().writeByte(0);
            message.writer().writeByte(id);
            message.writer().writeInt(x);
            message.writer().writeInt(y);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void explodeTimeBomb(int id) {
        try {
            Message message = new Message(109);
            message.writer().writeByte(1);
            message.writer().writeByte(id);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void addPlayer(Player... players) {
        try {
            Message message = new Message(89);
            message.writer().writeByte(players.length);
            for (Player player : players) {
                message.writer().writeInt(player.userID);
                message.writer().writeUTF(player.name);
                message.writer().writeInt(player.hpMax);
                message.writer().writeByte(player.glassID);
                message.writer().writeShort(player.x);
                message.writer().writeShort(player.y);
            }
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void setTurn(int index) {
        try {
            Message message = new Message(24);
            message.writer().writeByte(index);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void setWind(int x, int y) {
        try {
            Message message = new Message(25);
            message.writer().writeByte(x);
            message.writer().writeByte(y);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void chat(int id, String str) {
        try {
            Message message = new Message(9);
            message.writer().writeInt(id);
            message.writer().writeUTF(str);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void changeLocation(int index, short x, short y) {
        try {
            Message message = new Message(21);
            message.writer().writeByte(index);
            message.writer().writeShort(x);
            message.writer().writeShort(y);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void changeLocationFly(int index, int x, int y) {
        try {
            Message message = new Message(93);
            message.writer().writeByte(index);
            message.writer().writeShort(x);
            message.writer().writeShort(y);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void shoot(Gun gun) {
        try {
            Message message = new Message(22);
            //typeshoot
            message.writer().writeByte(gun.typeshoot);
            message.writer().writeByte(gun.isPow);
            message.writer().writeByte(gun.index);
            message.writer().writeByte(gun.bulletId);
            message.writer().writeShort(gun.gunX);
            message.writer().writeShort(gun.gunY);
            message.writer().writeShort(gun.ang);
            // Apa or chicky: send force 2
            if(gun.bulletId == 17 || gun.bulletId == 19) {
                message.writer().writeByte(gun.force2);
            }
            // dan laser
            if(gun.bulletId == 14 || gun.bulletId == 40) {
                // Goc
                message.writer().writeByte(0);
                // Null byte
                message.writer().writeByte(0);
            }
            // Send goc
            if(gun.bulletId == 44 || gun.bulletId == 45 || gun.bulletId == 47) {
                message.writer().writeByte(0);
            }
            // So lan ban
            message.writer().writeByte(gun.nshoot);
            // So dan
            message.writer().writeByte(gun.bullets.size());
            for (int i = 0; i < gun.bullets.size(); i++) {
                message.writer().writeShort(gun.bullets.get(i).frames.size());
                for (int j = 0; j < gun.bullets.get(i).frames.size(); j++) {
                    if (j == 0 || gun.typeshoot == 1) {
                        message.writer().writeShort(gun.bullets.get(i).frames.get(j).fX);
                        message.writer().writeShort(gun.bullets.get(i).frames.get(j).fY);
                    } else {
                        if(gun.bulletId == 49 && (j == gun.bullets.get(i).frames.size() - 1)) {
                            message.writer().writeShort(gun.bullets.get(i).frames.get(j).fX);
                            message.writer().writeShort(gun.bullets.get(i).frames.get(j).fY);
                        }
                        // Buoc nhay
                        message.writer().writeByte(gun.bullets.get(i).frames.get(j).jumpX);
                        message.writer().writeByte(gun.bullets.get(i).frames.get(j).jumpY);
                    }
                }
                if(gun.bulletId == 48) {
                    // Lent
                    message.writer().writeByte(1);
                    for(int j=0; j<1; j++) {
                        // xHit, yHit
                        message.writer().writeShort(0);
                        message.writer().writeShort(0);
                    }
                }
            }
            // Type Sieu cao
            if(gun.isSuper == 2 && !gun.setEff.isEmpty()) {
                message.writer().writeByte(1);
                // X, Y super
                message.writer().writeShort(gun.bullets.get(0).maxYX);
                message.writer().writeShort(gun.bullets.get(0).maxY);
            } else {
                message.writer().writeByte(0);
            }
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void useItem(int index, int itemId) {
        try {
            Message message = new Message(26);
            message.writer().writeByte(index);
            message.writer().writeByte(itemId);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
    public void updateLuck(int index) {
        try {
            Message message = new Message(100);
            message.writer().writeByte(index);
            this.session.sendMessage(message);
        } catch (IOException ex){}
    }
    
}
