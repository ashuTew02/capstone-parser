package com.capstone.parser.dto.event.payload.ticket;

import com.capstone.parser.model.KafkaTopic;
import com.capstone.parser.model.Tool;

public class TicketUpdateStatusEventPayload {
    String findingId;
    Long tenantId;
    KafkaTopic destTopic;
    Tool tool;
    String status;

    public TicketUpdateStatusEventPayload(String findingId, Long tenantId, KafkaTopic destTopic, Tool tool,
            String status) {
        this.findingId = findingId;
        this.tenantId = tenantId;
        this.destTopic = destTopic;
        this.tool = tool;
        this.status = status;
    }

    public TicketUpdateStatusEventPayload(String findingId, Long tenantId, KafkaTopic destTopic, Tool tool) {
        this.findingId = findingId;
        this.tenantId = tenantId;
        this.destTopic = destTopic;
        this.tool = tool;
        this.status = "DONE";
    }

    public TicketUpdateStatusEventPayload() {
    }

    public String getFindingId() {
        return findingId;
    }
    public void setFindingId(String findingId) {
        this.findingId = findingId;
    }
    public Long getTenantId() {
        return tenantId;
    }
    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
    public KafkaTopic getDestTopic() {
        return destTopic;
    }
    public void setDestTopic(KafkaTopic destTopic) {
        this.destTopic = destTopic;
    }
    public Tool getTool() {
        return tool;
    }
    public void setTool(Tool tool) {
        this.tool = tool;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    
}
