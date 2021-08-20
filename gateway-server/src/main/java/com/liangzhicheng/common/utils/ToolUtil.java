package com.liangzhicheng.common.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;

/**
 * @description 【网关模块】常用工具类
 * @author liangzhicheng
 * @since 2021-07-30
 */
public class ToolUtil {

    /**
     * @description 判断String参数是否为空，参数数量可变
     * @param params
     * @return boolean
     */
    public static boolean isBlank(String ... params){
        for(String s : params){
            if(StringUtils.isBlank(s)){
                return true;
            }
        }
        return false;
    }

    /**
     * @description 判断多个参数是否为空，参数数量可变
     * @param params
     * @return boolean
     */
    public static boolean isNotBlank(String ... params){
        return !isBlank(params);
    }

    /**
     * @description 打印日志(info级别)
     * @param info
     */
    public static void info(Object info) {
        LogManager.getLogger(ToolUtil.class).info(info);
    }

    /**
     * @description 打印日志(info级别)
     * @param info
     * @param clazz
     */
    public static void info(Object info, Class<?> clazz) {
        LogManager.getLogger(clazz).info(info);
    }

    /**
     * @description 打印日志(warn级别)
     * @param warn
     */
    public static void warn(Object warn) {
        LogManager.getLogger(ToolUtil.class).warn(warn);
    }

    /**
     * @description 打印日志(warn级别)
     * @param warn
     * @param clazz
     */
    public static void warn(Object warn, Class<?> clazz) {
        LogManager.getLogger(clazz).warn(warn);
    }

    /**
     * @description 打印日志(error级别)
     * @param error
     * @param clazz
     */
    public static void error(Object error, Class<?> clazz) {
        LogManager.getLogger(clazz).error(error);
    }

}
