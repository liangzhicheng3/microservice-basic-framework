package com.liangzhicheng.common.utils;

import com.liangzhicheng.common.constant.Constants;
import com.liangzhicheng.config.context.SpringContextHolder;
import com.liangzhicheng.config.init.CacheThread;
import com.liangzhicheng.modules.entity.vo.SysMenuVO;
import com.liangzhicheng.modules.service.ISysMenuService;
import com.liangzhicheng.modules.service.ISysRolePermService;

import java.util.List;
import java.util.Map;

/**
 * @description 【用户服务】缓存工具类
 * @author liangzhicheng
 * @since 2021-08-10
 */
public class CacheUtil {

    private static SysRedisUtil redis = SpringContextHolder.getBean(SysRedisUtil.class);
    private static ISysMenuService menuService = SpringContextHolder.getBean(ISysMenuService.class);
    private static ISysRolePermService rolePermService = SpringContextHolder.getBean(ISysRolePermService.class);

    /**
     * @description 写操作,永久存活
     * @param key
     * @param obj
     */
    public static void set(String key, Object obj) {
        redis.set(key, obj);
        SysToolUtil.info("user server cacheUtil set : " + "key=" + key + ", obj=" + obj);
    }

    /**
     * @description 读操作
     * @param key
     * @return Object
     */
    public static Object get(String key) {
        SysToolUtil.info("user server cacheUtil get : " + "key=" + key);
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
     * @description 初始化
     */
    public static void init() {
        SysToolUtil.info("user server cacheUtil invoke init start ...");
        Thread cacheThread = new Thread(new CacheThread());
        cacheThread.start();
    }

    /**
     * @description 刷新系统权限菜单列表
     */
    public static void refreshListPermMenu() {
        set(Constants.KEY_MENU_LIST, menuService.listPermMenu());
    }

    /**
     * 刷新系统权限Map
     */
    public static void refreshRolePerm(){
        Map<String, Object> resultMap = rolePermService.mapRolePerm();
        set(Constants.KEY_ROLE_MAP, resultMap.get("roleMap"));
        set(Constants.KEY_PERM_MAP, resultMap.get("permMap"));
    }

    /**
     * @description 获取缓存中系统权限菜单列表
     * @return List<SysMenuVO>
     */
    public static List<SysMenuVO> listPermMenu() {
        return (List<SysMenuVO>) get(Constants.KEY_MENU_LIST);
    }

    /**
     * @description 获取缓存中角色Map
     * @return Map<String, Object>
     */
    public static Map<String, Object> getRoleMap(){
        return (Map<String, Object>) get(Constants.KEY_ROLE_MAP);
    }

    /**
     * @description 获取缓存中权限Map
     * @return Map<String, Object>
     */
    public static Map<String, Object> getPermMap(){
        return (Map<String, Object>) get(Constants.KEY_PERM_MAP);
    }

}
