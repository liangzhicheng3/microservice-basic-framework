package com.liangzhicheng.modules.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liangzhicheng.common.page.PageResult;
import com.liangzhicheng.modules.entity.TestAreaNameEntity;
import com.liangzhicheng.modules.entity.dto.TestAreaDTO;

import java.util.List;
import java.util.Map;

/**
 * @description 地区名称 服务类
 * @author liangzhicheng
 * @since 2021-08-11
 */
public interface ITestAreaNameService extends IService<TestAreaNameEntity> {

    /**
     * @description 获取地区列表
     * @param areaDTO
     * @return PageResult
     */
    PageResult listArea(TestAreaDTO areaDTO);

    /**
     * @description 查询地区信息
     * @param areaDTO
     * @return List<Map<String, Object>>
     */
    List<Map<String, Object>> getArea(TestAreaDTO areaDTO);

}
