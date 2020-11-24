package com.bokecc.util;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import lombok.Builder;
import lombok.Getter;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;

/**
    钉钉消息封装类
 */
@Builder
@Getter
public class DingMsg {

    private static final String MSG_TYPE = "msgtype";

    private static final String MARK_DOWN = "markdown";

    private static final String AT_MOBILES = "atMobiles";

    private static final String IS_AT_ALL = "isAtAll";

    private String title;

    private String text;

    private List<String> notifyList;

    private Boolean isAtAll;

    @Override
    public String toString() {

        Map<String, Object> requestMap = Maps.newLinkedHashMap();
        requestMap.put(MSG_TYPE, MARK_DOWN);

        Map<String, String> markdown = Maps.newLinkedHashMap();
        markdown.put("title", this.title);

        processText();

        //title不显示，必须用#加在text里面
        String text = "# " + this.title + "\n" + this.text;

        markdown.put("text", text);

        requestMap.put(MARK_DOWN, markdown);

        Map<String, Object> atMap = Maps.newLinkedHashMap();;

        if(null != notifyList && !notifyList.isEmpty()){

            atMap.put(AT_MOBILES, notifyList);
        }

        if(null != isAtAll){

            atMap.put(IS_AT_ALL, isAtAll);
        }

        if(atMap.size() > 0){

            requestMap.put("at", atMap);
        }

        return JSON.toJSONString(requestMap);
    }

    private void processText(){

        StringBuilder sb = new StringBuilder();

        String host = "UNKNOWN";

        try {

            InetAddress localhost = InetAddress.getLocalHost();

            host = localhost.getHostName();

        }catch (Exception e){

            //
            e.printStackTrace();
        }

        sb.append("- **time**: ")
                .append(TimeFormatter.getDate(TimeStyle.TIME_HORIZONTAL_LINE))
                .append("\n")
                .append("- **host** : ")
                .append(host).append("\n")
                .append("- **server-name** : common-user").append("\n")
                .append("- **text** : ").append(this.text);

        this.text = sb.toString();
    }
}
