package com.liangzhicheng.modules.entity.dto.basic;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description 基础数据传输对象
 * @author liangzhicheng
 * @since 2021-08-10
 */
@Data
public class BaseDTO {

    @ApiModelProperty(value = "当前页码", required = true)
    private Integer pageNo;
    @ApiModelProperty(value = "每页数量", required = true)
    private Integer pageSize;

}
