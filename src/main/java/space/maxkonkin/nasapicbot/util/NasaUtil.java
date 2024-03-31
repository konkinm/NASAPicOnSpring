package space.maxkonkin.nasapicbot.util;

import lombok.experimental.UtilityClass;
import space.maxkonkin.nasapicbot.model.LangCode;
import space.maxkonkin.nasapicbot.model.Nasa;
import space.maxkonkin.nasapicbot.to.NasaTo;

@UtilityClass
public class NasaUtil {
    public static Nasa fromTo(NasaTo to, LangCode langCode) {
        return new Nasa(langCode, to.credit(), to.copyright(), to.date(), to.explanation(), to.hdUrl(),
                to.mediaType(), to.serviceVersion(), to.title(), to.url());
    }

    public static NasaTo getTo(Nasa nasa) {
        return new NasaTo(nasa.getCredit(), nasa.getCopyright(), nasa.getDate(), nasa.getExplanation(), nasa.getHdUrl(),
                nasa.getMediaType(), nasa.getServiceVersion(), nasa.getTitle(), nasa.getUrl());
    }

    public static String getFormattedMessage(NasaTo nasaTo) {
        String url = nasaTo.url();
        String hdUrl = nasaTo.hdUrl();
        StringBuilder message = new StringBuilder();
        message.append("<a href=\"")
                .append(url)
                .append("\">")
                .append("<b>")
                .append(nasaTo.title())
                .append("</b>")
                .append("</a>");
        if (!url.equalsIgnoreCase(hdUrl)) {
            message.append(" | <a href=\"")
                    .append(hdUrl)
                    .append("\">")
                    .append("HD")
                    .append("</a>");
        }
        message.append("\n(Posted on ")
                .append(nasaTo.date())
                .append(")\n\n")
                .append(nasaTo.explanation());
        return message.toString();
    }
}
