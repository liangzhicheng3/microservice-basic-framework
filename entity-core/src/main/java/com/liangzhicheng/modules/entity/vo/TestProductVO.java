package com.liangzhicheng.modules.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @description 商品VO类
 * @author liangzhicheng
 * @since 2021-07-30
 */
@Data
@ApiModel(value="TestProductVO")
public class TestProductVO {

    @ApiModelProperty("商品id")
    private String id;
    @ApiModelProperty("商品名称")
    private String name;
    @ApiModelProperty("商品价格")
    private BigDecimal price;
    @ApiModelProperty("库存")
    private Integer stock;

}
