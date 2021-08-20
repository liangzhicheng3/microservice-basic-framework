package com.liangzhicheng.modules.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * 关系型数据库          Elasticsearch
 * Database(数据库)     Indices(索引)
 * Tables(表) 	       Types(类型)
 * Rows(行) 	       Documents(文档)
 * Columns(列) 	       Fields(字段)
 */
@Document(indexName = "test_product", type = "test_product") //配置对应的索引和类型
public class ElasticProductEntity {

    /**
     * 商品id
     */
    @Id //文档的id
    private String id;

    /**
     * 商品名称
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String name;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 库存
     */
    @Field(type = FieldType.Integer)
    private Integer stock;

}
