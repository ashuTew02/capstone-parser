package com.capstone.parser.service.github.mapper;

import org.springframework.stereotype.Service;
import com.capstone.parser.model.FindingState;

@Service
public class GitHubCodeScanMapper implements GitHubStateToFindingStateMapper {

    @Override
    public FindingState toFindingState(String state, String dismissedReason) {
        // Code scanning: state => "open" or "dismissed"
        if (state == null || state.equalsIgnoreCase("open")) {
            return FindingState.OPEN;
        }

        // If "dismissed", look at reason:
        if (state.equalsIgnoreCase("dismissed")) {
            if (dismissedReason != null) {
                String dr = dismissedReason.toLowerCase();
                if (dr.equals("false positive")) {
                    return FindingState.FALSE_POSITIVE;
                } else if (dr.equals("won't fix")) {
                    return FindingState.SUPPRESSED;
                } else if (dr.equals("used in tests")) {
                    return FindingState.FIXED;
                }
            }
            // fallback if reason is unknown
            return FindingState.SUPPRESSED;
        }

        // unrecognized => default to OPEN
        return FindingState.OPEN;
    }
}
