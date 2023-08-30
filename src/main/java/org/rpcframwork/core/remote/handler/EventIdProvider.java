package org.rpcframwork.core.remote.handler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

/**
 * 为整个框架提供事件 id，事件id加锁
 */
public class EventIdProvider {
    private static Integer eventId = new Random(100).nextInt(0, 100);

    public static String getEventId() {
        String Id = "";
        try {
            Id = Id + InetAddress.getLocalHost().getHostAddress().replaceAll("\\.", "");
        } catch (UnknownHostException e) {
            System.out.println("can not get LocalHost");
            e.printStackTrace();
            System.exit(1);
        }

        synchronized (eventId) {
            Id = Id + eventId.intValue();
            eventId++;
        }
        return Id;
    }
}
