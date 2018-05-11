package com.fosun.estest;

import com.alibaba.fastjson.JSON;
import com.fosun.domain.ProductInfoLangEntity;
import com.fosun.domain.ProductInfoLangMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class TestTask {
    @Autowired
    private ProductInfoLangMapper productInfoLangMapper;

    @Autowired
    private RestHighLevelClient client;

    @Scheduled(fixedDelay = 1000  )
    public void doSync(){
        if( 1 < 2)return;

        List<ProductInfoLangEntity> list = productInfoLangMapper.selectAll();

        if(! CollectionUtils.isEmpty(list)){
            for (ProductInfoLangEntity item : list) {
                IndexRequest indexRequest = new IndexRequest("product","productinfo");
                indexRequest.id(Integer.toString(item.getId()));
                String productJson = JSON.toJSONString(item);
                indexRequest.source(productJson, XContentType.JSON);
                try {
                    IndexResponse indexResponse = client.index(indexRequest);
                    if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
                        log.info(item.getId() + "|"+item.getSubtitle()+"|created");
                    } else if (indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
                        log.info(item.getId() + "|"+item.getSubtitle()+"|updated");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
