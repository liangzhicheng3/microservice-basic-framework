package com.liangzhicheng.modules.service.impl;

import com.liangzhicheng.modules.service.IProviderService;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import javax.annotation.Resource;

/**
 * @description 供应者服务实现类
 * @author liangzhicheng
 * @since 2021-08-16
 */
@EnableBinding(Source.class) //定义消息的推送广告
public class ProviderServiceImpl implements IProviderService {

    @Resource
    private MessageChannel output; //消息发送管道

    /**
     * @description 消息发送方
     */
    @Override
    public void send() {
        String uuid = "123456";
        output.send(MessageBuilder.withPayload(uuid).build());
        System.out.println("provider send message:" + uuid);
    }

}
