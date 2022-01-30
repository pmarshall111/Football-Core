package com.footballbettingcore.scrape;

import java.util.Random;

public class ScrapeTimeout {
    /*
     * Creates a random base timeout with the possibility of adding an additional, longer, random timeout to avoid
     * uniform timeouts
     */
    public static int getRandomTimeoutMs() {
        Random r = new Random();
        double rDouble = r.nextDouble();
        int randomBound = r.nextInt(5);
        int rInt = r.nextInt(2 + randomBound);
        double baseTimeout = rDouble * rInt;
        boolean wantBigTimeout = r.nextDouble() < 0.27;
        if (wantBigTimeout) {
            int newAddition = r.nextInt(7);
            return Math.max((int) ((baseTimeout + newAddition*0.97)*1000), 104);
        } else {
            return Math.max((int) ((r.nextDouble() + baseTimeout)*1000), 104);
        }
    }
}
