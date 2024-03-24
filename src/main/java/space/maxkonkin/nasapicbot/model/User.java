package space.maxkonkin.nasapicbot.model;

import tech.ydb.table.result.ResultSetReader;

public class User {
    private Long chatId;
    private String name;
    private Boolean isScheduled;
    private String translateLangCode;

    public User(long chatId, String name, boolean isScheduled, String translateLangCode) {
        this.name = name;
        this.chatId = chatId;
        this.isScheduled = isScheduled;
        this.translateLangCode = translateLangCode;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public boolean isScheduled() {
        return isScheduled;
    }

    public void setScheduled(boolean scheduled) {
        isScheduled = scheduled;
    }

    public String getTranslateLangCode() {
        return translateLangCode;
    }

    public void setTranslateLangCode(String translateLangCode) {
        this.translateLangCode = translateLangCode;
    }

    @Override
    public String toString() {
        return "User{" +
                "chatId=" + chatId +
                ", name='" + name + '\'' +
                ", isScheduled=" + isScheduled +
                ", translateLangCode='" + translateLangCode + '\'' +
                '}';
    }

    public static User fromResultSet(ResultSetReader resultSet) {
        var chatId = resultSet.getColumn("chat_id").getUint64();
        var name = resultSet.getColumn("name").getText();
        var isScheduled = resultSet.getColumn("is_scheduled").getBool();
        var translateLangCode = resultSet.getColumn("translate_lang_code").getText();
        return new User(chatId, name, isScheduled, translateLangCode);
    }
}
