package mobiarmy.server;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import javax.imageio.ImageIO;

/**
 *
 * @author Văn Tú
 */
public class GameData {
    
    public static Pack mapIcon;
    public static Pack player;
    
    public static HashMap<String, byte[]> files = new HashMap<>();
    
    public static BufferedImage MANGNHEN;
    public static BufferedImage[] holeIMask = new BufferedImage[10];
    private final static short sinData[];
    private final static short cosData[];
    private final static int tanData[];
    
    static  {
        sinData = new short[] { 0, 18, 36, 54, 71, 89, 107, 125, 143, 160, 178, 195, 213, 230, 248, 265, 282, 299, 316, 333, 350, 367, 384, 400, 416, 433, 449, 465, 481, 496, 512, 527, 543, 558, 573, 587, 602, 616, 630, 644, 658, 672, 685, 698, 711, 724, 737, 749, 761, 773, 784, 796, 807, 818, 828, 839, 849, 859, 868, 878, 887, 896, 904, 912, 920, 928, 935, 943, 949, 956, 962, 968, 974, 979, 984, 989, 994, 998, 1002, 1005, 1008, 1011, 1014, 1016, 1018, 1020, 1022, 1023, 1023, 1024, 1024};
        cosData = new short[91];
        tanData = new int[91];
        for (int i = 0; i <= 90; i++) {
            cosData[i] = sinData[90 - i];
            if (cosData[i] == 0) {
                tanData[i] = 0x7fffffff;
            } else {
                tanData[i] = (sinData[i] << 10) / cosData[i];
            }
        }
    }
    
    public static void loadHole() {
        try {
            MANGNHEN = ImageIO.read(new File("res/effect/hole/mangnhen.png"));
            holeIMask[0] = ImageIO.read(new File("res/effect/hole/h32x26.png"));
            holeIMask[1] = ImageIO.read(new File("res/effect/hole/smallhole.png"));
            holeIMask[2] = ImageIO.read(new File("res/effect/hole/smallhole.png"));
            holeIMask[3] = ImageIO.read(new File("res/effect/hole/h36x30.png"));
            holeIMask[4] = ImageIO.read(new File("res/effect/hole/rocket.png"));
            holeIMask[5] = ImageIO.read(new File("res/effect/hole/rangehole.png"));
            holeIMask[6] = ImageIO.read(new File("res/effect/hole/hrangcua.png"));
            holeIMask[7] = ImageIO.read(new File("res/effect/hole/hgrenade.png"));
            holeIMask[8] = ImageIO.read(new File("res/effect/hole/h14x12.png"));
            holeIMask[9] = ImageIO.read(new File("res/effect/hole/h55x50.png"));
        } catch (IOException e) {
            System.exit(0);
        }
    }
    
    public static byte[] getCache(String url) {
        byte[] data = files.get(url);
        if (data == null) {
            try {
                data = readFile(new File(url));
            } catch (IOException ex) {
            }
        }
        return data;
    }
    
    public static void loadMapIcon() throws IOException {
        File[] files = new File("res/map/icon").listFiles();
        mapIcon = new Pack();
        for (File file : files) {
            mapIcon.addEntry(file.getName(), readFile(file));
        }
    }
    
    public static void loadLayer() throws IOException {
        File[] files = new File("res/player").listFiles();
        player = new Pack();
        for (File file : files) {
            player.addEntry(file.getName(), readFile(file));
        }
    }
    
    private static byte[] readFile(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            byte[] dataChunk = new byte[1024];  // đọc từng phần 1KB
            int bytesRead;
            while ((bytesRead = fis.read(dataChunk)) != -1) {
                buffer.write(dataChunk, 0, bytesRead);
            }
            return buffer.toByteArray();
        }
    }
    
    public static byte getHoleType(int bulletType) {
        return switch (bulletType) {
            case 0 -> 3;
            case 1 -> 1;
            case 2 -> 0;
            case 3 -> 9;
            case 6 -> 6;
            case 7 -> 7;
            case 9 -> 5;
            case 10 -> 4;
            case 11 -> 2;
            case 12 -> 6;
            case 15 -> 7;
            case 17 -> 2;
            case 18 -> 2;
            case 19 -> 2;
            case 20 -> 0;
            case 21 -> 2;
            case 22 -> 7;
            case 24 -> 3;
            case 25 -> 8;
            case 27 -> 1;
            case 30 -> 0;
            case 31 -> 7;
            case 32 -> 3;
            case 37 -> 7;
            case 42 -> 7;
            case 43 -> 7;
            case 44 -> 2;
            case 45 -> 7;
            case 47 -> 8;
            case 48 -> 3;
            case 52 -> 3;
            case 57 -> 7;
            default -> 0;
        };
    }
    
    public static int radius(int bulletId) {
        return switch (bulletId) {
            case 0 -> 18;
            case 1 -> 11;
            case 2 -> 16;
            case 3 -> 110;
            case 6 -> 20;
            case 7 -> 28;
            case 8 -> 20;
            case 9 -> 16;
            case 10 -> 17;
            case 11 -> 11;
            case 12 -> 20;
            case 15 -> 28;
            case 17 -> 11;
            case 18 -> 11;
            case 19 -> 11;
            case 20 -> 16;
            case 21 -> 11;
            case 22 -> 28;
            case 24 -> 18;
            case 25 -> 7;
            case 26 -> 18;
            case 27 -> 11;
            case 29 -> 11;
            case 30 -> 16;
            case 31 -> 30;
            case 32 -> 30;
            case 33 -> 11;
            case 35 -> 450;
            case 37 -> 150;
            case 40 -> 40;
            case 42 -> 80;
            case 43 -> 28;
            case 44 -> 11;
            case 45 -> 28;
            case 47 -> 7;
            case 48 -> 18;
            case 49 -> 16;
            case 50 -> 70;
            case 51 -> 30;
            case 52 -> 18;
            case 54 -> 30;
            case 55 -> 30;
            case 56 -> 20;
            case 57 -> 28;
            case 59 -> 16;
            default -> 0;
        };
    }

    public static int toArg0_360(int ang) {
        if (ang >= 360) {
            ang -= 360;
        }
        if (ang < 0) {
            ang += 360;
        }
        return ang;
    }

    public static int sin(int ang) {
        if ((ang = toArg0_360(ang)) >= 0 && ang < 90) {
            return sinData[ang];
        }
        if (ang >= 90 && ang < 180) {
            return sinData[180 - ang];
        }
        if (ang >= 180 && ang < 270) {
            return -sinData[ang - 180];
        }
        return -sinData[360 - ang];
    }

    public static int cos(int ang) {
        if ((ang = toArg0_360(ang)) >= 0 && ang < 90) {
            return cosData[ang];
        }
        if (ang >= 90 && ang < 180) {
            return -cosData[180 - ang];
        }
        if (ang >= 180 && ang < 270) {
            return -cosData[ang - 180];
        }
        return cosData[360 - ang];
    }

    public static int getArg(int cos, int sin) {
        if(cos == 0) {
            return sin == 0 ? 0 : (sin < 0 ? 270 : 90);
        }
        int arg;
        label2: {
            arg = Math.abs((sin << 10) / cos);
            for (int i = 0; i <= 90; i++) {
                if(tanData[i] < arg) {
                    continue;
                }
                arg = i;
                break label2;
            }
            arg = 0;
        }
        if (sin >= 0 && cos < 0) {
            arg = 180 - arg;
        }
        if (sin < 0 && cos < 0) {
            arg += 180;
        }
        if (sin < 0 && cos >= 0) {
            arg = 360 - arg;
        }
        return arg;
    }
}

