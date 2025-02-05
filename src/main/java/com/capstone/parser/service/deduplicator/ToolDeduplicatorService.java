package com.capstone.parser.service.deduplicator;

import java.util.List;

import com.capstone.parser.dto.DeduplicatorResponse;
import com.capstone.parser.model.Finding;

public interface ToolDeduplicatorService {

    public Boolean isCompositeKeyHashSame(Finding finding1, Finding finding2);
    
    public Boolean isHashSame(Finding finding1, Finding finding2);

    public DeduplicatorResponse checkDuplication(Finding newFinding, List<Finding> toCompareFindingsList);
    
}
