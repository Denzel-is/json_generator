package com.disgroup.pipeline.json.generator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public class RecordsWrapper {

    @JsonProperty("records")
    private final Map<String, List<RecordItem>> records;

    @JsonProperty("resolveResult")
    private final boolean resolveResult = true;

    public RecordsWrapper(String registerName, List<RecordItem> items) {
        this.records = Map.of(registerName, items);
    }

    public Map<String, List<RecordItem>> getRecords() { return records; }
    public boolean isResolveResult()                  { return resolveResult; }
}
