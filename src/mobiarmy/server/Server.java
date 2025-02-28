package mobiarmy.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import mobiarmy.io.Message;
import mobiarmy.war.MapData;
import mobiarmy.war.Player;
import mobiarmy.war.Room;
import mobiarmy.war.RoomInfo;

/**
 *
 * @author Văn Tú
 */
public class Server {
    
    private int port;
    private boolean is_start;
    private ServerSocket server;
    private Thread updateThread;
    public static final ArrayList<Command> CMD = new ArrayList<>();
    
    public static class Command  {
        
        public int cmd;
        public Object[] object;

        public Command(int cmd, Object... object) {
            this.cmd = cmd;
            this.object = object;
        }
        
    }
    
    public static final DBManager dbManager = new DBManager("jdbc:mysql://localhost:3306/army", "root", "");
    
    public Server(int port) {
        this.port = port;
        this.is_start = false;
    }
    
    public void start() {
        if (!this.is_start) {
            try {
                Glass.loadGlass();
                Map.loadMap();
                MapBoss.loadMapBoss();
                Equip.loadEquip();
                Item.loadItem();
                LinhTinh.loadLinhTinh();
                ShopEquipment.loadShopEquipment();
                ShopLinhTinh.loadShopLinhTinh();
                Exp.loadExp();
                Caption.loadCaption();
                Mission.loadMession();
                Room.loadRoom();
                RoomInfo.loadRoomInfo();
                SessionManager.loadUser();
            } catch(SQLException ex) {
                ex.printStackTrace();
            }
            try {
                GameData.loadHole();
                GameData.loadMapIcon();
                GameData.loadLayer();
            } catch(IOException ex) {
                ex.printStackTrace();
            }
            updateThread = new Thread(() -> {
                while (is_start) {
                    update();
                    try {
                        TimeUnit.MILLISECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            
            Bot.generateBot();
            
            try {
                try {
                    System.out.println("Start server port:"+this.port);
                    this.server = new ServerSocket(this.port);
                    this.is_start = true;
                    updateThread.start();
                    for (int i = 0;this.is_start; i++) {
                        Session session = new Session(this.server.accept(), i);
                        SessionManager.addSession(session);
                        session.start();
                    }
                } catch (IOException ex) {
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private void update() {
        ArrayList<Command> commands;
        synchronized (CMD) {
            commands = new ArrayList<>(CMD);
        }
        for (Command command : commands) {
            try {
                switch (command.cmd) {
                    case 0 ->  {
                        Session session = (Session) command.object[0];
                        Message message = (Message) command.object[1];
                        if (session.user != null) {
                            if (session.user.lock) {
                                continue;
                            }
                            session.user.update();
                        }
                        if (session.connected) {
                            session.controlHandler.handleControlMessage(message);
                        }
                    }
                    case 1 -> {
                        MapData mapData = (MapData) command.object[0];
                        //Giải phóng hành động
                        mapData.roomWait.lock = false;
                        for (Player player : mapData.players) {
                            if (player != null && player.user != null) {
                                player.user.lock = false;
                            }
                        }
                    }
                }
                removeCommand(command);
            } catch (IOException e) {
            }
        }
        RoomInfo.update();
        Bot.updateBot();
    }
    
    public void stop() {
        if (this.is_start) {
            try {
                this.server.close();
                this.is_start = false;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try {
                SessionManager.saveUsers();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public boolean isRunning() {
        return is_start;
    }
    
    public static void addCommand(Session session, Message message) {
        synchronized (CMD) {
            CMD.add(new Command(0, session, message));
        }
    }
    
    public static void addCommand(int cmd, Object... object) {
        synchronized (CMD) {
            CMD.add(new Command(cmd, object));
        }
    }
    
    public static void removeCommand(Command command) {
        synchronized (CMD) {
            CMD.remove(command);
        }
    }
    
}
