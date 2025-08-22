package space.maxkonkin.nasapicbot.util

import space.maxkonkin.nasapicbot.model.LangCode
import space.maxkonkin.nasapicbot.model.Nasa
import space.maxkonkin.nasapicbot.to.NasaTo
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun fromTo(to: NasaTo, langCode: LangCode): Nasa {
    return Nasa(
        langCode,
        to.credit,
        to.copyright,
        LocalDate.parse(requireNotNull(to.date), DateTimeFormatter.ISO_LOCAL_DATE),
        to.explanation,
        to.hdUrl,
        to.mediaType,
        to.serviceVersion,
        to.title,
        to.url
    )
}

fun getTo(nasa: Nasa): NasaTo {
    return NasaTo(
        nasa.credit,
        nasa.copyright,
        nasa.date?.format(DateTimeFormatter.ISO_LOCAL_DATE),
        nasa.explanation,
        nasa.hdUrl,
        nasa.mediaType,
        nasa.serviceVersion,
        nasa.title,
        nasa.url
    )
}

fun cloneWithReplacedUrl(to: NasaTo, newUrl: String): NasaTo {
    return NasaTo(
        to.credit,
        to.copyright,
        to.date,
        to.explanation,
        to.hdUrl,
        to.mediaType,
        to.serviceVersion,
        to.title,
        newUrl
    )
}

fun getFormattedMessage(nasaTo: NasaTo): String {
    val url = nasaTo.url
    val hdUrl = nasaTo.hdUrl
    val mediaType = nasaTo.mediaType
    val message = StringBuilder()
    message.append("<a href=\"")
        .append(url)
        .append("\">")
        .append("<b>")
        .append(nasaTo.title)
        .append("</b>")
        .append("</a>")
    if (url.equals(hdUrl, ignoreCase = true) && !mediaType.equals("video", ignoreCase = true)) {
        message.append(" | <a href=\"")
            .append(hdUrl)
            .append("\">")
            .append("HD")
            .append("</a>")
    }
    message.append("\n(Posted on ")
        .append(nasaTo.date)
        .append(")\n\n")
        .append(nasaTo.explanation)
    return message.toString()
}
