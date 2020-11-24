package com.bokecc.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池工具类
 **/

@Slf4j
public class ThreadPoolUtil {

    private static final ThreadPoolExecutor THREAD_POOL;

    private static final String MSG_01 = "线程池队列溢出";

    private static final String MSG_02 = "线程池异常";

    static {

        int core = Runtime.getRuntime().availableProcessors();

        THREAD_POOL = new ThreadPoolExecutor(core, core * 2 + 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue(1000), new ThreadPoolExecutor.AbortPolicy());    }

    public static void execute(Runnable thread, String dingUrl){

        try {

            THREAD_POOL.execute(thread);

        }catch (RejectedExecutionException e){

            alarm(MSG_01, dingUrl);

            log.error(MSG_01, e);

        }catch (Exception e){

            alarm(MSG_02, dingUrl);

            log.error(MSG_02, e);
        }
    }

    private static void alarm(String text, String dingUrl){

        if(null == dingUrl){

            return;
        }

        DingMsg msg = DingMsg.builder()
                .title("ERROR")
                .text(text).build();

        DingTalkNotifier.sendMsg(dingUrl, msg);
    }
}
