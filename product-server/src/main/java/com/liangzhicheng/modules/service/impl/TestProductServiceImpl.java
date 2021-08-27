package com.liangzhicheng.modules.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liangzhicheng.common.constant.ApiConstant;
import com.liangzhicheng.common.exception.CustomizeException;
import com.liangzhicheng.common.exception.TransactionException;
import com.liangzhicheng.common.page.PageResult;
import com.liangzhicheng.common.utils.SysBeanUtil;
import com.liangzhicheng.common.utils.SysToolUtil;
import com.liangzhicheng.modules.dao.ITestProductDao;
import com.liangzhicheng.modules.entity.ElasticProductEntity;
import com.liangzhicheng.modules.entity.TestProductEntity;
import com.liangzhicheng.modules.entity.dto.TestProductDTO;
import com.liangzhicheng.modules.entity.vo.TestProductVO;
import com.liangzhicheng.modules.repository.IElasticProductRepository;
import com.liangzhicheng.modules.service.ITestProductService;
import org.assertj.core.util.Lists;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @description 商品服务实现类
 * @author liangzhicheng
 * @since 2021-07-30
 */
@Service
public class TestProductServiceImpl extends ServiceImpl<ITestProductDao, TestProductEntity> implements ITestProductService {

    @Resource
    private IElasticProductRepository elasticProductRepository;
    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    /**
     * @description 保存商品
     * @param productDTO
     * @return
     */
    @Override
    public TestProductVO saveProduct(TestProductDTO productDTO) {
        TestProductEntity product = new TestProductEntity(SysToolUtil.random(),
                productDTO.getName(), productDTO.getPrice(), productDTO.getStock());
        baseMapper.insert(product);
        elasticProductRepository.save(SysBeanUtil.copyEntity(product, ElasticProductEntity.class));
        SysToolUtil.info("创建商品成功,商品信息为:" + JSONObject.toJSONString(product));
        return SysBeanUtil.copyEntity(product, TestProductVO.class);
    }

    /**
     * @description 删除商品
     * @param productDTO
     */
    @Override
    public void deleteProduct(TestProductDTO productDTO) {
        String productId = productDTO.getId();
        Optional<ElasticProductEntity> optionalProduct = elasticProductRepository.findById(productId);
        if(!optionalProduct.isPresent()){
            throw new CustomizeException(ApiConstant.BASE_FAIL_CODE, "获取远程elasticsearch调用商品信息不存在");
        }
        SysToolUtil.info("查询到{" + productId + "}号商品的信息,内容是:" + JSONObject.toJSONString(optionalProduct.get()));
        baseMapper.deleteById(productId);
        elasticProductRepository.deleteById(productId);
        SysToolUtil.info("删除{" + productId + "}号商品成功");
    }

    /**
     * @description 商品列表
     * @param productDTO
     * @return PageResult
     */
    @Override
    public PageResult<ElasticProductEntity> listProduct(TestProductDTO productDTO) {
        String name = productDTO.getName();
        Integer stock = productDTO.getStock();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if(SysToolUtil.isNotBlank(name)){
            queryBuilder.must(QueryBuilders.matchQuery("name", name));
        }
        if(SysToolUtil.isNotNull(stock) && stock > 0){
            queryBuilder.must(QueryBuilders.rangeQuery("stock").gte(300).lte(800)); //库存区间查询
        }
        //设置分页条件
        int pageNo = SysToolUtil.getPageNo(productDTO.getPageNo());
        int pageSize = SysToolUtil.getPageSize(productDTO.getPageSize());
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        //设置排序条件
        FieldSortBuilder sortBuilder = SortBuilders.fieldSort("stock").order(SortOrder.ASC);
        //设置高亮条件
        HighlightBuilder highlightBuilder = getHighlightBuilder(name);
        //构建查询条件后组装条件
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withPageable(pageRequest)
                .withSort(sortBuilder)
                .withHighlightBuilder(highlightBuilder)
                .build();
        SearchHits<ElasticProductEntity> searchHits =
                elasticsearchRestTemplate.search(searchQuery, ElasticProductEntity.class);
        List<ElasticProductEntity> records = Lists.newArrayList();
        if(searchHits.getTotalHits() > 0){
            records = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        }
        return new PageResult<>(pageNo, pageSize, records, (int) searchHits.getTotalHits());
    }

    /**
     * @description 获取商品
     * @param productId
     * @return TestProductEntity
     */
    @Override
    public TestProductEntity getProduct(String productId) {
        if(SysToolUtil.isBlank(productId)){
            throw new TransactionException(ApiConstant.PARAM_IS_NULL);
        }
        Optional<ElasticProductEntity> optionalProduct = elasticProductRepository.findById(productId);
        if(!optionalProduct.isPresent()){
            throw new CustomizeException(ApiConstant.BASE_FAIL_CODE, "获取远程elasticsearch调用商品信息不存在");
        }
        SysToolUtil.info("查询到{" + productId + "}号商品的信息,内容是:" + JSONObject.toJSONString(optionalProduct.get()));
        return SysBeanUtil.copyEntity(optionalProduct.get(), TestProductEntity.class);
    }

    /**
     * @description 获取设置高亮后参数
     * @param fields
     * @return HighlightBuilder
     */
    private HighlightBuilder getHighlightBuilder(String ... fields){
        //生成高亮构造器
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        for(String field : fields){
            //高亮查询字段
            highlightBuilder.field(field);
        }
        //多个字段高亮,requireFieldMatch要为false
        highlightBuilder.requireFieldMatch(false);
        //设置高亮
        highlightBuilder.preTags("<span style=\"color=red\">");
        highlightBuilder.postTags("</span>");
        //高亮如文字内容等多个字段,必须配置fragmentSize,不然出现高亮不全,内容缺失等
        highlightBuilder.fragmentSize(800000); //最大高亮分片数
        highlightBuilder.numOfFragments(0); //从第一个分片获取高亮片段
        return highlightBuilder;
    }

}
