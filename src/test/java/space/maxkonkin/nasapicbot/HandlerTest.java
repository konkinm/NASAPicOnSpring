package space.maxkonkin.nasapicbot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import space.maxkonkin.nasapicbot.model.QueueMessage;
import space.maxkonkin.nasapicbot.web.Handler;

public class HandlerTest {
    public static void main(String[] args) throws JsonProcessingException {
        final String MESSAGE = """
                {
                  "messages": [
                    {
                      "details": {
                        "queue_id": "yrn:yc:ymq:ru-central1:b1gh84dl01a0jkghcf9b:tg-bot-app-dt9lh564tru3gh2l90uu",
                        "message": {
                          "message_id": "2b073a97-a8f635fd-5cd5606b-6811fc75",
                          "md5_of_body": "227d53eaa080cc3bad424a352fd37563",
                          "body": "{\\"update_id\\":10281888,\\n\\"message\\":{\\"message_id\\":252,\\"from\\":{\\"id\\":229590625,\\"is_bot\\":false,\\"first_name\\":\\"Max\\",\\"username\\":\\"Mfx_m\\",\\"language_code\\":\\"en\\"},\\"chat\\":{\\"id\\":229590625,\\"first_name\\":\\"Max\\",\\"username\\":\\"Mfx_m\\",\\"type\\":\\"private\\"},\\"date\\":1695296606,\\"text\\":\\"/start\\",\\"entities\\":[{\\"offset\\":0,\\"length\\":6,\\"type\\":\\"bot_command\\"}]}}",
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
        final String MESSAGE_2 =
                """
                        {
                            "messages": [
                                {
                                    "event_metadata": {
                                        "event_id": "cce76685-5828-4304-a83d-95643c0507a0",
                                        "event_type": "yandex.cloud.events.messagequeue.QueueMessage",
                                        "created_at": "2019-09-24T00:54:28.980441Z"
                                    },
                                    "details": {
                                        "queue_id": "yrn:yc:ymq:ru-central1:21i6v06sqmsaoeon7nus:event-queue",
                                        "message": {
                                            "message_id": "cce76685-5828-4304-a83d-95643c0507a0",
                                            "md5_of_body": "d29343907090dff4cec4a9a0efb80d20",
                                            "body": "{\\"update_id\\":10281888,\\"message\\":{\\"message_id\\":252,\\"from\\":{\\"id\\":229590625,\\"is_bot\\":false,\\"first_name\\":\\"Max\\",\\"username\\":\\"Mfx_m\\",\\"language_code\\":\\"en\\"},\\"chat\\":{\\"id\\":229590625,\\"first_name\\":\\"Max\\",\\"username\\":\\"Mfx_m\\",\\"type\\":\\"private\\"},\\"date\\":1695296606,\\"text\\":\\"/start\\",\\"entities\\":[{\\"offset\\":0,\\"length\\":6,\\"type\\":\\"bot_command\\"}]}}",
                                            "attributes": {
                                                "SentTimestamp": "1569285804456"
                                            },
                                            "message_attributes": {
                                                "messageAttributeKey": {
                                                    "dataType": "StringValue",
                                                    "stringValue": "value"
                                                }
                                            },
                                            "md5_of_message_attributes": "83eb2d0afefb150c1ffe69f66c3de068"
                                        }
                                    }
                                }
                            ]
                        }
                """;
        ObjectMapper mapper = new ObjectMapper();
        Handler handler = new Handler();
        String message = handler.apply(mapper.readValue(MESSAGE_2, QueueMessage.class));
        System.out.println("Message: " + mapper.writeValueAsString(message));
    }
}
