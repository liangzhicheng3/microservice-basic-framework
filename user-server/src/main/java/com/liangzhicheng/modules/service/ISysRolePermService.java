package com.liangzhicheng.modules.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liangzhicheng.modules.entity.SysRolePermEntity;

import java.util.List;
import java.util.Map;

/**
 * @description 角色权限 服务类
 * @author liangzhicheng
 * @since 2021-08-09
 */
public interface ISysRolePermService extends IService<SysRolePermEntity> {

    /**
     * @description 根据key，value获取角色权限列表
     * @param key
     * @param value
     * @return List<SysRolePermEntity>
     */
    List<SysRolePermEntity> list(String key, String value);

    /**
     * @description 权限Map
     * @return Map<String, Object>
     */
    Map<String, Object> mapRolePerm();

}
