package com.capstone.parser.service.deduplicator;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capstone.parser.dto.DeduplicatorResponse;
import com.capstone.parser.model.Finding;
import com.capstone.parser.service.hashing.SHA256HashGenerator;

@Service
public class CommonToolDeduplicatorService implements ToolDeduplicatorService {

    @Autowired
    public SHA256HashGenerator hashGenerator;

    public CommonToolDeduplicatorService(SHA256HashGenerator hashGenerator) {
        this.hashGenerator = hashGenerator;
    }
 
    @Override
    public DeduplicatorResponse checkDuplication(Finding newFinding, List<Finding> findings) {
        Boolean canSave;

        for (Finding oldFinding : findings) {
            if (isCompositeKeyHashSame(newFinding, oldFinding)) {
                canSave = !isHashSame(newFinding, oldFinding);
                return new DeduplicatorResponse(canSave, false, oldFinding);
            }
        }

        return new DeduplicatorResponse(true, true, null);
    }

    @Override
    public Boolean isHashSame(Finding finding1, Finding finding2) {

        List<String> dynamicFields1 = Arrays.asList(
            finding1.getSeverity().toString(),
            finding1.getState().toString()
        );

        List<String> dynamicFields2 = Arrays.asList(
            finding2.getSeverity().toString(),
            finding2.getState().toString()
        );

        String hash1 = hashGenerator.generateHash(dynamicFields1);
        String hash2 = hashGenerator.generateHash(dynamicFields2);
        return (hash1 == null ? hash2 == null : hash1.equals(hash2));

    }

    @Override
    public Boolean isCompositeKeyHashSame(Finding finding1, Finding finding2) {
        String alertNumber1 = finding1.getToolAdditionalProperties().get("number").toString();      //FISHY
        String alertNumber2 = finding2.getToolAdditionalProperties().get("number").toString();      //FISHY
        List<String> compositeKeyFields1 = Arrays.asList(alertNumber1, finding1.getTitle());
        List<String> compositeKeyFields2 = Arrays.asList(alertNumber2, finding2.getTitle());

        String hash1 = hashGenerator.generateHash(compositeKeyFields1);
        String hash2 = hashGenerator.generateHash(compositeKeyFields2);
        return (hash1 == null ? hash2 == null : hash1.equals(hash2));

    }


}
