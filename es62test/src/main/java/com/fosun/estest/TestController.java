package com.fosun.estest;

import com.alibaba.fastjson.JSON;
import com.fosun.domain.ProductInfoLangEntity;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.metadata.MetaDataIndexTemplateService;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.filter.FiltersAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private RestHighLevelClient client;

    private final String INDEX_NAME = "product";

    @RequestMapping("/getproduct")
    public ProductInfoLangEntity getTest(@RequestParam("id") String id) throws Exception {
        GetRequest getRequest = new GetRequest(INDEX_NAME);
        getRequest.type("productinfo").id(id);

        GetResponse getResponse = client.get(getRequest);
        String source = getResponse.getSourceAsString();

        ProductInfoLangEntity productInfoLangEntity = JSON.parseObject(source, ProductInfoLangEntity.class);
        return productInfoLangEntity;
    }

    @RequestMapping("/search")
    public PagedResult<List<ProductInfoLangEntity>> searchTest(@RequestParam("keyword") String keyWord) throws Exception {
        List<ProductInfoLangEntity> list = Lists.newArrayList();
        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
        searchRequest.types("productinfo");

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if(StringUtils.isNumeric(keyWord)){
            TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("id",Long.valueOf(keyWord));
            boolQueryBuilder.should().add(termQueryBuilder);
//            boolQueryBuilder.should().add(QueryBuilders.commonTermsQuery("id",keyWord));
        }else {
            boolQueryBuilder.should().add(QueryBuilders.matchQuery("subtitle", keyWord));
        }

//        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("birthday").lte("1604037076026").gte("1524019277016");
//        boolQueryBuilder.must().add(rangeQueryBuilder);

        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource().query(boolQueryBuilder);//.aggregation(filterAggregationBuilder);
        searchRequest.source(searchSourceBuilder);
        searchSourceBuilder.size(20).from(0);
        SearchResponse searchResponse = client.search(searchRequest);

        if (searchResponse.getHits().getTotalHits() > 0) {
            for (SearchHit searchHit : searchResponse.getHits().getHits()) {
                String source = searchHit.getSourceAsString();
                ProductInfoLangEntity test = JSON.parseObject(source, ProductInfoLangEntity.class);
                list.add(test);
            }
        }

        PagedResult<List<ProductInfoLangEntity>> result = new PagedResult<>();
        result.setData(list);
        result.setTotal((int)searchResponse.getHits().getTotalHits());
        return result;
    }

//    @RequestMapping("/update")
//    public String updateTest() throws Exception {
//        User user = getTest("101");
//        user.setDesc("想预测狗狗每天都在做什么？没问题，AI可以告诉你答案");
//        user.setBirthday(new Date());
//
//        UpdateRequest updateRequest = new UpdateRequest("foliday", "user", "101");
//        updateRequest.doc(JSON.toJSONString(user), XContentType.JSON);
//        UpdateResponse updateResponse = client.update(updateRequest);
//        if (updateResponse.getResult() == DocWriteResponse.Result.CREATED) {
//            return "created";
//        } else if (updateResponse.getResult() == DocWriteResponse.Result.UPDATED) {
//            return "updated";
//        } else if (updateResponse.getResult() == DocWriteResponse.Result.DELETED) {
//            return "deleted";
//        } else if (updateResponse.getResult() == DocWriteResponse.Result.NOOP) {
//            return "noop";
//        }
//        return "";
//    }
//
//    @RequestMapping("/insert")
//    public String insertTest() throws Exception {
//        IndexRequest indexRequest = new IndexRequest("foliday", "user");
//        indexRequest.id("101");
//
//        ProductInfoLangEntity user = new User();
//        user.setUserid(101);
//        user.setUserName("张三");
//        user.setBirthday(new Date());
//        user.setDesc("想预测狗狗每天都在做什么？没问题，AI可以告诉你答案" );
//
//        indexRequest.source(JSON.toJSONString(user), XContentType.JSON);
//
//        IndexResponse indexResponse = client.index(indexRequest);
//
//        if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
//            return "created";
//        } else if (indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
//            return "updated";
//        }
//        return "fail";
//    }

}
