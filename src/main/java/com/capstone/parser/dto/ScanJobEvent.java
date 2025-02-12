package com.capstone.parser.dto;

public class ScanJobEvent {

    // e.g. "code-scan", "dependabot", "secret-scan"
    private String type;

    // path to JSON file containing alerts
    private String scanFilePath;

    private String esIndexOfFindings;

    public String getEsIndexOfFindings() {
        return esIndexOfFindings;
    }
    public void setEsIndexOfFindings(String esIndexOfFindings) {
        this.esIndexOfFindings = esIndexOfFindings;
    }
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
