package com.fosun.estest;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private RestHighLevelClient client;

    @RequestMapping("/createindex")
    public String createIndex() {
        String indexName = "product";
        String typeName = "productinfo";
        Settings settings = Settings.builder()
                .put("index.number_of_shards", 3)
                .put("index.number_of_replicas", 1)
                .build();
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName, settings);

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


        createIndexRequest.mapping(typeName, builder);
        CreateIndexResponse createIndexResponse=null;
        try {
            createIndexResponse = client.indices().create(createIndexRequest);
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }

        if (Objects.isNull(createIndexResponse)) {
            return "fail";
        }

        return "ok";
    }
}
