package com.liangzhicheng.modules.repository;

import com.liangzhicheng.modules.entity.ElasticProductEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * 泛型1:操作的对象类型
 * 泛型2:主键id的类型
 */
@Repository
public interface IElasticProductRepository extends ElasticsearchRepository<ElasticProductEntity, String> {
    //可按照jpa方法规范制定高级查询方法
}
