package com.huatu.handheld_huatu.mvpmodel.Sensor;

import com.huatu.handheld_huatu.utils.GsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by cjx on 2018\12\28 0028.
 */

/*       https://blog.csdn.net/u011368551/article/details/80503605
          3.JSONObject：添加value为null的键值对，Map保存的时候会删掉这一键值对；
        JsonObject：添加value为null的键值对，Map会保留value值是null的键值对。

        在开发中使用JSONObject需要注意的是，如果保存了一个value值为null的键值对，在其它地方通过key来取值的时候，不会出现通过key取到值为null的情况。因为根本就没有这个key所对应的键值对。*/

public class BaseEvent {



    public JSONObject toJsonObject() throws JSONException {
        return  new JSONObject(GsonUtil.toJsonStr(this));

    }

}
