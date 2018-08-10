package models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "status",
        "metadata",
        "cost_time",
        "result_type"
})
public class acrResponse {

    @JsonProperty("status")
    private Status status;
    @JsonProperty("metadata")
    private Metadata metadata;
    @JsonProperty("cost_time")
    private Double costTime;
    @JsonProperty("result_type")
    private Integer resultType;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("status")
    public Status getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(Status status) {
        this.status = status;
    }

    @JsonProperty("metadata")
    public Metadata getMetadata() {
        return metadata;
    }

    @JsonProperty("metadata")
    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    @JsonProperty("cost_time")
    public Double getCostTime() {
        return costTime;
    }

    @JsonProperty("cost_time")
    public void setCostTime(Double costTime) {
        this.costTime = costTime;
    }

    @JsonProperty("result_type")
    public Integer getResultType() {
        return resultType;
    }

    @JsonProperty("result_type")
    public void setResultType(Integer resultType) {
        this.resultType = resultType;
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

