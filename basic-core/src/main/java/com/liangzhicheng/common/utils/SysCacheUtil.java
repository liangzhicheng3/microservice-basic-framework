package com.liangzhicheng.common.utils;

import com.liangzhicheng.config.context.SpringContextHolder;

import java.util.Map;
import java.util.Set;

/**
 * @description 缓存工具类
 * @author liangzhicheng
 * @since 2021-08-05
 */
public class SysCacheUtil {

    private static SysRedisUtil redis = SpringContextHolder.getBean(SysRedisUtil.class);

    /**
     * @description key操作,设置某个key的存活时间
     * @param key
     * @param second
     * @return boolean
     */
    public static boolean expire(String key, int second){
        SysToolUtil.info("sysCacheUtil expire with second : " + "key=" + key + ", second=" + second);
        return redis.expire(key, second);
    }

    /**
     * @description 获取Map
     * @param mapName
     * @return Map
     */
    public static Map<String, Object> entries(String mapName) {
        SysToolUtil.info("sysCacheUtil entries : " + "mapName=" + mapName);
        return redis.entries(mapName);
    }

    /**
     * @description 写操作,永久存活
     * @param key
     * @param obj
     */
    public static void set(String key, Object obj) {
        redis.set(key, obj);
        SysToolUtil.info("sysCacheUtil set : " + "key=" + key + ", obj=" + obj);
    }

    /**
     * @description 写操作,可设置存活秒数
     * @param key
     * @param obj
     * @param second
     */
    public static void set(String key, Object obj, int second) {
        redis.set(key, obj, second);
        SysToolUtil.info("sysCacheUtil set with second : " + "key=" + key + ", obj=" + obj + ", second=" + second);
    }

    /**
     * @description 写操作,向名称为key的set中添加元素member
     * @param key
     * @param obj
     */
    public static void sadd(String key, Object obj) {
        redis.sadd(key, obj);
    }

    /**
     * @description 删除操作,删除名称为key的set中的元素member
     * @param key
     * @param obj
     */
    public static void srem(String key, Object obj){
        redis.srem(key, obj);
    }

    /**
     * @description 读操作,返回名称为key的set的所有元素
     * @param key
     * @return Set
     */
    public static Set<Object> smembers(String key){
        return redis.smembers(key);
    }

    /**
     * @description 读操作
     * @param key
     * @return Object
     */
    public static Object get(String key) {
        SysToolUtil.info("sysCacheUtil get : " + "key=" + key);
        return redis.get(key);
    }

    /**
     * @description 删除操作
     * @param key
     */
    public static void del(String key) {
        redis.del(key);
    }

    /**
     * @description 判断key是否存在
     * @param key
     * @return boolean
     */
    public static boolean hasKey(String key) {
        return redis.hasKey(key);
    }

    /**
     * @description hash写操作,向名称为key的hash中添加元素field
     * @param key
     * @param field
     * @param obj
     */
    public static void hset(String key, String field, Object obj) {
        redis.hset(key, field, obj);
        SysToolUtil.info("sysCacheUtil hset : " + "key=" + key + ", field=" + field + ", obj=" + obj);
    }

    /**
     * @description hash读操作,返回名称为key的hash中field对应的value
     * @param key
     * @param field
     * @return Object
     */
    public static Object hget(String key, String field) {
        SysToolUtil.info("sysCacheUtil hget : " + "key=" + key + ", field=" + field);
        return redis.hget(key, field);
    }

    /**
     * @description 判断hash的某个map是否存在某个key
     * @param mapName
     * @param key
     * @return boolean
     */
    public static boolean hHasKey(String mapName, String key) {
        return redis.hHasKey(mapName, key);
    }

    /**
     * @description hash删除操作,删除名称为key的hash中键为field的域
     * @param key
     * @param field
     */
    public static void hdel(String key, String field) {
        redis.hdel(key, field);
        SysToolUtil.info("sysCacheUtil hdel : " + "key=" + key + ", field=" + field);
    }

}
