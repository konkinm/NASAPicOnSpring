package space.maxkonkin.nasapicbot.model

import com.fasterxml.jackson.annotation.JsonProperty
import tech.ydb.table.result.ResultSetReader
import java.time.LocalDate

data class Nasa(
    @JsonProperty("lang")
    val langCode: LangCode?,

    @JsonProperty("credit")
    val credit: String?,

    @JsonProperty("copyright")
    val copyright: String?,

    @JsonProperty("date")
    val date: LocalDate?,

    @JsonProperty("explanation")
    val explanation: String?,

    @JsonProperty("hdurl")
    val hdUrl: String?,

    @JsonProperty("media_type")
    val mediaType: String?,

    @JsonProperty("service_version")
    val serviceVersion: String?,

    @JsonProperty("title")
    val title: String?,

    @JsonProperty("url")
    val url: String?
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
