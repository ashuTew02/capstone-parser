package com.capstone.parser.model;

/**
 * Our internal states for all findings.
 */
public enum FindingState {
    OPEN,
    FALSE_POSITIVE,
    SUPPRESSED,
    FIXED,
    CONFIRMED
}
