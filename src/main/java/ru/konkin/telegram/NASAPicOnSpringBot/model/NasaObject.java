package ru.konkin.telegram.NASAPicOnSpringBot.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NasaObject(String credit, String copyright, String date, String explanation, String hdUrl,
                         String mediaType, String serviceVersion, String title, String url) {
    public NasaObject(
            @JsonProperty("credit") String credit,
            @JsonProperty("copyright") String copyright,
            @JsonProperty("date") String date,
            @JsonProperty("explanation") String explanation,
            @JsonProperty("hdurl") String hdUrl,
            @JsonProperty("media_type") String mediaType,
            @JsonProperty("service_version") String serviceVersion,
            @JsonProperty("title") String title,
            @JsonProperty("url") String url) {
        this.credit = credit;
        this.copyright = copyright;
        this.date = date;
        this.explanation = explanation;
        this.hdUrl = hdUrl;
        this.mediaType = mediaType;
        this.serviceVersion = serviceVersion;
        this.title = title;
        this.url = url;
    }

    @Override
    public String toString() {
        return "NasaObject{" +
                "credit='" + credit + '\'' +
                ", copyright='" + copyright + '\'' +
                ", date='" + date + '\'' +
                ", explanation='" + explanation + '\'' +
                ", hdurl='" + hdUrl + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", serviceVersion='" + serviceVersion + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}