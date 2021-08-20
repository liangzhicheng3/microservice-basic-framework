package com.liangzhicheng.modules.entity.dto;

import com.liangzhicheng.modules.entity.dto.basic.BaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @description 商品相关数据传输对象
 * @author liangzhicheng
 * @since 2021-08-11
 */
@Data
public class TestProductDTO extends BaseDTO {

    @ApiModelProperty("商品id")
    private String id;
    @ApiModelProperty("商品名称")
    private String name;
    @ApiModelProperty("商品价格")
    private BigDecimal price;
    @ApiModelProperty("库存")
    private Integer stock;

    public TestProductDTO() {
        super();
    }

    public TestProductDTO(String id) {
        this.id = id;
    }

}
