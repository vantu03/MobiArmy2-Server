package mobiarmy.war;

/**
 *
 * @author Văn Tú
 */
public class TimeBomb {
    
    public int id;
    public int leadIndex;
    public int x;
    public int y;
    public int count;
    public int att;
    public int radius;
    public MapData mapData;
    
    public TimeBomb(MapData mapData, int id, int leadIndex, int att, int x, int y, int count, int radius) {
        this.id = id;
        this.leadIndex = leadIndex;
        this.x = x;
        this.y = y;
        this.count = count;
        this.att = att;
        this.radius = radius;
        this.mapData = mapData;
    }
    
    public void update() {
        while(this.y < this.mapData.height + 200) {
            for (int i = 0; i < 14; i++) {
                if (this.mapData.isCollisionMap((this.x - 7) + i, this.y)) {
                    return;
                }
            }
            this.y++;
        }
    }
    
}
