package space.maxkonkin.nasapicbot.repository

import space.maxkonkin.nasapicbot.model.LangCode
import space.maxkonkin.nasapicbot.model.Nasa
import space.maxkonkin.nasapicbot.util.ThrowingConsumer
import tech.ydb.table.query.DataQueryResult
import tech.ydb.table.query.Params
import tech.ydb.table.values.PrimitiveValue
import java.time.LocalDate
import java.util.Optional

class NasaRowTableRepository(private val tableName: String) {
    private val entityManager: EntityManager =
        EntityManager(System.getenv("DATABASE"), System.getenv("ENDPOINT"))

    fun getAll(): List<Nasa> {
        val nasaList: MutableList<Nasa> = ArrayList()
        entityManager.execute("SELECT * FROM $tableName", Params.empty(),
            ThrowingConsumer.unchecked<DataQueryResult, RuntimeException> { result ->
                val resultSet = result.getResultSet(0)
                while (resultSet.next()) {
                    nasaList.add(Nasa.fromResultSet(resultSet))
                }
            })
        return nasaList
    }

    fun getByDateAndLang(date: LocalDate, langCode: LangCode): Optional<Nasa> {
        val nasaList: MutableList<Nasa> = ArrayList()
        entityManager.execute("DECLARE \$localDate AS Date;" +
                "DECLARE \$langCode AS Utf8;" +
                "SELECT date, lang, credit, copyright, explanation, title, url, hd_url, media_type " +
                "FROM " + tableName +
                " WHERE date = \$localDate AND lang = \$langCode",
            Params.of(
                "\$localDate", PrimitiveValue.newDate(date),
                "\$langCode", PrimitiveValue.newText(langCode.code)
            ),
            ThrowingConsumer.unchecked<DataQueryResult, RuntimeException> { result ->
                val resultSet = result.getResultSet(0)
                while (resultSet.next()) {
                    nasaList.add(Nasa.fromResultSet(resultSet))
                }
            })
        return if (nasaList.isNotEmpty()) Optional.of(nasaList.first()) else Optional.empty()
    }

    fun save(nasa: Nasa) {
        val query = "DECLARE \$localDate AS Date;" +
                "DECLARE \$lang AS Utf8;" +
                "DECLARE \$credit AS Utf8;" +
                "DECLARE \$copyright AS Utf8;" +
                "DECLARE \$explanation AS Utf8;" +
                "DECLARE \$title AS Utf8;" +
                "DECLARE \$url AS Utf8;" +
                "DECLARE \$hd_url AS Utf8;" +
                "DECLARE \$media_type AS Utf8;" +
                "INSERT INTO " + tableName + " (date, lang, credit, copyright, explanation, title, url, " +
                "hd_url, media_type)" +
                "VALUES (\$localDate, \$lang, \$credit, \$copyright, \$explanation, \$title, \$url, \$hd_url, " +
                "\$media_type)"
        val credit = nasa.credit ?: ""
        val copyright = nasa.copyright ?: ""
        val explanation = nasa.explanation ?: ""
        val title = nasa.title ?: ""
        val hdUrl = nasa.hdUrl ?: ""
        val params = Params.of(
            "\$localDate", PrimitiveValue.newDate(nasa.date),
            "\$lang", PrimitiveValue.newText(nasa.langCode?.code),
            "\$credit", PrimitiveValue.newText(credit),
            "\$copyright", PrimitiveValue.newText(copyright),
            "\$explanation", PrimitiveValue.newText(explanation),
            "\$title", PrimitiveValue.newText(title),
            "\$url", PrimitiveValue.newText(nasa.url),
            "\$hd_url", PrimitiveValue.newText(hdUrl),
            "\$media_type", PrimitiveValue.newText(nasa.mediaType)
        )
        entityManager.execute(query, params)
    }

    fun update(nasa: Nasa) {
        val query = "DECLARE \$localDate AS Date;" +
                "DECLARE \$lang AS Utf8;" +
                "DECLARE \$credit AS Utf8;" +
                "DECLARE \$copyright AS Utf8;" +
                "DECLARE \$explanation AS Utf8;" +
                "DECLARE \$title AS Utf8;" +
                "DECLARE \$url AS Utf8;" +
                "DECLARE \$hd_url AS Utf8;" +
                "DECLARE \$media_type AS Utf8;" +
                "UPSERT INTO " + tableName + " (date, lang, credit, copyright, explanation, title, url, " +
                "hd_url, media_type, service_version)" +
                "VALUES (\$localDate, \$lang, \$credit, \$copyright, \$explanation, \$title, \$url, \$hd_url, " +
                "\$media_type)"
        val params = Params.of(
            "\$localDate", PrimitiveValue.newDate(nasa.date),
            "\$lang", PrimitiveValue.newText(nasa.langCode?.code),
            "\$credit", PrimitiveValue.newText(nasa.credit),
            "\$copyright", PrimitiveValue.newText(nasa.copyright),
            "\$explanation", PrimitiveValue.newText(nasa.explanation),
            "\$title", PrimitiveValue.newText(nasa.title),
            "\$url", PrimitiveValue.newText(nasa.url),
            "\$hd_url", PrimitiveValue.newText(nasa.hdUrl),
            "\$media_type", PrimitiveValue.newText(nasa.mediaType)
        )
        entityManager.execute(query, params)
    }

    fun deleteByDate(date: LocalDate) {
        entityManager.execute(
            "DECLARE \$localDate AS Date;" +
                    "DELETE FROM " + tableName + " WHERE date = \$localDate",
            Params.of("\$localDate", PrimitiveValue.newDate(date))
        )
    }
}
