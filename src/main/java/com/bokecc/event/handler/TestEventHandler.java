
package com.bokecc.event.handler;

import com.bokecc.event.TestEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TestEventHandler {

    @EventListener
    @Async("taskExecutor")
    public void handler(TestEvent event) {
        log.info("{} 收到事件", this.getClass().getSimpleName());
    }

}
