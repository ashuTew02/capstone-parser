package com.capstone.parser.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.capstone.parser.config.ApplicationProperties;
import com.capstone.parser.dto.ScanJobEvent;
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

    public ParserJobConsumer(ObjectMapper objectMapper,
                             CodeScanJobProcessorService codeScanJobProcessorService,
                             DependabotScanJobProcessorService dependabotScanJobProcessorService,
                             SecretScanJobProcessorService secretScanJobProcessorService,
                             ApplicationProperties appProperties) {
        this.objectMapper = objectMapper;
        this.codeScanJobProcessorService = codeScanJobProcessorService;
        this.dependabotScanJobProcessorService = dependabotScanJobProcessorService;
        this.secretScanJobProcessorService = secretScanJobProcessorService;
        this.appProperties = appProperties;
    }

    @KafkaListener(
        topics = "#{applicationProperties.topic}",  // SpEL to read parser.kafka.topic
        groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(String message) {
        try {
            // Parse the incoming message into ScanJobEvent
            System.out.println("Received ScanJobEvent: " + message);
            ScanJobEvent event = objectMapper.readValue(message, ScanJobEvent.class);
            String type = event.getType();
            String filePath = event.getScanFilePath();
            String esIndexOfFindings = event.getEsIndexOfFindings();

            switch (type) {
                case "code-scan":
                    codeScanJobProcessorService.processJob(filePath, esIndexOfFindings);
                    break;
                case "dependabot":
                    dependabotScanJobProcessorService.processJob(filePath, esIndexOfFindings);
                    break;
                case "secret-scan":
                    secretScanJobProcessorService.processJob(filePath, esIndexOfFindings);
                    break;
                default:
                    // Log unknown type or handle error
                    System.err.println("Unknown scan type: " + type);
                    break;
            }

        } catch (Exception e) {
            // Log and handle exception
            e.printStackTrace();
        }
    }
}
