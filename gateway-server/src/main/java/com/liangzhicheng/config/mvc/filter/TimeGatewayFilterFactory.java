package com.liangzhicheng.config.mvc.filter;

import lombok.Data;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * @description 自定义路由过滤器,类名定义要与配置文件中的名称一致,xxxGatewayFilterFactory(类名),- Time(配置文件中的名称) = true
 * @author liangzhicheng
 * @since 2021-07-30
 */
@Component
public class TimeGatewayFilterFactory extends AbstractGatewayFilterFactory<TimeGatewayFilterFactory.Config> {

    /**
     * 开始时间
     */
    private static final String BEGIN_TIME = "beginTime";

    /**
     * 构造函数
     */
    public TimeGatewayFilterFactory() {
        super(Config.class);
    }

    /**
     * @description 读取配置文件中参数赋值到配置类中
     * @return List<String>
     */
    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("parts");
    }

    /**
     * @description 过滤执行方法
     * @param config
     * @return GatewayFilter
     */
    @Override
    public GatewayFilter apply(Config config) {
        return new GatewayFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                if(!config.parts){
                    return chain.filter(exchange);
                }
                exchange.getAttributes().put(BEGIN_TIME, System.currentTimeMillis());
                /**
                 * pre的逻辑(前置逻辑)
                 * chain.filter().then(Mono.fromRunable(()->{
                 * post的逻辑(后置逻辑)
                 * }))
                 */
                return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                    Long startTime = exchange.getAttribute(BEGIN_TIME);
                    if (startTime != null) {
                        System.out.println("请求路径：" + exchange.getRequest().getURI() + "\n请求耗时: "
                                + (System.currentTimeMillis() - startTime) + "ms");
                    }
                }));
            }
        };
    }

    @Data
    public static class Config{
        private boolean parts;
    }

}
