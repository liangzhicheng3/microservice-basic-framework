package com.liangzhicheng.config.swagger;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @description Swagger-ui接口文档配置
 * @author liangzhicheng
 * @since 2021-08-02
 */
@EnableKnife4j //Swagger升级版knife4j,核心注解
@EnableSwagger2
@Configuration
public class SwaggerAPIConfig {

    /**
     * @description 创建RestApi,并扫描包下Controller生成
     * @return Docket
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.liangzhicheng")) //指定Controller扫描包路径
                .paths(PathSelectors.any())
                .paths(Predicates.not(PathSelectors.regex("/error.*")))
                .build();
    }

    /**
     * @description 创建Swagger页面信息
     * @return ApiInfo
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().
                title("API文档")
                .description("// 佛祖保佑 永不宕机 永无BUG 阿门 //")
                .contact(new Contact("liangzhicheng", "https://github.com/liangzhicheng3/", "yichengc3@163.com"))
                .version("1.0.0")
                .build();
    }

}
