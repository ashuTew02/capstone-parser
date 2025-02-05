package com.capstone.parser.dto;

import com.capstone.parser.model.Finding;

public class DeduplicatorResponse {
    private Boolean canSave;
    private Boolean isNewEntry;
    private Finding oldFinding;
    public Boolean getCanSave() {
        return canSave;
    }
    public DeduplicatorResponse(Boolean canSave, Boolean isNewEntry, Finding oldFinding) {
        this.canSave = canSave;
        this.isNewEntry = isNewEntry;
        this.oldFinding = oldFinding;
    }
    public void setCanSave(Boolean canSave) {
        this.canSave = canSave;
    }
    public Boolean getIsNewEntry() {
        return isNewEntry;
    }
    public void setIsNewEntry(Boolean isNewEntry) {
        this.isNewEntry = isNewEntry;
    }

    public Finding getOldFinding() {
        return oldFinding;
    }

    public void setOldFinding(Finding oldFinding) {
        this.oldFinding = oldFinding;
    }

}
