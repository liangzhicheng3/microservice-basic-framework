package com.liangzhicheng.modules.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liangzhicheng.modules.entity.SysUserEntity;

import java.util.List;

/**
 * @description 账号 Mapper接口
 * @author liangzhicheng
 * @since 2021-08-09
 */
public interface ISysUserDao extends BaseMapper<SysUserEntity> {

    List<String> selectListByUserMenu(String accountId);

}
