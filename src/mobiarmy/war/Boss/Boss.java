package mobiarmy.war.Boss;

import java.util.ArrayList;
import mobiarmy.war.Player;

/**
 *
 * @author Văn Tú
 */
public class Boss extends Player {
    
    public ArrayList<Command> commands = new ArrayList<>();
    
    public Command getCommand(int id) {
        for (Command command : this.commands) {
            if (command.id == id) {
                return command;
            }
        }
        return null;
    }
    
    public void addCommand(int id, Object... objects) {
        this.commands.add(new Command(id, objects));
    }
    
    public class Command {
        
        public int id;
        public Object[] objects;

        public Command(int id, Object... objects) {
            this.id = id;
            this.objects = objects;
        }
        
    }
    
    public Boss(String name, byte glassID, int hp, int att, int def, int luck, int tea, int width, int height, int x, int y) {
        super(null, null, name, glassID, (byte)-1, hp, att, def, luck, tea, width, height, null);
        super.x = (short) x;
        super.y = (short) y;
        super.isBoss = true;
        super.isDie = false;
        super.typePlayer = 1;
    }
    
    @Override
    public void updateTurn() {
        super.updateTurn();
    }
    
    @Override
    public void update() {
        if (!this.commands.isEmpty()) {
            for (Command command : this.commands) {
                switch (command.id) {
                    //Di chuyển
                    case 1 -> {
                        if (super.mapData.getTurn() == super.index) {
                            
                        }
                    }
                }
            }
        }
        super.update();
    }
    
}
