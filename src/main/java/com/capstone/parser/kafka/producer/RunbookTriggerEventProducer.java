package com.capstone.parser.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.capstone.parser.dto.event.RunbookTriggerEvent;
import com.capstone.parser.model.KafkaTopic; // The new enum
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RunbookTriggerEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public RunbookTriggerEventProducer(KafkaTemplate<String, String> kafkaTemplate,
                                       ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void produce(RunbookTriggerEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(KafkaTopic.JOBINGESTION_JFC.getTopicName(),
                event.getEventId(),
                    json);
            System.out.println("PRODUCED RUNBOOKTRIGGEREVENT to "+ KafkaTopic.JOBINGESTION_JFC.getTopicName() + ", tool" + event.getPayload().getTool());
        } catch (JsonProcessingException e) {
            System.out.println("Error producing RunbookTriggerEvent at AuthServer");
        }
    }
}
