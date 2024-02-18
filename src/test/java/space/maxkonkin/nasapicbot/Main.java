package space.maxkonkin.nasapicbot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import space.maxkonkin.nasapicbot.web.Handler;

public class Main {
    public static void main(String[] args) throws JsonProcessingException {
        final String UPDATE = """
                {"update_id":0,
                    "message":{
                        "message_id":0,
                        "from":{"id":0,"is_bot":false,"first_name":"name","username":"username","language_code":"en"},
                        "chat":{"id":0,"first_name":"name","username":"username","type":"private"},
                        "date":1700000000,
                        "text":"/start",
                        "entities":[{"offset":0,"length":6,"type":"bot_command"}]
                        }
                }
                """;
        ObjectMapper mapper = new ObjectMapper();
        Handler handler = new Handler();
        SendMessage message = handler.apply(mapper.readValue(UPDATE, Update.class));
        System.out.println(mapper.writeValueAsString(message));
    }
}
