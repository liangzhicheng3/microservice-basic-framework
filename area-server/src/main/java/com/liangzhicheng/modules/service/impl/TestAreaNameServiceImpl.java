package com.liangzhicheng.modules.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.liangzhicheng.common.page.PageResult;
import com.liangzhicheng.common.utils.SysBeanUtil;
import com.liangzhicheng.common.utils.SysToolUtil;
import com.liangzhicheng.modules.dao.ITestAreaNameDao;
import com.liangzhicheng.modules.entity.TestAreaNameEntity;
import com.liangzhicheng.modules.entity.dto.TestAreaDTO;
import com.liangzhicheng.modules.entity.query.TestAreaQueryEntity;
import com.liangzhicheng.modules.entity.vo.TestAreaNameVO;
import com.liangzhicheng.modules.service.ITestAreaNameService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @description 地区名称 服务实现类
 * @author liangzhicheng
 * @since 2021-08-11
 */
@Service
public class TestAreaNameServiceImpl extends ServiceImpl<ITestAreaNameDao, TestAreaNameEntity> implements ITestAreaNameService {

    /**
     * @description 获取地区列表
     * @param areaDTO
     * @return PageResult
     */
    @Override
    public PageResult<TestAreaNameVO> listArea(TestAreaDTO areaDTO) {
        TestAreaQueryEntity areaQuery = new TestAreaQueryEntity(areaDTO);
        Integer pageNo = areaQuery.getPageNo();
        Integer pageSize = areaQuery.getPageSize();
        Long count = baseMapper.getCount(areaQuery);
        if(count.intValue() < 1){
            return new PageResult<>(pageNo, pageSize, Collections.emptyList(), count.intValue());
        }
        List<Map<String, Object>> areaNameList = baseMapper.listArea(areaQuery);
        List<TestAreaNameVO> areaNameVOList = Lists.newArrayList();
        if(SysToolUtil.listSizeGT(areaNameList)){
            TestAreaNameVO areaNameVO = null;
            for(Map<String, Object> area : areaNameList){
                areaNameVO = SysBeanUtil.copyEntity(area, TestAreaNameVO.class);
                areaNameVO.setAreaId(String.valueOf(area.get("areaId")));
                areaNameVO.setAreaCode(String.valueOf(area.get("areaCode")));
                areaNameVO.setAreaName(String.valueOf(area.get("areaName")));
                areaNameVO.setAreaLevel(Integer.parseInt(String.valueOf(area.get("areaLevel"))));
                areaNameVOList.add(areaNameVO);
            }
        }
        return new PageResult<>(pageNo, pageSize, areaNameVOList, count.intValue());
    }

    /**
     * @description 查询地区信息
     * @param areaDTO
     * @return List<Map<String, Object>>
     */
    @Override
    public List<Map<String, Object>> getArea(TestAreaDTO areaDTO) {
        return baseMapper.getArea(areaDTO);
    }

}
