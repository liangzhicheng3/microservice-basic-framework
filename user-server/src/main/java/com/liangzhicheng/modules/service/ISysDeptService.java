package com.liangzhicheng.modules.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liangzhicheng.modules.entity.SysDeptEntity;
import com.liangzhicheng.modules.entity.dto.SysDeptDTO;
import com.liangzhicheng.modules.entity.vo.SysDeptDescVO;

/**
 * @description 部门 服务类
 * @author liangzhicheng
 * @since 2021-08-09
 */
public interface ISysDeptService extends IService<SysDeptEntity> {

    /**
     * @description 部门列表
     * @param deptDTO
     * @return IPage
     */
    IPage listDept(SysDeptDTO deptDTO);

    /**
     * @description 获取部门
     * @param deptDTO
     * @return SysDeptVO
     */
    SysDeptDescVO getDept(SysDeptDTO deptDTO);

    /**
     * @description 保存部门
     * @param deptDTO
     */
    void saveDept(SysDeptDTO deptDTO);

    /**
     * @description 删除部门
     * @param deptDTO
     */
    void deleteDept(SysDeptDTO deptDTO);

}
