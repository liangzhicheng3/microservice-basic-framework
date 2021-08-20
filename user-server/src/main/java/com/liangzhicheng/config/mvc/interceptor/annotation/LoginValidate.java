package com.liangzhicheng.config.mvc.interceptor.annotation;

import java.lang.annotation.*;

/**
 * @description 登录校验注解
 * @author liangzhicheng
 * @since 2021-08-12
 */
@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginValidate {

	boolean validate() default true;

}
