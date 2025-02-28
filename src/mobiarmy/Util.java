package mobiarmy;

import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author Văn Tú
 */
public class Util {
    
    public static int nextInt(int min, int max) {
        if (min == max) return min;
        if (min > max) {
            return ThreadLocalRandom.current().nextInt(max, min);
        }
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    public static <T> T nextT(T ...array) {
        return array[Util.nextInt(array.length)];
    }
    
    public static double nextDouble() {
        return ThreadLocalRandom.current().nextDouble();
    }
    
    public static int nextInt(int bound) {
        return ThreadLocalRandom.current().nextInt(bound);
    }
    
    public static String formatNum(int num) {
        return String.format("%,d", num).replace(",", ".");
    }
    
    public static String getRandomCharacters(String inputString, int numberOfChars) {
        if (numberOfChars > inputString.length()) {
            numberOfChars = inputString.length();
        }
        
        StringBuilder result = new StringBuilder(numberOfChars);
        
        for (int i = 0; i < numberOfChars; i++) {
            int randomIndex = nextInt(inputString.length());
            result.append(inputString.charAt(randomIndex));
        }
        
        return result.toString();
    }
}
