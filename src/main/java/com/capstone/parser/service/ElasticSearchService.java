package com.capstone.parser.service;

import org.springframework.stereotype.Service;

import com.capstone.parser.model.Finding;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;

@Service
public class ElasticSearchService {

    private final ElasticsearchClient esClient;

    public ElasticSearchService(ElasticsearchClient esClient) {
        this.esClient = esClient;
    }

    /**
     * Save the Finding document to the "findings" index in ES.
     */
    public void saveFinding(Finding finding) {
        try {
            IndexRequest<Finding> request = IndexRequest.of(builder ->
                builder.index("findings")
                       .id(finding.getId()) // if you want to use the ID as doc ID
                       .document(finding)
            );
            IndexResponse response = esClient.index(request);
            // handle response if needed
        } catch (Exception e) {
            // log or handle
            e.printStackTrace();
        }
    }
}
