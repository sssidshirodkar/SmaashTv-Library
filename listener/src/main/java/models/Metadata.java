package models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "timestamp_utc",
        "custom_files"
})
public class Metadata {

    @JsonProperty("timestamp_utc")
    private String timestampUtc;
    @JsonProperty("custom_files")
    private List<CustomFile> customFiles = null;
    @JsonProperty("custom_streams")
    private List<CustomStream> customStreams = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("timestamp_utc")
    public String getTimestampUtc() {
        return timestampUtc;
    }

    @JsonProperty("timestamp_utc")
    public void setTimestampUtc(String timestampUtc) {
        this.timestampUtc = timestampUtc;
    }

    @JsonProperty("custom_files")
    public List<CustomFile> getCustomFiles() {
        return customFiles;
    }

    @JsonProperty("custom_files")
    public void setCustomFiles(List<CustomFile> customFiles) {
        this.customFiles = customFiles;
    }

    @JsonProperty("custom_streams")
    public List<CustomStream> getCustomStreams() {
        return customStreams;
    }

    @JsonProperty("custom_streams")
    public void setCustomStreams(List<CustomStream> customStreams) {
        this.customStreams = customStreams;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}