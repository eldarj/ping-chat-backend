package com.pingchat.authenticationservice.util;

import java.time.Instant;
import java.util.Random;

public final class UniqueUtil {
    static final Random random = new Random();

    public static long nextUniqueLong() {
        // generate random int, bound to biggest value of first bits of max Long
        int randomInt = random.nextInt(922337);
        randomInt += randomInt == 0 ? 1 : 0;

        long currentTimestamp = Instant.now().toEpochMilli();

        // concat the rest of the Long bits bycurrent epoch millis timestamp
        long uniqueId = Long.parseLong(randomInt + "" + currentTimestamp);

        return uniqueId;
    }
}
