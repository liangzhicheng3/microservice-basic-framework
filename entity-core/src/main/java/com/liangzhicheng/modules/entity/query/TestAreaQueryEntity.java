package com.liangzhicheng.modules.entity.query;

import com.liangzhicheng.common.utils.SysToolUtil;
import com.liangzhicheng.modules.entity.dto.TestAreaDTO;
import com.liangzhicheng.modules.entity.query.basic.BaseQueryEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description 地区查询实体类
 * @author liangzhicheng
 * @since 2021-08-11
 */
@Data
@NoArgsConstructor
public class TestAreaQueryEntity extends BaseQueryEntity {

    /**
     * 地区id
     */
    private String areaId;

    /**
     * 地区层级
     */
    private String areaLevel;

    /**
     * 类型
     */
    private String langType;

    /**
     * 长度
     */
    private String length;

    public TestAreaQueryEntity(TestAreaDTO areaDTO) {
        super(areaDTO);
        String areaId = areaDTO.getAreaId();
        if(SysToolUtil.isNotBlank(areaId)) {
            this.areaId = areaId;
            this.length = String.valueOf(areaId.length());
        }
        String areaLevel = areaDTO.getAreaLevel();
        if(SysToolUtil.isNotBlank(areaLevel)) {
            this.areaLevel = areaLevel;
        }
        String langType = "zh_CN";
        this.langType = langType;
    }

}
