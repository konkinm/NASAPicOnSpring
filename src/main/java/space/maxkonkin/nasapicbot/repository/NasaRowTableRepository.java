package space.maxkonkin.nasapicbot.repository;

import org.springframework.stereotype.Repository;
import space.maxkonkin.nasapicbot.model.LangCode;
import space.maxkonkin.nasapicbot.model.Nasa;
import space.maxkonkin.nasapicbot.util.ThrowingConsumer;
import tech.ydb.table.query.Params;
import tech.ydb.table.result.ResultSetReader;
import tech.ydb.table.values.PrimitiveValue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class NasaRowTableRepository {
    private final EntityManager entityManager = new EntityManager(System.getenv("DATABASE"), System.getenv("ENDPOINT"));

    public List<Nasa> getAll() {
        List<Nasa> nasaList = new ArrayList<>();
        entityManager.execute("SELECT * FROM nasapic_row_storage", Params.empty(), ThrowingConsumer.unchecked(result -> {
            ResultSetReader resultSet = result.getResultSet(0);
            while (resultSet.next()) {
                nasaList.add(Nasa.fromResultSet(resultSet));
            }
        }));
        return nasaList;
    }

    public Optional<Nasa> getByDateAndLang(LocalDate date, LangCode langCode) {
        List<Nasa> nasaList = new ArrayList<>();
        entityManager.execute("DECLARE $localDate AS Date;" +
                        "DECLARE $langCode AS Utf8;" +
                        "SELECT date, lang, credit, copyright, explanation, title, url, hd_url, media_type " +
                        "FROM nasapic_row_storage " +
                        "WHERE date = $localDate AND lang = $langCode",
                Params.of("$localDate", PrimitiveValue.newDate(date),
                        "$langCode", PrimitiveValue.newText(langCode.getCode())),
                ThrowingConsumer.unchecked(result -> {
                    ResultSetReader resultSet = result.getResultSet(0);
                    while (resultSet.next()) {
                        nasaList.add(Nasa.fromResultSet(resultSet));
                    }
                }));
        return !nasaList.isEmpty() ? Optional.of(nasaList.getFirst()) : Optional.empty();
    }

    public void save(Nasa nasa) {
        String query = "DECLARE $localDate AS Date;" +
                "DECLARE $lang AS Utf8;" +
                "DECLARE $credit AS Utf8;" +
                "DECLARE $copyright AS Utf8;" +
                "DECLARE $explanation AS Utf8;" +
                "DECLARE $title AS Utf8;" +
                "DECLARE $url AS Utf8;" +
                "DECLARE $hd_url AS Utf8;" +
                "DECLARE $media_type AS Utf8;" +
                "INSERT INTO nasapic_row_storage (date, lang, credit, copyright, explanation, title, url, " +
                "hd_url, media_type)" +
                "VALUES ($localDate, $lang, $credit, $copyright, $explanation, $title, $url, $hd_url, " +
                "$media_type)";
        String credit = nasa.getCredit() != null ? nasa.getCredit() : "";
        String copyright = nasa.getCopyright() != null ? nasa.getCopyright() : "";
        String explanation = nasa.getExplanation() != null ? nasa.getExplanation() : "";
        String title = nasa.getTitle() != null ? nasa.getTitle() : "";
        String hd_url = nasa.getHdUrl() != null ? nasa.getHdUrl() : "";
        Params params = Params.of("$localDate", PrimitiveValue.newDate(nasa.getDate()),
                "$lang", PrimitiveValue.newText(nasa.getLangCode().getCode()),
                "$credit",PrimitiveValue.newText(credit),
                "$copyright", PrimitiveValue.newText(copyright),
                "$explanation", PrimitiveValue.newText(explanation),
                "$title", PrimitiveValue.newText(title),
                "$url", PrimitiveValue.newText(nasa.getUrl()),
                "$hd_url", PrimitiveValue.newText(hd_url),
                "$media_type", PrimitiveValue.newText(nasa.getMediaType()));
        entityManager.execute(query, params);
    }

    public void update(Nasa nasa) {
        String query = "DECLARE $localDate AS Date;" +
                "DECLARE $lang AS Utf8;" +
                "DECLARE $credit AS Utf8;" +
                "DECLARE $copyright AS Utf8;" +
                "DECLARE $explanation AS Utf8;" +
                "DECLARE $title AS Utf8;" +
                "DECLARE $url AS Utf8;" +
                "DECLARE $hd_url AS Utf8;" +
                "DECLARE $media_type AS Utf8;" +
                "UPSERT INTO nasapic_row_storage (date, lang, credit, copyright, explanation, title, url, " +
                "hd_url, media_type, service_version)" +
                "VALUES ($localDate, $lang, $credit, $copyright, $explanation, $title, $url, $hd_url, " +
                "$media_type)";
        Params params = Params.of("$localDate", PrimitiveValue.newDate(nasa.getDate()),
                "$lang", PrimitiveValue.newText(nasa.getLangCode().getCode()),
                "$credit",PrimitiveValue.newText(nasa.getCredit()),
                "$copyright", PrimitiveValue.newText(nasa.getCopyright()),
                "$explanation", PrimitiveValue.newText(nasa.getExplanation()),
                "$title", PrimitiveValue.newText(nasa.getTitle()),
                "$url", PrimitiveValue.newText(nasa.getUrl()),
                "$hd_url", PrimitiveValue.newText(nasa.getHdUrl()),
                "$media_type", PrimitiveValue.newText(nasa.getMediaType()));
        entityManager.execute(query, params);
    }

    public void deleteByDate(LocalDate date) {
        entityManager.execute("DECLARE $localDate AS Date;" +
                        "DELETE FROM nasapic_row_storage WHERE date = $localDate",
                Params.of("$localDate", PrimitiveValue.newDate(date)));
    }
}
