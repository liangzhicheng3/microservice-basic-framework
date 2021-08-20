package com.liangzhicheng.modules.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description 订单相关数据传输对象
 * @author liangzhicheng
 * @since 2021-08-10
 */
@Data
public class TestOrderDTO {

    @ApiModelProperty("订单id")
    private String id;
    @ApiModelProperty("用户id")
    private String userId;
    @ApiModelProperty("商品id")
    private String productId;

}
