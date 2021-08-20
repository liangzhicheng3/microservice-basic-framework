package com.liangzhicheng.config.swagger;

import com.google.common.collect.Lists;
import com.liangzhicheng.common.utils.ToolUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.support.NameUtils;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.List;

/**
 * @description 集成knife4j(升级版Swagger接口文档),需新增以下类
 *              springfox-swagger提供分组接口swagger-resource,返回的是分组接口名称,地址等信息
 *              微服务架构下,需要重写该接口,主要是通过网关的注册中心动态发现所有的微服务接口文档
 *              (需同时新增SwaggerHandler类)
 * @author liangzhicheng
 * @since 2021-08-04
 */
@AllArgsConstructor
@Component
public class SwaggerResourceConfig implements SwaggerResourcesProvider {

    /**
     * 路由加载器
     */
    @Autowired
    private RouteLocator routeLocator;

    /**
     * gateway初始化时,GatewayAutoConfiguration配置中初始化加载GatewayProperties实例配置:
     *    routes:网关路由定义配置,列表形式(对应gateway配置文件中参数):
     *       id:路由id 唯一编号
     *       uri:路由指向的url
     *       order:顺序
     *       predicates:谓语数组,列表形式
     *    default-filters:网关默认过滤器定义配置,列表形式:
     *       name:过滤器定义名称
     *       args:参数
     *    streamingMediaTypes:网关网络媒体类型,列表形式
     */
    @Autowired
    private GatewayProperties gatewayProperties;

    @Override
    public List<SwaggerResource> get() {
        //服务名称列表
        List<String> routes = Lists.newArrayList();
        //接口资源列表
        List<SwaggerResource> resources = Lists.newArrayList();
        //获取所有可用的应用服务名称
        routeLocator.getRoutes().subscribe(route -> routes.add(route.getId()));
        //获取应用服务中参数信息
        gatewayProperties.getRoutes()
                .stream()
                .filter(routeDefinition ->
                        routes.contains(routeDefinition.getId())).forEach(route -> {
                            route.getPredicates()
                                    .stream()
                                    .filter(predicateDefinition -> ("Path").equalsIgnoreCase(predicateDefinition.getName())).forEach(predicateDefinition ->
                                    resources.add(swaggerResource(route.getId(),
                                            predicateDefinition.getArgs().get(NameUtils.GENERATED_NAME_PREFIX + "0")
                                                    .replace("**", "v2/api-docs"))));
                        });
        return resources;
    }

    /**
     * @description 参数信息处理
     * @param name
     * @param location
     * @return SwaggerResource
     */
    private SwaggerResource swaggerResource(String name, String location) {
        ToolUtil.info("name:{" + name + "}, location:{" + location + "}");
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion("1.0.0");
        return swaggerResource;
    }

}
