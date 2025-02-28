package mobiarmy.war;

/**
 *
 * @author Văn Tú
 */
public class Tornado {
        
    public int index;
    public int x;
    public int y;
    public int nturn;
    public MapData mapData;
    public int dx = 32;
    public int dy = 9999999;

    public Tornado (int index, int x, int y, int nturn, MapData mapData) {
        this.index = index;
        this.x = x;
        this.y = y;
        this.nturn = nturn;
        this.mapData = mapData;
    }
    
    public void update() {
        if (this.nturn < 0) {
            this.mapData.tornados.remove(this);
        } else {
            this.nturn--;
        }
    }
        
}
