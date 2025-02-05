package com.capstone.parser.service.github.mapper;

import org.springframework.stereotype.Service;

import com.capstone.parser.model.FindingState;

@Service
public class GitHubSecretScanMapper implements GitHubStateToFindingStateMapper {

    @Override
    public FindingState toFindingState(String state, String resolution) {
        // Secret scanning: "open" or "resolved"
        if (state == null || state.equalsIgnoreCase("open")) {
            return FindingState.OPEN;
        }

        if (state.equalsIgnoreCase("resolved")) {
            if (resolution != null) {
                String rr = resolution.toLowerCase();
                switch (rr) {
                    case "false_positive":
                        return FindingState.FALSE_POSITIVE;
                    case "wont_fix":
                        return FindingState.SUPPRESSED;
                    case "revoked":
                        return FindingState.FIXED;
                    case "used_in_tests":
                        // maybe also => FIXED, or SUPPRESSED, your choice
                        return FindingState.FIXED;
                }
            }
            // fallback if unknown
            return FindingState.SUPPRESSED;
        }

        return FindingState.OPEN;
    }
}
