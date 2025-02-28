package mobiarmy.server;

import java.util.ArrayList;
import java.util.HashMap;
import mobiarmy.Util;
import mobiarmy.war.Boss.bullet.BulletTrajectory;
import mobiarmy.war.PathSimulator;
import mobiarmy.war.Player;
import mobiarmy.war.RoomInfo;
import mobiarmy.war.RoomWait;

/**
 *
 * @author Văn Tú
 */
public class Bot extends User {
    
    public boolean remove = false;
    public Object[] invited = null;
    public long waitJoinAnyBoard;
    public long waitInvited;
    public long waitReady;
    public long waitLeave;
    private boolean isLand = true;
    private final ArrayList<Player> selecteds = new ArrayList<>();

    public Bot(int id, String name) {
        super(id, name);
    }
    
    @Override
    public void update() {
        super.update();
        //Nếu bot ở phòng chờ sẽ sẵn sàng chơi
        if (System.currentTimeMillis() > this.waitReady) {
            this.waitReady = System.currentTimeMillis() + Util.nextInt(5000);
            //Nếu chủ phòng sẽ bắt đầu nếu trong map có người thật
            if (super.roomWait != null && !super.roomWait.started) {
                if (super.roomWait.userID == super.id) {
                    for (User player : super.roomWait.players) {
//                        if (player != null && !(player instanceof Bot)) {
                            super.startGame();
                            break;
//                        }
                    }
                } else if (!super.ready) {
                    super.ready();
                }
            }
        }
        //Nếu bot ở phòng chờ lâu sẽ rời đi
        if (System.currentTimeMillis() > this.waitLeave) {
            this.waitLeave = System.currentTimeMillis() + Util.nextInt(120000);
            if (super.roomWait != null && !super.roomWait.started) {
                if (super.ready) {
                    super.ready();
                }
                super.leaveRoomWait();
            }
        }
        //Nếu phòng chờ toàn bot sẽ đi ra
        if (super.roomWait != null && super.id == super.roomWait.userID) {
            //super.leaveRoomWait();
        }
        //Nếu trong trận và đến lượt
        if (super.roomWait != null && super.roomWait.started && super.roomWait.mapData.getTurn() == super.index) {
            Player me = super.roomWait.mapData.players[super.index];
            if (!me.isShoot && System.currentTimeMillis() > me.mapData.timeUntilAction2) {
                //Dùng item
                if (!me.isUseItem) {
                    //Kĩ năng đặc biệt
                    if (((float) me.hp / me.hpMax) * 100 < 70 && me.angry >= 100) super.useItem((byte)100);
                    //Dùng hp
                    else if (((float) me.hp / me.hpMax) * 100 < 70 && me.findUnusedItemById(0) != null) super.useItem((byte)0);
                }
                //Bắn
                if (me.trajectory == null) {
                    if (this.selecteds.isEmpty()) {
                        if (me.buocdi == 0) {
                            this.isLand = true;
                        }
                        PathSimulator simulator = new PathSimulator(me.x, me.y, me.x + Util.nextInt(-1, 1), me.y, me.mapData);
                        simulator.simulate();
                        if (simulator.pathFrames.size() > 1) {
                            this.moveLocation(simulator.pathFrames.get(simulator.pathFrames.size() - 1)[0], simulator.pathFrames.get(simulator.pathFrames.size() - 1)[1]);
                        }
                        //Tìm đối thủ
                        for (Player player : super.roomWait.mapData.players) {
                            if (player != null && !player.isDie) {
                                if (super.roomWait.mapData.isFightBoss) {
                                    if (player.isBoss) {
                                        this.selecteds.add(player);
                                    }
                                } else {
                                    if (me.team != player.team) {
                                        this.selecteds.add(player);
                                    }
                                }
                            }
                        }
                    }
                    if (!this.selecteds.isEmpty()) {
                        Player player = this.selecteds.remove(Util.nextInt(this.selecteds.size()));
                        if (me.glassID == 3 || me.glassID == 2) {
                            me.trajectory = new BulletTrajectory(
                                    super.roomWait.mapData, 
                                    super.index, me.bulletIdByGlassID(),
                                    me.x, me.y,
                                    me.width, me.height,
                                    player.x - player.width / 2, player.y - player.height,
                                    player.width, player.height, 45, 1,
                                    this.isLand, true);
                        } else {
                            me.trajectory = new BulletTrajectory(
                                    super.roomWait.mapData,
                                    super.index, me.bulletIdByGlassID(),
                                    me.x, me.y,
                                    me.width, me.height,
                                    player.x - player.width / 2, player.y - player.height,
                                    player.width, player.height,
                                    me.user.glass().angle, 10,
                                    this.isLand, true);
                        }
                        me.trajectory.start();
                    }
                } else if (me.trajectory.complate) {
                    if (me.trajectory.place) {
                        this.selecteds.clear();
                        super.shoot(me.bulletIdByGlassID(), me.x, me.y, (short)me.trajectory.ang, (byte)me.trajectory.force, (byte)me.trajectory.force2, me.nshoot);
                        me.mapData.isTurn = true;
                    } else {
                        if (me.buocdi < me.theluc) {
                            PathSimulator simulator = new PathSimulator(me.x, me.y, me.trajectory.tX, me.trajectory.tY, me.mapData);
                            simulator.simulate();
                            if (simulator.pathFrames.size() > 1) {
                                this.moveLocation(simulator.pathFrames.get(simulator.pathFrames.size()-1)[0], simulator.pathFrames.get(simulator.pathFrames.size()-1)[1]);
                                if (me.buocdi < me.theluc) {
                                    simulator = new PathSimulator(me.x, me.y, me.x + Util.nextT(-me.theluc, me.theluc), me.y, me.mapData);simulator.simulate();
                                    if (simulator.pathFrames.size() > 1) {
                                        this.moveLocation(simulator.pathFrames.get(simulator.pathFrames.size()-1)[0], simulator.pathFrames.get(simulator.pathFrames.size()-1)[1]);
                                    }
                                }
                            } else {
                                this.isLand = false;
                            }
                        } else {
                            this.isLand = false;
                        }
                        me.trajectory = null;
                    }
                }
            }
        }
        //Mời vào phòng chờ
        if (System.currentTimeMillis() > this.waitInvited) {
            this.waitInvited = System.currentTimeMillis()+ Util.nextInt(3000);
            if (this.invited != null) {
                super.joinRoomWait((byte)this.invited[0], (byte)this.invited[1], (String) this.invited[2]);
                this.invited = null;
            }
        }
        //Tìm phòng ngẫu nhiên người chơi
        if (System.currentTimeMillis() > this.waitJoinAnyBoard) {
            this.waitJoinAnyBoard = System.currentTimeMillis() + Util.nextInt(3000, 600000);
            if (super.roomWait == null) {
//                this.joinRandom();
            }
        }
        //Xóa bot
        if (this.remove) {
            this.remove();
        }
    }
    
