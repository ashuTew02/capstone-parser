package com.capstone.parser.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.capstone.parser.dto.event.AckScanParseJobEvent;
import com.capstone.parser.dto.event.payload.AckJobEventPayload;
import com.capstone.parser.model.KafkaTopic;
import com.capstone.parser.model.JobStatus; // or your enum location
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AckScanParseJobEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public AckScanParseJobEventProducer(KafkaTemplate<String, String> kafkaTemplate,
                                        ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void produce(Long jobId, JobStatus status) {
        try {
            AckJobEventPayload payload = new AckJobEventPayload(jobId, status);
            AckScanParseJobEvent ackEvent = new AckScanParseJobEvent(payload);

            String json = objectMapper.writeValueAsString(ackEvent);

            kafkaTemplate.send(KafkaTopic.ACK_JOB.getTopicName(),
                               AckScanParseJobEvent.class.getName(),
                               json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
