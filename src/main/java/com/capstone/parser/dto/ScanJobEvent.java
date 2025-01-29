package com.capstone.parser.dto;

/**
 * Represents the message from the Kafka topic:
 * {
 *   "type": "code-scan" | "dependabot" | "secret-scan",
 *   "scanFilePath": "<file path where JSON is located>"
 * }
 */
public class ScanJobEvent {

    // e.g. "code-scan", "dependabot", "secret-scan"
    private String type;

    // path to JSON file containing alerts
    private String scanFilePath;

    // Getters and Setters
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getScanFilePath() {
        return scanFilePath;
    }
    public void setScanFilePath(String scanFilePath) {
        this.scanFilePath = scanFilePath;
    }
}
