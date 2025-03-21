package com.capstone.parser.kafka.consumer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.capstone.parser.dto.event.RunbookTriggerEvent;
import com.capstone.parser.dto.event.job.ScanParseJobEvent;
import com.capstone.parser.dto.event.payload.RunbookTriggerEventPayload;
import com.capstone.parser.dto.event.payload.job.ScanParseJobEventPayload;
import com.capstone.parser.model.JobStatus;
import com.capstone.parser.model.KafkaTopic;
import com.capstone.parser.model.Tool;
import com.capstone.parser.model.runbook.RunbookTriggerType;
import com.capstone.parser.repository.TenantRepository;
import com.capstone.parser.service.processor.CodeScanJobProcessorService;
import com.capstone.parser.service.processor.DependabotScanJobProcessorService;
import com.capstone.parser.service.processor.SecretScanJobProcessorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.capstone.parser.kafka.producer.AckScanParseJobEventProducer;
import com.capstone.parser.kafka.producer.RunbookTriggerEventProducer;

@Component
public class ParserJobConsumer {

    private final ObjectMapper objectMapper;
    private final CodeScanJobProcessorService codeScanJobProcessorService;
    private final DependabotScanJobProcessorService dependabotScanJobProcessorService;
    private final SecretScanJobProcessorService secretScanJobProcessorService;
    private final TenantRepository tenantRepository;
    private final AckScanParseJobEventProducer ackProducer;
    private final RunbookTriggerEventProducer runbookProducer;

    public ParserJobConsumer(ObjectMapper objectMapper,
                             CodeScanJobProcessorService codeScanJobProcessorService,
                             DependabotScanJobProcessorService dependabotScanJobProcessorService,
                             SecretScanJobProcessorService secretScanJobProcessorService,
                             TenantRepository tenantRepository,
                             AckScanParseJobEventProducer ackProducer,
                             RunbookTriggerEventProducer runbookProducer) {
        this.objectMapper = objectMapper;
        this.codeScanJobProcessorService = codeScanJobProcessorService;
        this.dependabotScanJobProcessorService = dependabotScanJobProcessorService;
        this.secretScanJobProcessorService = secretScanJobProcessorService;
        this.tenantRepository = tenantRepository;
        this.ackProducer = ackProducer;
        this.runbookProducer = runbookProducer;
    }

    @KafkaListener(
        topics = "#{T(com.capstone.parser.model.KafkaTopic).PARSER_JFC.getTopicName()}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(String message) {
        Long wholeJobId = null;
        try {
            ScanParseJobEvent event = objectMapper.readValue(message, ScanParseJobEvent.class);
            System.out.println("9. Parser Received ScanParseJobEvent from JFC id: " + event.getEventId());

            ScanParseJobEventPayload payload = event.getPayload();
            wholeJobId = payload.getJobId();
            Tool tool = payload.getTool();
            String filePath = payload.getScanFilePath();
            Long tenantId = payload.getTenantId();

            // Potentially find the ES index for the tenant
            String esIndexOfFindings = tenantRepository.findEsIndexByTenantId(tenantId);
            List<String> findingIds;
            // 1) Process the job
            switch (tool) {
                case Tool.CODE_SCAN:
                    findingIds = codeScanJobProcessorService.processJob(filePath, esIndexOfFindings);
                    break;
                case Tool.DEPENDABOT:
                    findingIds = dependabotScanJobProcessorService.processJob(filePath, esIndexOfFindings);
                    break;
                case Tool.SECRET_SCAN:
                    findingIds = secretScanJobProcessorService.processJob(filePath, esIndexOfFindings);
                    break;
                default:
                    findingIds = new ArrayList<>();
                    System.err.println("Unknown scan type: " + tool.getValue());
                    return;
            }

            // 2) Produce ACK to JFC
            System.out.println("10. Parser Processes ScanParseJobEvent id: " + event.getEventId());
            RunbookTriggerEventPayload runbookPayload = new RunbookTriggerEventPayload(tool, tenantId, findingIds, KafkaTopic.BGJOBS_JFC, RunbookTriggerType.NEW_SCAN_INITIATE);
            RunbookTriggerEvent runbookEvent = new RunbookTriggerEvent(runbookPayload);
            runbookProducer.produce(runbookEvent);
            System.out.println(payload.getTool());
            System.out.println("11. Parser Produces RunbookTriggerEvent for job id: " + event.getEventId());
            System.out.println("FINDING ID LIST:: " + findingIds.toString());
            ackProducer.produce(payload.getJobId(), JobStatus.SUCCESS);

            System.out.println("11. Parser Produces AckScanParseJobEvent for job id: " + event.getEventId());

        } catch (Exception e) {
            if(wholeJobId != null) {
                ackProducer.produce(wholeJobId, JobStatus.FAILURE);
            }
            // if(e.getMessage());
            // e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}
