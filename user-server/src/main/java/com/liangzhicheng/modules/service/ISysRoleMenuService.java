package com.liangzhicheng.modules.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liangzhicheng.modules.entity.SysRoleMenuEntity;

import java.util.List;

/**
 * @description 角色菜单 服务类
 * @author liangzhicheng
 * @since 2021-08-09
 */
public interface ISysRoleMenuService extends IService<SysRoleMenuEntity> {

    /**
     * @description 根据key，value获取角色菜单列表
     * @param key
     * @param value
     * @return List<SysRoleMenuEntity>
     */
    List<SysRoleMenuEntity> list(String key, String value);

}
