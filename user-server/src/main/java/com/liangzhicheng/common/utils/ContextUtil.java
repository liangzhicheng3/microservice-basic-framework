package com.liangzhicheng.common.utils;

import com.liangzhicheng.modules.entity.SysUserEntity;
import org.apache.shiro.SecurityUtils;

/**
 * @description 上下文相关工具类
 * @author liangzhicheng
 * @since 2021-08-11
 */
public class ContextUtil {

    /**
     * @description 从请求头中获取当前登录用户
     * @return SysUserEntity
     */
    public static SysUserEntity getCurrentUser(){
        return (SysUserEntity) SecurityUtils.getSubject().getPrincipal();
    }

}
