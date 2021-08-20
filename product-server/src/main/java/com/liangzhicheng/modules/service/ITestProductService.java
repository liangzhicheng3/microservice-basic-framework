package com.liangzhicheng.modules.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liangzhicheng.common.page.PageResult;
import com.liangzhicheng.modules.entity.TestProductEntity;
import com.liangzhicheng.modules.entity.dto.TestProductDTO;
import com.liangzhicheng.modules.entity.vo.TestProductVO;

/**
 * @description 商品服务接口类
 * @author liangzhicheng
 * @since 2021-07-30
 */
public interface ITestProductService extends IService<TestProductEntity> {

    /**
     * @description 保存商品
     * @param productDTO
     * @return TestProductVO
     */
    TestProductVO saveProduct(TestProductDTO productDTO);

    /**
     * @description 删除商品
     * @param productDTO
     */
    void deleteProduct(TestProductDTO productDTO);

    /**
     * @description 商品列表
     * @param productDTO
     * @return PageResult
     */
    PageResult listProduct(TestProductDTO productDTO);

    /**
     * @description 获取商品
     * @param productId
     * @return TestProductEntity
     */
    TestProductEntity getProduct(String productId);

}
