package com.liangzhicheng.modules.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liangzhicheng.modules.entity.TestAreaNameEntity;
import com.liangzhicheng.modules.entity.dto.TestAreaDTO;
import com.liangzhicheng.modules.entity.query.TestAreaQueryEntity;

import java.util.List;
import java.util.Map;

/**
 * @description 地区名称 Mapper接口
 * @author liangzhicheng
 * @since 2021-08-11
 */
public interface ITestAreaNameDao extends BaseMapper<TestAreaNameEntity> {

    /**
     * @description 获取地区总记录
     * @param areaQuery
     * @return long
     */
    Long getCount(TestAreaQueryEntity areaQuery);

    /**
     * @description 获取地区列表
     * @param areaQuery
     * @return List<Map<String, Object>>
     */
    List<Map<String, Object>> listArea(TestAreaQueryEntity areaQuery);

    /**
     * @description 查询地区信息
     * @param areaDTO
     * @return List<Map<String, Object>>
     */
    List<Map<String, Object>> getArea(TestAreaDTO areaDTO);

}
