package space.maxkonkin.nasapicbot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import space.maxkonkin.nasapicbot.model.TimerMessage;
import space.maxkonkin.nasapicbot.web.TimerHandler;

public class TimerHandlerTest {
    public static void main(String[] args) throws JsonProcessingException {
        final String MESSAGE = """
                            {
                              "messages": [
                                {
                                  "event_metadata": {
                                    "event_id": "a1s41g2n5g0o********",
                                    "event_type": "yandex.cloud.events.serverless.triggers.TimerMessage",
                                    "created_at": "2019-12-04T12:05:14.227761Z",
                                    "cloud_id": "b1gvlrnlei4l********",
                                    "folder_id": "b1g88tflru0e********"
                                  },
                                  "details": {
                                    "trigger_id": "a1sfe084v4se********",
                                    "payload": "229590625"
                                  }
                                }
                              ]
                            }
                """;
        ObjectMapper mapper = new ObjectMapper();
        TimerHandler handler = new TimerHandler();
        String message = handler.apply(mapper.readValue(MESSAGE, TimerMessage.class));
        System.out.println("Message: " + mapper.writeValueAsString(message));
    }
}
