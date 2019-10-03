package callcenter.utils;

import java.util.concurrent.atomic.AtomicLong;

public class Utils {
    private static AtomicLong callIdCounter = new AtomicLong(0);
    private static AtomicLong responserIdCounter = new AtomicLong(0);

    public static long genCallId() {
        return  callIdCounter.incrementAndGet();
    }
    protected static void reset() {
        callIdCounter.set(0);
    }

    public static long genResponserId() {
        return responserIdCounter.incrementAndGet();
    }
}
