package com.liangzhicheng.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @description 订单实体类
 * @author liangzhicheng
 * @since 2021-07-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("test_order")
public class TestOrderEntity extends Model<TestOrderEntity> {

    private static final long serialVersionUID = 1L;

    /**
     * 订单id
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户名称
     */
    private String username;

    /**
     * 商品id
     */
    private String productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品单价
     */
    private BigDecimal productPrice;

    /**
     * 购买数量
     */
    private Integer buyNum;

    @Override
    public Serializable pkVal() {
        return null;
    }

    public TestOrderEntity() {
        super();
    }

    public TestOrderEntity(String id, String userId, String username, String productId, String productName, BigDecimal productPrice, Integer buyNum) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.buyNum = buyNum;
    }

}
