//package com.liangzhicheng.config.mvc.filter;
//
//import com.liangzhicheng.common.utils.SysToolUtil;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
///**
// * @description 全局路由过滤器,请求中是否包含token,没有则不转发路由,有则执行正常逻辑
// * @author liangzhicheng
// * @since 2021-07-30
// */
//@Component
//public class AuthGlobalFilter implements GlobalFilter {
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        //获取请求中的token
//        String token = exchange.getRequest().getQueryParams().getFirst("token");
//        if (SysToolUtil.isBlank(token)) {
//            SysToolUtil.info("鉴权失败");
//            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//            return exchange.getResponse().setComplete();
//        }
//        return chain.filter(exchange);
//    }
//
//}
