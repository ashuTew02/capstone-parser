package com.capstone.parser.service.processor;

/**
 * Generic interface for processing a JSON scan file.
 */
public interface ScanJobProcessorService {

    /**
     * Process the scan results from the provided JSON file path.
     *
     * @param filePath The path to the JSON file containing tool-specific alerts.
     * @param esIndexOfFindings The Elasticsearch index to save findings to.
     * @throws Exception if reading or parsing fails
     */
    void processJob(String filePath, String esIndexOfFindings) throws Exception;
}
