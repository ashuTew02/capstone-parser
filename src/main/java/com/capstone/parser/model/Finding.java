package com.capstone.parser.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Our common schema for all findings, regardless of the tool.
 */
public class Finding {

    // Unique ID for the document in Elasticsearch
    private String id;

    // Title of the finding
    private String title;

    // Description or details
    private String desc;

    // Mapped internal severity
    private FindingSeverity severity;

    // Mapped internal state
    private FindingState state;

    // Timestamps
    private Instant createdAt;
    private Instant updatedAt;

    // URL to the external alert (e.g., GitHub alert URL)
    private String url;

    // Which scanning tool or type generated this (code scan, dependabot, secret, etc.)
    private ScanToolType toolType;

    // CVE if present
    private String cve;

    // List of CWEs, if any
    private List<String> cwes;

    // CVSS score or vector
    private String cvss;

    // For example, "js/template-object-injection" or "github_personal_access_token"
    private String type;

    // Suggestions or recommendations
    private String suggestions;

    // Path in the repo or manifest file where the problem is found
    private String filePath;

    // For library vulnerabilities
    private String componentName;
    private String componentVersion;

    // All other tool-specific properties
    private Map<String, Object> toolAdditionalProperties;

    // Getters and setters ...
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }

    public FindingSeverity getSeverity() {
        return severity;
    }
    public void setSeverity(FindingSeverity severity) {
        this.severity = severity;
    }

    public FindingState getState() {
        return state;
    }
    public void setState(FindingState state) {
        this.state = state;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public ScanToolType getToolType() {
        return toolType;
    }
    public void setToolType(ScanToolType toolType) {
        this.toolType = toolType;
    }

    public String getCve() {
        return cve;
    }
    public void setCve(String cve) {
        this.cve = cve;
    }

    public List<String> getCwes() {
        return cwes;
    }
    public void setCwes(List<String> cwes) {
        this.cwes = cwes;
    }

    public String getCvss() {
        return cvss;
    }
    public void setCvss(String cvss) {
        this.cvss = cvss;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getSuggestions() {
        return suggestions;
    }
    public void setSuggestions(String suggestions) {
        this.suggestions = suggestions;
    }

    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getComponentName() {
        return componentName;
    }
    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public String getComponentVersion() {
        return componentVersion;
    }
    public void setComponentVersion(String componentVersion) {
        this.componentVersion = componentVersion;
    }

    public Map<String, Object> getToolAdditionalProperties() {
        return toolAdditionalProperties;
    }
    public void setToolAdditionalProperties(Map<String, Object> toolAdditionalProperties) {
        this.toolAdditionalProperties = toolAdditionalProperties;
    }
}
