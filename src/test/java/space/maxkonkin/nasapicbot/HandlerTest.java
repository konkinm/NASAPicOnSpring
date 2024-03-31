package space.maxkonkin.nasapicbot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import space.maxkonkin.nasapicbot.model.QueueMessage;
import space.maxkonkin.nasapicbot.web.Handler;

import java.io.IOException;

@Slf4j
public class HandlerTest {
    public static void main(String[] args) throws IOException {
        final String MESSAGE = """
                {
                  "messages": [
                    {
                      "details": {
                        "queue_id": "yrn:yc:ymq:ru-central1:b1gh84dl01a0jkghcf9b:tg-bot-app-dt9lh564tru3gh2l90uu",
                        "message": {
                          "message_id": "2b073a97-a8f635fd-5cd5606b-6811fc75",
                          "md5_of_body": "227d53eaa080cc3bad424a352fd37563",
                          "body": "{\\"update_id\\":10281888,\\n\\"message\\":{\\"message_id\\":252,\\"from\\":{\\"id\\":229590625,\\"is_bot\\":false,\\"first_name\\":\\"Max\\",\\"username\\":\\"Mfx_m\\",\\"language_code\\":\\"en\\"},\\"chat\\":{\\"id\\":229590625,\\"first_name\\":\\"Max\\",\\"username\\":\\"Mfx_m\\",\\"type\\":\\"private\\"},\\"date\\":1695296606,\\"text\\":\\"/today\\",\\"entities\\":[{\\"offset\\":0,\\"length\\":6,\\"type\\":\\"bot_command\\"}]}}",
                          "attributes": {
                            "ApproximateFirstReceiveTimestamp": "1695296607678",
                            "ApproximateReceiveCount": "1",
                            "SenderId": "ajee6hju6eohckp9g2ru@as",
                            "SentTimestamp": "1695296607376"
                          },
                          "message_attributes": {},
                          "md5_of_message_attributes": ""
                        }
                      },
                      "event_metadata": {
                        "event_id": "2b073a97-a8f635fd-5cd5606b-6811fc75",
                        "event_type": "yandex.cloud.events.messagequeue.QueueMessage",
                        "created_at": "2023-09-21T11:43:27.376Z",
                        "tracing_context": null,
                        "cloud_id": "b1gkclasvcpkk5ilt8pp",
                        "folder_id": "b1gh84dl01a0jkghcf9b"
                      }
                    }
                  ]
                }
                """;

        ObjectMapper mapper = new ObjectMapper();
        Handler handler = new Handler();
        String status = handler.apply(mapper.readValue(MESSAGE, QueueMessage.class));
        log.info("Status: " + mapper.writeValueAsString(status));
    }
}
