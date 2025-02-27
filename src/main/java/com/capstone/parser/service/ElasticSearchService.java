package com.capstone.parser.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.capstone.parser.dto.DeduplicatorResponse;
import com.capstone.parser.model.Finding;
import com.capstone.parser.model.Tool;
import com.capstone.parser.service.deduplicator.ToolDeduplicatorService;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;

@Service
public class ElasticSearchService {

    private final ElasticsearchClient esClient;
    private final ToolDeduplicatorService toolDeduplicatorService;

    public ElasticSearchService(ElasticsearchClient esClient, ToolDeduplicatorService toolDeduplicatorService) {
        this.esClient = esClient;
        this.toolDeduplicatorService = toolDeduplicatorService;
    }

    public Finding saveFinding(Finding finding, String esIndexOfFindings) {
        try {
            List<Finding> findings = searchFindings(finding.getToolType(), esIndexOfFindings);
            DeduplicatorResponse deduplicatorResponse = toolDeduplicatorService.checkDuplication(finding, findings);

            Boolean canSave = deduplicatorResponse.getCanSave();
            if(!canSave) {
                return deduplicatorResponse.getOldFinding();
            }

            finding.setUpdatedAt(LocalDateTime.now().toString());

            Boolean isNewEntry = deduplicatorResponse.getIsNewEntry();
            if(isNewEntry) { 
                finding.setCreatedAt(LocalDateTime.now().toString());
                
                IndexRequest<Finding> request = IndexRequest.of(builder ->
                    builder.index(esIndexOfFindings)
                        .id(finding.getId())
                        .document(finding)
                    );
                IndexResponse response = esClient.index(request);
                System.out.println("Saved " + finding.getToolType() +" job to ES findings index with _id: " + response.id());
                return finding;
            } else {
                // We have the old document already in Elasticsearch
                Finding oldFinding = deduplicatorResponse.getOldFinding();
            
                // Overwrite fields in oldFinding with the new data from 'finding'
                // (except for id and createdAt, which we want to preserve in oldFinding)
                oldFinding.setTitle(finding.getTitle());
                oldFinding.setDesc(finding.getDesc());
                oldFinding.setSeverity(finding.getSeverity());
                oldFinding.setState(finding.getState());
                // oldFinding.setUrl(finding.getUrl());
                // oldFinding.setToolType(finding.getToolType());
                // oldFinding.setCve(finding.getCve());
                // oldFinding.setCwes(finding.getCwes());
                // oldFinding.setCvss(finding.getCvss());
                // oldFinding.setType(finding.getType());
                // oldFinding.setSuggestions(finding.getSuggestions());
                // oldFinding.setFilePath(finding.getFilePath());
                // oldFinding.setComponentName(finding.getComponentName());
                // oldFinding.setComponentVersion(finding.getComponentVersion());
                oldFinding.setToolAdditionalProperties(finding.getToolAdditionalProperties());
            
                // Update timestamp
                oldFinding.setUpdatedAt(LocalDateTime.now().toString());
            
                // Re-index the (updated) oldFinding using the same _id
                IndexRequest<Finding> request = IndexRequest.of(builder ->
                    builder.index(esIndexOfFindings)
                           .id(oldFinding.getId())     // same Elasticsearch ID
                           .document(oldFinding)
                );
                IndexResponse response = esClient.index(request);
                System.out.println("Updated " + finding.getToolType() 
                                   + " job in ES findings index with _id: " 
                                   + response.id());
                return oldFinding;
            }



        } catch (Exception e) {
            // log or handle
            e.printStackTrace();
            return new Finding();
        }
    }

    public List<Finding> searchFindings(Tool toolType, String esIndexOfFindings) {
        List<Finding> allFindings = new ArrayList<>();

        // Build a term query for the "toolType" field. Adjust field name as needed.
        Query query = QueryBuilders.term(t -> t
                .field("toolType.keyword")      //might have to change*******************************************
                .value(toolType.toString())
        );

        final int size = 1000;  // batch size
        int from = 0;

        try {
            while (true) {
                final int currentFrom = from;
                SearchResponse<Finding> response = esClient.search(s -> s
                        .index(esIndexOfFindings)
                        .query(query)
                        .from(currentFrom)
                        .size(size)
                        // Track total hits if you want an exact total count
                        .trackTotalHits(th -> th.enabled(true)),
                    Finding.class
                );

                // Map the hits to Finding objects
                List<Finding> findingsBatch = response.hits().hits().stream()
                        .map(Hit::source)
                        .collect(Collectors.toList());

                // If we got no results, we're done
                if (findingsBatch.isEmpty()) {
                    break;
                }

                allFindings.addAll(findingsBatch);

                // If we received fewer than 'size', we've reached the end
                if (findingsBatch.size() < size) {
                    break;
                }

                from += size;  // Move to the next batch
            }
        } catch (ElasticsearchException e) {
            // Handle Elasticsearch-specific exceptions
            e.printStackTrace();
            // throw e;  // or handle/log as appropriate
        } catch (IOException e) {
            // Handle IO exceptions (network, etc.)
            e.printStackTrace();
            // throw e;  // or handle/log as appropriate
        }

        return allFindings;
}



}
