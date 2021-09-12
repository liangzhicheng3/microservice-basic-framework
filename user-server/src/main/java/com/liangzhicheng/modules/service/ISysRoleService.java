package com.liangzhicheng.modules.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liangzhicheng.modules.entity.SysRoleEntity;
import com.liangzhicheng.modules.entity.dto.SysRoleDTO;
import com.liangzhicheng.modules.entity.vo.SysRoleDescVO;
import org.springframework.data.domain.Pageable;

import java.util.Map;

/**
 * @description 角色 服务类
 * @author liangzhicheng
 * @since 2021-08-09
 */
public interface ISysRoleService extends IBaseService<SysRoleEntity> {

    /**
     * @description 角色管理
     * @param roleDTO
     * @param pageable
     * @return IPage
     */
    Map<String, Object> listRole(SysRoleDTO roleDTO, Pageable pageable);

    /**
     * @description 获取角色
     * @param roleDTO
     * @return SysRoleVO
     */
    SysRoleDescVO getRole(SysRoleDTO roleDTO);

    /**
     * @description 保存角色
     * @param roleDTO
     */
    void saveRole(SysRoleDTO roleDTO);

    /**
     * @description 删除角色
     * @param roleDTO
     */
    void deleteRole(SysRoleDTO roleDTO);

}
