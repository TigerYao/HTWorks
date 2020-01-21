package com.huatu.handheld_huatu.network.tcp;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

//import io.netty.channel.ChannelHandlerContext;

public class ConnectionKeeper {
   /* private static ConcurrentHashMap<Integer, ChannelHandlerContext> mapping = new ConcurrentHashMap<Integer, ChannelHandlerContext>();

    public static boolean add(ChannelHandlerContext context) {
        int channelId = getChannelId(context);
        mapping.put(channelId, context);
        return true;
    }

    public static ChannelHandlerContext get() {
        if (0 == mapping.size()) {
            return null;
        }
        Iterator<Integer> iter = mapping.keySet().iterator();
        ChannelHandlerContext ctx = mapping.get(iter.next());
        return ctx;
    }

    public static void remove(ChannelHandlerContext context) {
        int channelId = getChannelId(context);
        mapping.remove(channelId);
    }

    public static void close(ChannelHandlerContext context) {
        int channelId = getChannelId(context);
        mapping.remove(channelId);
        context.close();
    }

    public static int getChannelId(ChannelHandlerContext context) {
        return context.channel().hashCode();
    }

    public static void clear() {
        if(mapping == null || mapping.keySet() == null) {
            return;
        }
        synchronized (mapping) {
            Iterator<Integer> iter = mapping.keySet().iterator();
            if(iter != null) {
                while (iter.hasNext()) {
                    Integer key = iter.next();
                    ChannelHandlerContext ctx = mapping.get(key);
                    if(ctx != null) {
                        ctx.close();
                    }
                    mapping.remove(key);
                }
            }
        }
    }*/
}
