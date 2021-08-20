package com.liangzhicheng.modules.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liangzhicheng.modules.entity.SysMenuEntity;
import com.liangzhicheng.modules.entity.SysPermEntity;
import com.liangzhicheng.modules.entity.SysPermMenuEntity;

/**
 * @description 权限菜单 服务类
 * @author liangzhicheng
 * @since 2021-08-09
 */
public interface ISysPermMenuService extends IService<SysPermMenuEntity> {

    /**
     * @description 根据key，value获取权限菜单
     * @param key
     * @param value
     * @return SysPermMenuEntity
     */
    SysPermMenuEntity getOne(String key, String value);

    /**
     * @description 新增权限菜单
     * @param perm
     * @param menu
     */
    void insertPermMenu(SysPermEntity perm, SysMenuEntity menu);

}
