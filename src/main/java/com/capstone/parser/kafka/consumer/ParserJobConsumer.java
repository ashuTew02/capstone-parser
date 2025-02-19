package com.capstone.parser.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.capstone.parser.dto.event.job.ScanParseJobEvent;
import com.capstone.parser.dto.event.payload.job.ScanParseJobEventPayload;
import com.capstone.parser.model.JobStatus;
import com.capstone.parser.model.Tool;
import com.capstone.parser.repository.TenantRepository;
import com.capstone.parser.service.processor.CodeScanJobProcessorService;
import com.capstone.parser.service.processor.DependabotScanJobProcessorService;
import com.capstone.parser.service.processor.SecretScanJobProcessorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.capstone.parser.kafka.producer.AckScanParseJobEventProducer;

@Component
public class ParserJobConsumer {

    private final ObjectMapper objectMapper;
    private final CodeScanJobProcessorService codeScanJobProcessorService;
    private final DependabotScanJobProcessorService dependabotScanJobProcessorService;
    private final SecretScanJobProcessorService secretScanJobProcessorService;
    private final TenantRepository tenantRepository;
    private final AckScanParseJobEventProducer ackProducer;

    public ParserJobConsumer(ObjectMapper objectMapper,
                             CodeScanJobProcessorService codeScanJobProcessorService,
                             DependabotScanJobProcessorService dependabotScanJobProcessorService,
                             SecretScanJobProcessorService secretScanJobProcessorService,
                             TenantRepository tenantRepository,
                             AckScanParseJobEventProducer ackProducer) {
        this.objectMapper = objectMapper;
        this.codeScanJobProcessorService = codeScanJobProcessorService;
        this.dependabotScanJobProcessorService = dependabotScanJobProcessorService;
        this.secretScanJobProcessorService = secretScanJobProcessorService;
        this.tenantRepository = tenantRepository;
        this.ackProducer = ackProducer;
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

            // 1) Process the job
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
                    System.err.println("Unknown scan type: " + tool.getValue());
                    return;
            }

            // 2) Produce ACK to JFC
            System.out.println("10. Parser Processes ScanParseJobEvent id: " + event.getEventId());

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
