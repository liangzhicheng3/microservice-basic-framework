package com.liangzhicheng.modules.controller;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * @description 消费者控制器
 * @author liangzhicheng
 * @since 2021-08-16
 */
@Component
@EnableBinding(Sink.class)
public class ConsumerController {

    @StreamListener(Sink.INPUT)
    public void input(Message<String> message){
        System.out.println("consumer receive message:" + message.getPayload());
    }

}
