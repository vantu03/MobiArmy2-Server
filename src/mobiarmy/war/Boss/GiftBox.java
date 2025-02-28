package mobiarmy.war.Boss;

/**
 *
 * @author Văn Tú
 */
public class GiftBox extends Boss {
    
    public GiftBox(int x, int y) {
        super("Hộp quà", (byte)23, 1, 0, 0, 0, 0, 24, 24, x, y);
        super.isTurn = false;
        super.isFaction = false;
    }
    
}
