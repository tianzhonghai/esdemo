package com.fonsun.es553.controllers;

import com.alibaba.fastjson.JSON;
import com.fonsun.es553.domain.ProductInfo;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private TransportClient client;

    private final String INDEX_NAME = "product";

    @RequestMapping("/search")
    public List<ProductInfo> search(@RequestParam("keyword") String keyWord) {
        List<ProductInfo> list = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
        searchRequest.types("productinfo");

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if(StringUtils.isNumeric(keyWord)){
            TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("id",Long.valueOf(keyWord));
            boolQueryBuilder.should().add(termQueryBuilder);
        }else {
            boolQueryBuilder.should().add(QueryBuilders.matchQuery("subtitle", keyWord));
        }

        SearchResponse searchResponse = client.prepareSearch("product").setTypes("productinfo")
                .setQuery(boolQueryBuilder).get();

        if (searchResponse.getHits().getTotalHits() > 0) {
            for (SearchHit searchHit : searchResponse.getHits().getHits()) {
                String source = searchHit.getSourceAsString();
                ProductInfo test = JSON.parseObject(source, ProductInfo.class);
                list.add(test);
            }
        }

        return list;
    }
}
