package com.capstone.parser.service.github.mapper;

import com.capstone.parser.model.FindingState;

public interface GitHubStateToFindingStateMapper {
        /**
     * Convert raw strings from GitHub's "state" and optional "dismissed_reason"
     * (or "resolution") back to your FindingState.
     *
     * @param state  the GitHub state (e.g. "open", "dismissed", "fixed", "resolved", etc.)
     * @param reason the GitHub dismissed reason or resolution (may be null or empty)
     * @return the mapped FindingState
     */
    FindingState toFindingState(String state, String reason);
}