    private void joinRandom() {
        
        ArrayList<RoomWait> roomWaits = new ArrayList<>();
        for (RoomInfo entry : RoomInfo.entrys) {
            for (RoomWait wait : entry.roomWaits) {
                if (!wait.started && wait.pass.isEmpty() && wait.money <= this.xu && wait.playerLimit > wait.numPlayer && wait.numPlayer <= 7 && wait.type == 0) {
                    roomWaits.add(wait);
                }
            }
        }
        if (!roomWaits.isEmpty()) {
            RoomWait wait = roomWaits.get(Util.nextInt(roomWaits.size()));
            this.joinRoomWait(wait.roomID, wait.boardID, wait.pass);
        }
    }
    
    public void remove() {
        super.leaveRoomWait();
        bots.add(this);
    }
    
    public static int baseID = Integer.MIN_VALUE;
    
    public static Bot addBot(String name, int glassID, int exp, short[] equipID) {
        Bot bot = new Bot(baseID++, name != null ? name : Util.getRandomCharacters("abcdefghijklmnopqrstuvwxyz0123456789", Util.nextInt(5, 10)));
        bot.luong = Util.nextInt(1000);
        bot.xu = Util.nextInt(1000000);
        bot.getGlass(glassID).isOpen = true;
        bot.selectGlass((byte) glassID);
        bot.glass().addExp(exp, false);
        //Công điểm ngẫu nhiên nếu có
        if (bot.glass().point > 0) {
            int ability[] = new int[5];
            for (int i = 1; i <= bot.glass().point; i++) {
                ability[Util.nextT(0, 1, 0, 3, 1)]++;
            }
            bot.glass().upadtePoint(ability);
        }
        //Mặc trang bị nếu có null sẽ mặc ngẫu nhiên theo level
        if (equipID != null) {
            for (int i = 0; i < equipID.length; i++) {
                Equip equip = Equip.get((byte) glassID, equipID[i]);
                //Bỏ qua nếu không có
                if (equip != null) {
                    equip = equip.deepCopy();
                    equip.renewalDate = System.currentTimeMillis();
                    equip.isUse = true;
                    bot.addEquip(equip);
                }
            }
        } else {
            //Lấy ngẫu nhiên trong trang bị cửa hàng để mặc vào
            ArrayList<Equip> equips = ShopEquipment.generate((byte) glassID);
            ArrayList<Equip>[] es = new ArrayList[]{new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()};
            for (Equip equip : equips) {
                if (equip.level <= bot.glass().level && equip.vip == 0) {
                    es[equip.type].add(equip);
                }
            }
            for (ArrayList<Equip> esList : es) {
                if (!esList.isEmpty()) {
                    Equip equip = esList.get(Util.nextInt(esList.size()));
                    equip = equip.deepCopy();
                    equip.renewalDate = System.currentTimeMillis();
                    equip.isUse = true;
                    bot.addEquip(equip);
                }
            }
        }
        //Thêm ngọc để chuẩn bị ép vào trang bị
        for (int i = 0; i < 100; i++) {
            bot.addLinhTinh(Util.nextT(10, 40, 10, 30, 40) + Util.nextInt(7, 9), 1);
        }
        int [] old = bot.glass().createAbility();
        for (Equip equip : bot.equips) {
            while (equip.slot() > 0) {
                kt: {
                    for (LinhTinh linhtinh : bot.linhtinhs) {
                        if (linhtinh.id < 50) {
                            bot.getSelect().reset();
                            bot.getSelect().addElement(0, equip.dbKey, 1);
                            bot.getSelect().addElement(1, linhtinh.id, 1);
                            bot.getSelect().make();
                            bot.getConfirm().confirm();
                            break kt;
                        }
                    }
                    break;
                }
            }
        }
        bot.glass().updateAll();
        int pre[] = bot.glass().createAbility();
        add(bot);
        return bot;
    }

    // Tìm Bot theo ID
    public static Bot findById(int id) {
        return bot_id.get(id);
    }
    
    public static void add(Bot bot) {
        bots.add(bot);
        bot_id.put(bot.id, bot);
        bot_name.put(bot.name, bot);
    }
    
    public static ArrayList<Bot> bots = new ArrayList<>();
    public static HashMap<Integer, Bot> bot_id = new HashMap<>();
    public static HashMap<String, Bot> bot_name = new HashMap<>();
    
    public static void updateBot() {
        for (int i = bots.size() - 1; i >= 0; i--) {
            Bot bot = bots.get(i);
            if (!bot.lock) {
                bot.update();
            }
        }
    }
    
    public static void generateBot() {
        for (int i = 0; i < 5000; i++) {
            Bot.addBot(null, Util.nextInt(10), Util.nextInt(50000000), null);
//            Bot.addBot(null, Util.nextInt(10), Util.nextInt(100), null);
        }
    }

}
