package com.liangzhicheng.modules.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liangzhicheng.modules.entity.SysRoleUserEntity;

import java.util.List;

/**
 * @description 角色用户 服务类
 * @author liangzhicheng
 * @since 2021-08-09
 */
public interface ISysRoleUserService extends IService<SysRoleUserEntity> {

    /**
     * @description 根据key，value获取角色用户列表
     * @param key
     * @param value
     * @return List<SysRoleUserEntity>
     */
    List<SysRoleUserEntity> list(String key, String value);

}
