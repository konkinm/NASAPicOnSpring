package space.maxkonkin.nasapicbot.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class QueueMessage(
    @JsonProperty("messages")
    var messages: List<Message>
) {
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    data class Message(
        @JsonProperty("event_metadata")
        var eventMetadata: EventMetadata?,
        @JsonProperty("details")
        var details: Details?
    )

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    data class Details(
        @JsonProperty("queue_id")
        var queueId: String?,
        @JsonProperty("message")
        var message: MessageDetails?
    )

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    data class MessageDetails(
        @JsonProperty("message_id")
        var messageId: String?,
        @JsonProperty("md5_of_body")
        var md5OfBody: String?,
        @JsonProperty("body")
        var body: String?,
        @JsonProperty("attributes")
        var attributes: Attributes?,
        @JsonProperty("message_attributes")
        var messageAttributes: MessageAttributes?,
        @JsonProperty("md5_of_message_attributes")
        var md5OfMessageAttributes: String?
    )

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    data class Attributes(
        @JsonProperty("ApproximateFirstReceiveTimestamp")
        var approximateFirstReceiveTimestamp: String?,
        @JsonProperty("ApproximateReceiveCount")
        var approximateReceiveCount: String?,
        @JsonProperty("SenderId")
        var senderId: String?,
        @JsonProperty("SentTimestamp")
        var sentTimestamp: String?
    )

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    data class EventMetadata(
        @JsonProperty("event_id")
        var eventId: String?,
        @JsonProperty("event_type")
        var eventType: String?,
        @JsonProperty("created_at")
        var createdAt: String?,
        @JsonProperty("tracing_context")
        var tracingContext: String?,
        @JsonProperty("cloud_id")
        var cloudId: String?,
        @JsonProperty("folder_id")
        var folderId: String?
    )

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    data class MessageAttributes(
        @JsonProperty("messageAttributeKey")
        var messageAttributeKey: MessageAttributeKey?
    )

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    data class MessageAttributeKey(
        @JsonProperty("dataType")
        var dataType: String?,
        @JsonProperty("stringvarue")
        var stringvarue: String?
    )
}
