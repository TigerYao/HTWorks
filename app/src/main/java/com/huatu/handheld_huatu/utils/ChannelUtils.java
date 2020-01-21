package com.huatu.handheld_huatu.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * ChannelId对应关系
 */
public class ChannelUtils {

    private static ChannelUtils channelUtils;
    public Map<String, Integer> channel;

    public static ChannelUtils newInstance() {
        if (channelUtils == null){
            synchronized (ChannelUtils.class){
                if (channelUtils == null){
                    channelUtils = new ChannelUtils();
                }
            }
        }
        return channelUtils;
    }

    private ChannelUtils() {
        init();
    }

    private void init() {
        channel = new HashMap<>();
        channel.put("vhuatu",180001);
        channel.put("mvhuatu",180002);
        channel.put("upgrade",180003);
        channel.put("baidu",180004);
        channel.put("huawei",180005);
        channel.put("vivo",180006);
        channel.put("oppo",180007);
        channel.put("samsung",180008);
        channel.put("uc",180009);
        channel.put("qq",180010);
        channel.put("xiaomi",180011);
        channel.put("meizu",180012);
        channel.put("360",180013);
        channel.put("smartisan",180014);
        channel.put("sougouzhushou",180015);
        channel.put("gionee",180016);
        channel.put("lenovo",180017);
        channel.put("mumayi",180018);
        channel.put("web",180019);
        channel.put("anzhi",180020);
        channel.put("toutiao",180050);
        channel.put("baidufeed",180051);
        channel.put("openqq",180052);
        channel.put("360sem",180053);
    }
}
