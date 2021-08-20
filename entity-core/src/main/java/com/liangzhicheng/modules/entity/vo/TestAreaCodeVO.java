package com.liangzhicheng.modules.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description 地区编码VO类
 * @author liangzhicheng
 * @since 2021-08-11
 */
@Data
@ApiModel(value="TestAreaCodeVO")
public class TestAreaCodeVO {

    @ApiModelProperty("地区id")
    private String areaId;
    @ApiModelProperty("地区编码")
    private String areaCode;
    @ApiModelProperty("地区层级")
    private Integer areaLevel;

}
