package com.liangzhicheng.modules.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @description 订单VO类
 * @author liangzhicheng
 * @since 2021-07-30
 */
@Data
@ApiModel(value="TestOrderVO")
public class TestOrderVO {

    @ApiModelProperty("订单id")
    private String id;
    @ApiModelProperty("用户id")
    private String userId;
    @ApiModelProperty("用户名称")
    private String username;
    @ApiModelProperty("商品id")
    private String productId;
    @ApiModelProperty("商品名称")
    private String productName;
    @ApiModelProperty("商品单价")
    private BigDecimal productPrice;
    @ApiModelProperty("购买数量")
    private Integer buyNum;

}
