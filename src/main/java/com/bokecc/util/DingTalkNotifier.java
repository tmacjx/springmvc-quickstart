package com.bokecc.util;

import org.apache.http.entity.StringEntity;

import java.util.Collections;
import java.util.Map;

/**
 * 钉钉消息发送类
 */

public class DingTalkNotifier {

    private static final Map<String, String> HEAD = Collections.singletonMap("content-type","application/json;charset=utf-8");

    public static void sendMsg(String webHookUrl, DingMsg msg) {

        StringEntity entity = new StringEntity(msg.toString(), "utf-8");

        HttpUtil.httpPost(webHookUrl, entity, HEAD, 3, 5000);
    }
}
