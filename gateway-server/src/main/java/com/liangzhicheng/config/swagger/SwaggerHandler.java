package com.liangzhicheng.config.swagger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import springfox.documentation.swagger.web.*;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @description Swagger访问接口信息(需同时与SwaggerResourceConfig配套使用)
 * @author liangzhicheng
 * @since 2021-08-05
 */
@RestController
public class SwaggerHandler {

    /**
     * 权限配置
     */
    @Autowired(required = false)
    private SecurityConfiguration securityConfiguration;

    /**
     * Swagger ui配置
     */
    @Autowired(required = false)
    private UiConfiguration uiConfiguration;

    /**
     * 接口资源提供者
     */
    private final SwaggerResourcesProvider swaggerResourcesProvider;

    @Autowired
    public SwaggerHandler(SwaggerResourcesProvider swaggerResourcesProvider) {
        this.swaggerResourcesProvider = swaggerResourcesProvider;
    }

    @GetMapping("/swagger-resources/configuration/security")
    public Mono<ResponseEntity<SecurityConfiguration>> securityConfiguration() {
        return Mono.just(new ResponseEntity<>(
                Optional.ofNullable(securityConfiguration)
                        .orElse(SecurityConfigurationBuilder.builder().build()), HttpStatus.OK));
    }

    @GetMapping("/swagger-resources/configuration/ui")
    public Mono<ResponseEntity<UiConfiguration>> uiConfiguration() {
        return Mono.just(new ResponseEntity<>(
                Optional.ofNullable(uiConfiguration)
                        .orElse(UiConfigurationBuilder.builder().build()), HttpStatus.OK));
    }

    /**
     * @description 获取接口资源信息
     * @return Mono<ResponseEntity>
     */
    @GetMapping("/swagger-resources")
    public Mono<ResponseEntity> swaggerResources() {
        return Mono.just((new ResponseEntity<>(swaggerResourcesProvider.get(), HttpStatus.OK)));
    }

}
