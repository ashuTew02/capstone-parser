package com.capstone.parser.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.capstone.parser.config.ApplicationProperties;
import com.capstone.parser.dto.event.ScanParseJobEvent;
import com.capstone.parser.dto.event.payload.ScanParseJobEventPayload;
import com.capstone.parser.model.Tool;
import com.capstone.parser.repository.TenantRepository;
import com.capstone.parser.service.processor.CodeScanJobProcessorService;
import com.capstone.parser.service.processor.DependabotScanJobProcessorService;
import com.capstone.parser.service.processor.SecretScanJobProcessorService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ParserJobConsumer {

    private final ObjectMapper objectMapper;
    private final CodeScanJobProcessorService codeScanJobProcessorService;
    private final DependabotScanJobProcessorService dependabotScanJobProcessorService;
    private final SecretScanJobProcessorService secretScanJobProcessorService;
    private final ApplicationProperties appProperties;
    private final TenantRepository tenantRepository;

    public ParserJobConsumer(ObjectMapper objectMapper,
                             CodeScanJobProcessorService codeScanJobProcessorService,
                             DependabotScanJobProcessorService dependabotScanJobProcessorService,
                             SecretScanJobProcessorService secretScanJobProcessorService,
                             ApplicationProperties appProperties,
                             TenantRepository tenantRepository) {
        this.objectMapper = objectMapper;
        this.codeScanJobProcessorService = codeScanJobProcessorService;
        this.dependabotScanJobProcessorService = dependabotScanJobProcessorService;
        this.secretScanJobProcessorService = secretScanJobProcessorService;
        this.appProperties = appProperties;
        this.tenantRepository = tenantRepository;
    }

    @KafkaListener(
        topics = "#{applicationProperties.topic}",  // SpEL to read parser.kafka.topic
        groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(String message) {
        try {
            // Parse the incoming message into ScanJobEvent
            System.out.println("Received ScanParseJobEvent: " + message);
            ScanParseJobEvent event = objectMapper.readValue(message, ScanParseJobEvent.class);
            ScanParseJobEventPayload payload = event.getPayload();
            Tool tool = payload.getTool();
            String filePath = payload.getScanFilePath();
            Long tenantId = payload.getTenantId();
            String esIndexOfFindings = tenantRepository.findEsIndexByTenantId(tenantId);

            switch (tool) {
                case CODE_SCAN:
                    codeScanJobProcessorService.processJob(filePath, esIndexOfFindings);
                    break;
                case DEPENDABOT:
                    dependabotScanJobProcessorService.processJob(filePath, esIndexOfFindings);
                    break;
                case SECRET_SCAN:
                    secretScanJobProcessorService.processJob(filePath, esIndexOfFindings);
                    break;
                default:
                    // Log unknown type or handle error
                    System.err.println("Unknown scan type: " + tool.getValue());
                    break;
            }

        } catch (Exception e) {
            // Log and handle exception
            e.printStackTrace();
        }
    }
}
