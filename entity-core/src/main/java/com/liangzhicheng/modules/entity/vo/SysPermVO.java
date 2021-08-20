package com.liangzhicheng.modules.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description 权限VO类
 * @author liangzhicheng
 * @since 2021-08-09
 */
@Data
@ApiModel(value="SysPermVO")
public class SysPermVO {

    @ApiModelProperty("权限id")
    private String id;
    @ApiModelProperty("权限名称")
    private String name;
    @ApiModelProperty("表达式")
    private String expression;

}
