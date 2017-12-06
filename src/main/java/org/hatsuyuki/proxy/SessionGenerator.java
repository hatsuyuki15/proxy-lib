package org.hatsuyuki.proxy;

import java.util.concurrent.atomic.AtomicLong;

public class SessionGenerator {
    private static AtomicLong counter = new AtomicLong(System.currentTimeMillis());
    public static long generate() {
        return counter.getAndIncrement();
    }
}
