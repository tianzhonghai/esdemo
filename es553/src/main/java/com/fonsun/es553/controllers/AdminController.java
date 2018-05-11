package com.fonsun.es553.controllers;

import com.alibaba.fastjson.JSON;
import com.fonsun.es553.domain.ProductInfo;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private TransportClient client;

    @RequestMapping("/createindex")
    public String createIndex() {
        String typeName = "productinfo";
        XContentBuilder builder= null;
        try {
            builder = XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject(typeName)
                    .startObject("properties")
                    .startObject("id").field("type", "long").field("store", false).endObject()
                    .startObject("subtitle").field("type", "text").field("store", false).field("analyzer", "ik_max_word").field("search_analyzer", "ik_max_word").endObject()
                    .endObject()
                    .endObject()
                    .endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }


        CreateIndexResponse createIndexResponse = client.admin().indices().prepareCreate("product")
                .setSettings(Settings.builder()
                        .put("index.number_of_shards", 3)
                        .put("index.number_of_replicas", 1)
                )
                .addMapping(typeName, builder)
                .get();

        return "ok";
    }

    @RequestMapping("/createdoc")
    public String createDoc(){
        ProductInfo productInfo = new ProductInfo();
        productInfo.setId(100);
        productInfo.setSubtitle("中华人民共和国国歌");

        String pstr = JSON.toJSONString(productInfo);
        IndexResponse response = client.prepareIndex("product", "productinfo")
                .setSource(pstr , XContentType.JSON)
                .get();

        return "created";
    }
}
