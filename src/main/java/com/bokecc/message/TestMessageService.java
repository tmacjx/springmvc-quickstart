package com.bokecc.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;


@Slf4j
public class TestMessageService {

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            // TODO 队列的名字
                            value = "#{queue.name}", durable = "true"
                    ),
                    // TODO EXCHANGE的名字
                    exchange = @Exchange(
                            value = "exchange.name", type = ExchangeTypes.FANOUT, autoDelete = "true"
                    ),
                    key = "cache.update"
            )
    )
    public void receive(byte[] msg) throws Exception{
        log.info("收到消息 {}", msg);
    }
}


