package space.maxkonkin.nasapicbot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tech.ydb.table.result.ResultSetReader;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class Nasa {
    @JsonProperty("lang")
    private LangCode langCode;

    @JsonProperty("credit")
    private String credit;

    @JsonProperty("copyright")
    private String copyright;

    @JsonProperty("date")
    private LocalDate date;

    @JsonProperty("explanation")
    private String explanation;

    @JsonProperty("hdurl")
    private String hdUrl;

    @JsonProperty("media_type")
    private String mediaType;

    @JsonProperty("service_version")
    private String serviceVersion;

    @JsonProperty("title")
    private String title;

    @JsonProperty("url")
    private String url;

    public Nasa(LangCode langCode, String credit, String copyright, LocalDate date, String explanation,
                String hdUrl, String mediaType, String serviceVersion, String title, String url) {
        this.langCode = langCode;
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

    public static Nasa fromResultSet(ResultSetReader resultSet) {
        var date = resultSet.getColumn("date").getDate();
        var langCode = LangCode.valueOf(resultSet.getColumn("lang").getText().toUpperCase());
        var credit = resultSet.getColumn("credit").getText();
        var copyright = resultSet.getColumn("copyright").getText();
        var explanation = resultSet.getColumn("explanation").getText();
        var title = resultSet.getColumn("title").getText();
        var url = resultSet.getColumn("url").getText();
        var hdUrl = resultSet.getColumn("hd_url").getText();
        var mediaType = resultSet.getColumn("media_type").getText();
        return new Nasa(langCode, credit, copyright, date, explanation, hdUrl, mediaType, "v1", title, url);
    }

    @Override
    public String toString() {
        return "Nasa{" +
                "langCode=" + langCode.getCode() +
                ", credit='" + credit + '\'' +
                ", copyright='" + copyright + '\'' +
                ", date='" + date + '\'' +
                ", explanation='" + explanation + '\'' +
                ", hdUrl='" + hdUrl + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", serviceVersion='" + serviceVersion + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
