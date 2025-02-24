package com.capstone.parser.service.processor;

import com.capstone.parser.model.*;
import com.capstone.parser.service.ElasticSearchService;
import com.capstone.parser.service.StateSeverityMapper;
import com.capstone.parser.service.github.mapper.GitHubSecretScanMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Service
public class SecretScanJobProcessorService implements ScanJobProcessorService {

    private final ElasticSearchService elasticSearchService;
    private final ObjectMapper objectMapper;
    private final GitHubSecretScanMapper mapper;

    public SecretScanJobProcessorService(ElasticSearchService elasticSearchService,
                                         ObjectMapper objectMapper,
                                         GitHubSecretScanMapper mapper) {
        this.elasticSearchService = elasticSearchService;
        this.objectMapper = objectMapper;
        this.mapper = mapper;
    }

    @Override
    public void processJob(String filePath, String esIndexOfFindings) throws Exception {
        List<Map<String, Object>> alerts = objectMapper.readValue(
                new File(filePath),
                new TypeReference<List<Map<String, Object>>>() {}
        );

        for (Map<String, Object> alert : alerts) {
            Finding finding = mapAlertToFinding(alert);
            elasticSearchService.saveFinding(finding, esIndexOfFindings);
        }
    }

    private Finding mapAlertToFinding(Map<String, Object> alert) {
        String uniqueId = UUID.randomUUID().toString();

        String ghState = (String) alert.get("state");
        String url = (String) alert.get("url");

        String secretTypeDisplay = (String) alert.get("secret_type_display_name");
        String secretType = (String) alert.get("secret_type");
        String resolution = (String) alert.get("resolution");


        // GH has no explicit severity for secrets, we define default = HIGH or CRITICAL
        FindingSeverity internalSeverity = StateSeverityMapper.mapGitHubSeverity(null);

        // Possibly "resolved" => "fixed"
        // "open" => "open"
        // "dismissed" => "suppressed"? 
        // etc. No "dismissed_reason" typically for secret scanning, but let's just pass null
        FindingState internalState = mapper.toFindingState(ghState, resolution);

        Finding finding = new Finding();
        finding.setId(uniqueId);
        finding.setTitle(secretTypeDisplay);
        finding.setDesc("Secret found in repo (type: " + secretType + ")");
        finding.setSeverity(internalSeverity);
        finding.setState(internalState);
        finding.setUrl(url);
        finding.setToolType(Tool.SECRET_SCAN);
        finding.setCve(null);
        finding.setCwes(new ArrayList<>());
        finding.setCvss(null);
        finding.setType(secretType);
        finding.setSuggestions("Rotate or revoke this secret immediately");
        finding.setFilePath(null); 
        finding.setComponentName(null);
        finding.setComponentVersion(null);
        finding.setToolAdditionalProperties(alert);
        finding.setTicketId(null);


        return finding;
    }
}
