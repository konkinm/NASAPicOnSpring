package space.maxkonkin.nasapicbot

import com.fasterxml.jackson.databind.ObjectMapper
import space.maxkonkin.nasapicbot.model.TimerMessage
import space.maxkonkin.nasapicbot.web.handle

class TimerHandlerTest {
    fun main(args: Array<String>) {
        val message = """
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
                
                """.trimIndent()
        val mapper = ObjectMapper()
        val status = handle(mapper.readValue(message, TimerMessage::class.java))
        println("Status: " + mapper.writeValueAsString(status))
    }
}
