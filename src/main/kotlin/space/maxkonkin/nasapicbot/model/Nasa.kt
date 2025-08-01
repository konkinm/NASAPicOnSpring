package space.maxkonkin.nasapicbot.model

import com.fasterxml.jackson.annotation.JsonProperty
import tech.ydb.table.result.ResultSetReader
import java.time.LocalDate

data class Nasa(
    @JsonProperty("lang")
    var langCode: LangCode?,

    @JsonProperty("credit")
    var credit: String?,

    @JsonProperty("copyright")
    var copyright: String?,

    @JsonProperty("date")
    var date: LocalDate?,

    @JsonProperty("explanation")
    var explanation: String?,

    @JsonProperty("hdurl")
    var hdUrl: String?,

    @JsonProperty("media_type")
    var mediaType: String?,

    @JsonProperty("service_version")
    var serviceVersion: String?,

    @JsonProperty("title")
    var title: String?,

    @JsonProperty("url")
    var url: String?
) {
    companion object {
        fun fromResultSet(resultSet: ResultSetReader): Nasa {
            val date = resultSet.getColumn("date").date
            val langCode = LangCode.valueOf(resultSet.getColumn("lang").text.uppercase())
            val credit = resultSet.getColumn("credit").text
            val copyright = resultSet.getColumn("copyright").text
            val explanation = resultSet.getColumn("explanation").text
            val title = resultSet.getColumn("title").text
            val url = resultSet.getColumn("url").text
            val hdUrl = resultSet.getColumn("hd_url").text
            val mediaType = resultSet.getColumn("media_type").text
            return Nasa(
                langCode, credit, copyright, date, explanation, hdUrl, mediaType, "v1", title, url
            )
        }
    }
}
