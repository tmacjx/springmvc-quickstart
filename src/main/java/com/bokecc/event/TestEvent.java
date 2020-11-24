package com.bokecc.event;

import org.springframework.context.ApplicationEvent;

public class TestEvent extends ApplicationEvent {
    public TestEvent(Object source) {
        super(source);
    }
}
