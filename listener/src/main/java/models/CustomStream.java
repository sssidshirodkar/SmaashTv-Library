package models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "score",
        "timestamp_ms",
        "channel_id",
        "title",
        "acrid",
        "name",
        "result_type"
})
public class CustomStream {

    @JsonProperty("score")
    private Integer score;
    @JsonProperty("timestamp_ms")
    private Date timestampMs;
    @JsonProperty("channel_id")
    private String channelId;
    @JsonProperty("title")
    private String title;
    @JsonProperty("acrid")
    private String acrid;
    @JsonProperty("name")
    private String name;
    @JsonProperty("result_type")
    private String resultType;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("score")
    public Integer getScore() {
        return score;
    }

    @JsonProperty("score")
    public void setScore(Integer score) {
        this.score = score;
    }

    @JsonProperty("timestamp_ms")
    public Date getTimestampMs() {
        return timestampMs;
    }

    @JsonProperty("timestamp_ms")
    public void setTimestampMs(Date timestampMs) {
        this.timestampMs = timestampMs;
    }

    @JsonProperty("channel_id")
    public String getChannelId() {
        return channelId;
    }

    @JsonProperty("channel_id")
    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("acrid")
    public String getAcrid() {
        return acrid;
    }

    @JsonProperty("acrid")
    public void setAcrid(String acrid) {
        this.acrid = acrid;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("result_type")
    public String getResultType() {
        return resultType;
    }

    @JsonProperty("result_type")
    public void setResultType(String resultType) {
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