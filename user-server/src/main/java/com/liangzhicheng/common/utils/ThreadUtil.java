package com.liangzhicheng.common.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @description 【用户服务】创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程
 * @author liangzhicheng
 * @since 2021-08-10
 */
public class ThreadUtil {

    /**
     * 构造器
     */
    private ThreadUtil(){}

    /**
     * 创建一个可缓存线程池
     */
    private static final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    /**
     * @description 获取缓存线程池
     * @return
     */
    public static ExecutorService getCachedThreadPool(){
        return cachedThreadPool;
    }

    /**
     * @description 开启线程刷新系统权限菜单列表
     */
    public static void threadListPermMenu(){
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                CacheUtil.refreshListPermMenu();
                SysToolUtil.info("user server refresh listPermMenu success ...");
            }
        });
    }

    /**
     * @description 开启线程刷新系统权限信息
     */
    public static void threadRolePerm(){
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                CacheUtil.refreshRolePerm();
                SysToolUtil.info("user server refresh rolePerm success ...");
            }
        });
    }

}
