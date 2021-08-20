package com.liangzhicheng.modules.entity.query.basic;

import com.liangzhicheng.modules.entity.dto.basic.BaseDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description 基础查询实体类
 * @author liangzhicheng
 * @since 2021-08-11
 */
@Data
@NoArgsConstructor
public class BaseQueryEntity {

    /**
     * 当前页码
     */
    private int pageNo;

    /**
     * 每页数量
     */
    private int pageSize;

    public BaseQueryEntity(BaseDTO baseDTO){
        Integer pageNo = baseDTO.getPageNo();
        Integer pageSize = baseDTO.getPageSize();
        if(pageNo == null || pageNo < 1){
            pageNo = 1;
        }
        if(pageSize == null || pageSize < 1){
            pageSize = 10;
        }
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public int getPageNo(){
        return (this.pageNo - 1) * pageSize;
    }

}
