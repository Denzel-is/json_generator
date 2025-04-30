package com.disgroup.pipeline.json.generator.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AttributeSet {

    private final Map<String, Object> attrs = new LinkedHashMap<>();

    public AttributeSet set(String key, Object val) {
        attrs.put(key, val);
        return this;
    }

    @JsonAnyGetter
    public Map<String, Object> asMap() { return attrs; }
}
