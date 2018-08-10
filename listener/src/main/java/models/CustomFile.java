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
        "show",
        "play_offset_ms",
        "EPISODE",
        "bucket_id",
        "title",
        "duration_ms",
        "acrid",
        "THUMB",
        "score"
})
public class CustomFile {

    @JsonProperty("show")
    private String show;
    @JsonProperty("play_offset_ms")
    private Integer playOffsetMs;
    @JsonProperty("EPISODE")
    private String ePISODE;
    @JsonProperty("bucket_id")
    private String bucketId;
    @JsonProperty("channel_id")
    private String channelId;
    @JsonProperty("title")
    private String title;
    @JsonProperty("duration_ms")
    private String durationMs;
    @JsonProperty("acrid")
    private String acrid;
    @JsonProperty("THUMB")
    private String tHUMB;
    @JsonProperty("score")
    private Integer score;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("show")
    public String getShow() {
        return show;
    }

    @JsonProperty("show")
    public void setShow(String show) {
        this.show = show;
    }

    @JsonProperty("play_offset_ms")
    public Integer getPlayOffsetMs() {
        return playOffsetMs;
    }

    @JsonProperty("play_offset_ms")
    public void setPlayOffsetMs(Integer playOffsetMs) {
        this.playOffsetMs = playOffsetMs;
    }

    @JsonProperty("EPISODE")
    public String getEPISODE() {
        return ePISODE;
    }

    @JsonProperty("EPISODE")
    public void setEPISODE(String ePISODE) {
        this.ePISODE = ePISODE;
    }

    @JsonProperty("bucket_id")
    public String getBucketId() {
        return bucketId;
    }

    @JsonProperty("bucket_id")
    public void setBucketId(String bucketId) {
        this.bucketId = bucketId;
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

    @JsonProperty("duration_ms")
    public String getDurationMs() {
        return durationMs;
    }

    @JsonProperty("duration_ms")
    public void setDurationMs(String durationMs) {
        this.durationMs = durationMs;
    }

    @JsonProperty("acrid")
    public String getAcrid() {
        return acrid;
    }

    @JsonProperty("acrid")
    public void setAcrid(String acrid) {
        this.acrid = acrid;
    }

    @JsonProperty("THUMB")
    public String getTHUMB() {
        return tHUMB;
    }

    @JsonProperty("THUMB")
    public void setTHUMB(String tHUMB) {
        this.tHUMB = tHUMB;
    }

    @JsonProperty("score")
    public Integer getScore() {
        return score;
    }

    @JsonProperty("score")
    public void setScore(Integer score) {
        this.score = score;
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
