package com.liangzhicheng;

import com.liangzhicheng.modules.entity.ElasticProductEntity;
import com.liangzhicheng.modules.entity.query.PageResult;
import com.liangzhicheng.modules.repository.IElasticProductRepository;
import org.assertj.core.util.Lists;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ElasticTest {

    /**
     * ElasticsearchTemplate:框架封装用于便捷操作Elasticsearch的模板类
     * ElasticsearchRepository:框架封装用于便捷完成常用操作的工具接口
     *
     * NativeSearchQueryBuilder:用于生成查询条件的构建器,需要去封装各种查询条件
     * QueryBuilder:表示一个查询条件,其对象可以通过QueryBuilders工具类中的方法快速生成各种条件
     *    boolQuery():生成bool条件,相当于 "bool": { }
     *    matchQuery():生成match条件,相当于 "match": { }
     *    rangeQuery()：生成range条件,相当于 "range": { }
     * AbstractAggregationBuilder:用于生成分组查询的构建器,其对象通过AggregationBuilders工具类生成
     * Pageable:表示分页参数,对象通过PageRequest.of(当前页码, 每页数量)获取
     * SortBuilder:排序构建器,对象通过SortBuilders.fieldSort(字段).order(规则)获取
     */

    @Resource
    private IElasticProductRepository elasticProductRepository;
    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Test
    public void saveOrUpdate(){
        //框架底层根据索引库中是否存在该id来决定是新增还是更新
        ElasticProductEntity product = new ElasticProductEntity(
                "3", "ElasticsearchTemplate:框架封装用于便捷操作Elasticsearch的模板类", new BigDecimal(0.01), 10);
        elasticProductRepository.save(product);
    }

    @Test
    public void delete(){
        elasticProductRepository.deleteById("3");
    }

    @Test
    public void get(){
        Optional<ElasticProductEntity> product = elasticProductRepository.findById("1");
        product.ifPresent(System.out::println);
    }

    @Test
    public void list(){
        Iterable<ElasticProductEntity> list = elasticProductRepository.findAll();
        for(ElasticProductEntity product : list){
            System.out.println(product);
        }
    }

    @Test
    public void page1(){
        String name1 = "封装";
        int stock = 99;
        int pageNo = 1, pageSize = 10;
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        /**
         * must:相当于等于
         * should:相当于或者
         *
         * matchQuery:会将关键字分词处理后与目标查询字段进行匹配,若分词中任意一个词与目标查询字段匹配上，则可查询到
         * termQuery:不会将关键字进行分词处理，而是作为一个整体与目标查询字段进行匹配，若完全匹配，则可查询到
         */
        queryBuilder.must(QueryBuilders.matchQuery("name", name1));
//        queryBuilder.must(QueryBuilders.rangeQuery("stock").gte(80).lte(112)); //库存区间查询
        //设置分页条件
        PageRequest pageRequest = PageRequest.of(pageNo - 1, pageSize);
        //设置排序条件
        FieldSortBuilder sortBuilder = SortBuilders.fieldSort("stock").order(SortOrder.ASC);
        //设置高亮条件
        HighlightBuilder highlightBuilder = getHighlightBuilder(name1);
        //构建查询条件后组装条件
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withPageable(pageRequest)
                .withSort(sortBuilder)
                .withHighlightBuilder(highlightBuilder)
                .build();
        SearchHits<ElasticProductEntity> searchHits =
                elasticsearchRestTemplate.search(searchQuery, ElasticProductEntity.class);
        List<ElasticProductEntity> resultList = Lists.newArrayList();
        if(searchHits.getTotalHits() > 0){
            resultList = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        }
        PageResult<ElasticProductEntity> pageResult =
                new PageResult<ElasticProductEntity>(pageNo, pageSize, resultList, (int) searchHits.getTotalHits());
        System.out.println(pageResult);
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
