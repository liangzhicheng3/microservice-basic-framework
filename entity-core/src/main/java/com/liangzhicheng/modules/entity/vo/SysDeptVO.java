package com.liangzhicheng.modules.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description 部门VO类
 * @author liangzhicheng
 * @since 2021-08-09
 */
@Data
@ApiModel(value="SysDeptVO")
public class SysDeptVO {

    @ApiModelProperty("部门id(主键)")
    private String id;
    @ApiModelProperty("部门名称")
    private String name;
    @ApiModelProperty("部门描述")
    private String description;
    @ApiModelProperty("排序")
    private Integer rank;

}
