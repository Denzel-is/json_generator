package com.disgroup.pipeline.json.generator.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RecordItem {

    @JsonProperty("externalId")
    private String externalId;

    @JsonProperty("attributes")
    private AttributeSet attributes;

    public RecordItem() {}

    public RecordItem(String externalId, AttributeSet attributes) {
        this.externalId = externalId;
        this.attributes = attributes;
    }

    public String       getExternalId() { return externalId; }
    public AttributeSet getAttributes() { return attributes; }

    public void setExternalId(String externalId)   { this.externalId = externalId; }
    public void setAttributes(AttributeSet attrs)  { this.attributes = attrs; }
}
