package com.capstone.parser.model;

public enum KafkaTopic {
    AUTHSERVER_JFC("authserver_jfc"),
    TOOLSCHEDULER_JFC("toolscheduler_jfc"),
    PARSER_JFC("parser_jfc"),
    ACK_JOB("ack_job");

    private final String topicName;

    KafkaTopic(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicName() {
        return topicName;
    }
}
