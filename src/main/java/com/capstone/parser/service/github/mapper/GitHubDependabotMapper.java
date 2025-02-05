package com.capstone.parser.service.github.mapper;

import org.springframework.stereotype.Service;

import com.capstone.parser.model.FindingState;

@Service
public class GitHubDependabotMapper implements GitHubStateToFindingStateMapper {

    @Override
    public FindingState toFindingState(String state, String dismissedReason) {
        // Dependabot states: "open", "dismissed"
        if (state == null || state.equalsIgnoreCase("open")) {
            return FindingState.OPEN;
        }

        if (state.equalsIgnoreCase("dismissed")) {
            if (dismissedReason != null) {
                String dr = dismissedReason.toLowerCase();
                switch (dr) {
                    case "inaccurate":
                        return FindingState.FALSE_POSITIVE;
                    case "not_used":
                        return FindingState.SUPPRESSED;
                    case "fix_started":
                        return FindingState.FIXED;
                    // "no_bandwidth", "tolerable_risk", etc. -> pick one
                    case "no_bandwidth":
                    case "tolerable_risk":
                        return FindingState.SUPPRESSED;
                }
            }
            // fallback
            return FindingState.SUPPRESSED;
        }

        // unknown => OPEN
        return FindingState.OPEN;
    }
}
