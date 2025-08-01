package space.maxkonkin.nasapicbot.to

import com.fasterxml.jackson.annotation.JsonProperty

data class NasaTo(
    @JsonProperty("credit") val credit: String?,
    @JsonProperty("copyright") val copyright: String?,
    @JsonProperty("date") val date: String?,
    @JsonProperty("explanation") val explanation: String?,
    @JsonProperty("hdurl") val hdUrl: String?,
    @JsonProperty("media_type") val mediaType: String?,
    @JsonProperty("service_version") val serviceVersion: String?,
    @JsonProperty("title") val title: String?,
    @JsonProperty("url") val url: String?
)
