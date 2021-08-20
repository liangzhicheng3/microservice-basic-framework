package com.liangzhicheng.modules.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description 权限菜单VO类
 * @author liangzhicheng
 * @since 2021-08-09
 */
@Data
@ApiModel(value="SysPermMenuVO")
public class SysPermMenuVO {

    @ApiModelProperty("权限菜单id(主键)")
    private String id;
    @ApiModelProperty("权限id")
    private String permId;
    @ApiModelProperty("权限名称")
    private String permName;
    @ApiModelProperty("菜单id")
    private String menuId;
    @ApiModelProperty("菜单名称")
    private String menuName;

}
