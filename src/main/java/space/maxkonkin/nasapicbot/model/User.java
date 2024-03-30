package space.maxkonkin.nasapicbot.model;

import tech.ydb.table.result.ResultSetReader;

public class User {
    private Long chatId;
    private String name;
    private Boolean isScheduled;
    private LangCode translateLangCode;

    public User(Long chatId, String name, boolean isScheduled, LangCode translateLangCode) {
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

    public Long getChatId() {
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

    public LangCode getTranslateLangCode() {
        return translateLangCode;
    }

    public void setTranslateLangCode(LangCode translateLangCode) {
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
        var translateLangCode = LangCode.valueOf(resultSet.getColumn("translate_lang_code").getText().toUpperCase());
        return new User(chatId, name, isScheduled, translateLangCode);
    }
}
