package mobiarmy.war.Boss;

/**
 *
 * @author Văn Tú
 */
public class GiftBox2 extends Boss {
    
    public GiftBox2(int x, int y) {
        super("Hộp quà", (byte)24, 1, 0, 0, 0 ,0 , 24, 24, x, y);
        super.isFly = true;
        super.isTurn = false;
        super.isFaction =false;
    }
    
}
