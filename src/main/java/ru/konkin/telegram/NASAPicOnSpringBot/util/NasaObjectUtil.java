package ru.konkin.telegram.NASAPicOnSpringBot.util;

import lombok.experimental.UtilityClass;
import ru.konkin.telegram.NASAPicOnSpringBot.model.NasaObject;

@UtilityClass
public class NasaObjectUtil {
    public static String getFormattedMessage(NasaObject nasaObject) {
        String url = nasaObject.url();
        String hdUrl = nasaObject.hdUrl();
        StringBuilder message = new StringBuilder();
        message.append("<a href=\"")
                .append(url)
                .append("\">")
                .append("<b>")
                .append(nasaObject.title())
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
                .append(nasaObject.date())
                .append(")\n\n")
                .append(nasaObject.explanation());
        return message.toString();
    }
}
