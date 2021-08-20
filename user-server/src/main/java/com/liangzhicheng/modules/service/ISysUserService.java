package com.liangzhicheng.modules.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liangzhicheng.modules.entity.SysUserEntity;
import com.liangzhicheng.modules.entity.dto.SysUserDTO;
import com.liangzhicheng.modules.entity.vo.SysPersonInfoVO;
import com.liangzhicheng.modules.entity.vo.SysUserDescVO;
import com.liangzhicheng.modules.entity.vo.SysUserLoginVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @description 账号 服务类
 * @author liangzhicheng
 * @since 2021-08-09
 */
public interface ISysUserService extends IService<SysUserEntity> {

    /**
     * @description 登录
     * @param accountDTO
     * @param request
     * @return SysUserLoginVO
     */
    SysUserLoginVO login(SysUserDTO accountDTO, HttpServletRequest request);

    /**
     * @description 退出登录
     * @param request
     */
    void logOut(HttpServletRequest request);

    /**
     * @description 更新当前登录用户头像
     * @param userDTO
     * @param request
     * @return SysPersonInfoVO
     */
    SysPersonInfoVO updateAvatar(SysUserDTO userDTO, HttpServletRequest request);

    /**
     * @description 更新当前登录用户密码
     * @param userDTO
     * @param request
     */
    void updatePassword(SysUserDTO userDTO, HttpServletRequest request);

    /**
     * @description 账号管理列表
     * @param userDTO
     * @return IPage
     */
    IPage listAccount(SysUserDTO userDTO);

    /**
     * @description 根据key，value获取用户列表
     * @param key
     * @param value
     * @return List<SysUserEntity>
     */
    List<SysUserEntity> list(String key, String value);

    /**
     * @description 获取账号
     * @param userDTO
     * @return SysUserDescVO
     */
    SysUserDescVO getAccount(SysUserDTO userDTO);

    /**
     * @description 保存账号
     * @param userDTO
     */
    void saveAccount(SysUserDTO userDTO);

    /**
     * @description 重置密码
     * @param userDTO
     */
    void resetPassword(SysUserDTO userDTO);

    /**
     * @description 删除账号
     * @param userDTO
     */
    void deleteAccount(SysUserDTO userDTO);

}
