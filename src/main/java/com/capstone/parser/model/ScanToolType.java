package com.capstone.parser.model;

/**
 * The type of scan tool that generated the alert (code-scan, dependabot, secret-scan).
 */
public enum ScanToolType {
    CODE_SCAN,
    DEPENDABOT,
    SECRET_SCAN
}
