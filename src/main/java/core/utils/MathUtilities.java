package core.utils;

import org.jetbrains.annotations.NotNull;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class MathUtilities {

    public static SecureRandom RANDOM;

    static {
        try {
            RANDOM = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static int random(int range) {
        return (int)(Math.random() * (double)(range + 1));
    }

    public static int random(int startingRange, int endRange) {
        int random;
        for(random = (int)(Math.random() * (double)(endRange + 1)); random < startingRange; random = (int)(Math.random() * (double)(endRange + 1))) {
        }

        return random;
    }

    public static double secureRandom(int seedCount) {
        byte[] seed = RANDOM.generateSeed(seedCount);
        RANDOM.setSeed(seed);
        return RANDOM.nextDouble();
    }

    public static long stringToLong(@NotNull String string) {
        long l = 0L;
        for (int i = 0; i < string.length() && i < 12; i++) {
            char c = string.charAt(i);
            l *= 37L;
            if (c >= 'A' && c <= 'Z')
                l += (1 + c) - 65;
            else if (c >= 'a' && c <= 'z')
                l += (1 + c) - 97;
            else if (c >= '0' && c <= '9')
                l += (27 + c) - 48;
        }
        while (l % 37L == 0L && l != 0L)
            l /= 37L;
        return l;
    }
}
