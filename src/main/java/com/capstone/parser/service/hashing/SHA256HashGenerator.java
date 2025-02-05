package com.capstone.parser.service.hashing;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class SHA256HashGenerator implements HashGenerator {

    // @Override
    public String generateHash(String input) {
        return computeSHA256Hash(input);
    }

    // Method to hash a list of strings into a single hash
    @Override
    public String generateHash(List<String> inputs) {
        // Concatenate all strings into one
        String combinedInput = String.join("|", inputs);
        return computeSHA256Hash(combinedInput);
    }

    private String computeSHA256Hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes());

            // Convert byte array to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found!", e);
        }
    }
}
