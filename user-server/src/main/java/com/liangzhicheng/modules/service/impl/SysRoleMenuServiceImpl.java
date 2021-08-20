package com.liangzhicheng.modules.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liangzhicheng.common.constant.Constants;
import com.liangzhicheng.modules.dao.ISysRoleMenuDao;
import com.liangzhicheng.modules.entity.SysRoleMenuEntity;
import com.liangzhicheng.modules.service.ISysRoleMenuService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description 角色菜单 服务实现类
 * @author liangzhicheng
 * @since 2021-08-09
 */
@Service
public class SysRoleMenuServiceImpl extends ServiceImpl<ISysRoleMenuDao, SysRoleMenuEntity> implements ISysRoleMenuService {

    /**
     * @description 根据key，value获取角色菜单列表
     * @param key
     * @param value
     * @return List<SysRoleMenuEntity>
     */
    @Override
    public List<SysRoleMenuEntity> list(String key, String value) {
        return baseMapper.selectList(new QueryWrapper<SysRoleMenuEntity>()
                .eq(key, value).eq(Constants.DEL_FLAG, Constants.ZERO));
    }

}
