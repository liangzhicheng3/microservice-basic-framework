package com.liangzhicheng.config.init;

import com.liangzhicheng.common.utils.CacheUtil;
import com.liangzhicheng.common.utils.SysToolUtil;
import com.liangzhicheng.modules.entity.vo.SysMenuVO;

import java.util.List;
import java.util.Map;

/**
 * @description 【用户服务】缓存初始化线程处理类
 * @author liangzhicheng
 * @since 2021-08-10
 */
public class CacheThread implements Runnable {

    @Override
    public void run() {
        SysToolUtil.info("cacheThread run start ...");
        //系统权限菜单列表初始化处理
        List<SysMenuVO> permMenuVOList = CacheUtil.listPermMenu();
        //系统角色权限初始化处理
        Map<String, Object> roleMap = CacheUtil.getRoleMap();
        Map<String, Object> permMap = CacheUtil.getPermMap();
        if(!SysToolUtil.listSizeGT(permMenuVOList)){
            CacheUtil.refreshListPermMenu();
        }
        if(SysToolUtil.isNull(roleMap) || SysToolUtil.isNull(permMap)){
            CacheUtil.refreshRolePerm();
        }
        SysToolUtil.info("cacheThread run end ...");
    }

}
