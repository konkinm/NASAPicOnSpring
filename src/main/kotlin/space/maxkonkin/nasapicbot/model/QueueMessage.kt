package space.maxkonkin.nasapicbot.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class QueueMessage(
    @JsonProperty("messages")
    val messages: List<Message>
) {
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    data class Message(
        @JsonProperty("event_metadata")
        val eventMetadata: EventMetadata?,
        @JsonProperty("details")
        val details: Details?
    )

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    data class Details(
        @JsonProperty("queue_id")
        val queueId: String?,
        @JsonProperty("message")
        val message: MessageDetails?
    )

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    data class MessageDetails(
        @JsonProperty("message_id")
        val messageId: String?,
        @JsonProperty("md5_of_body")
        val md5OfBody: String?,
        @JsonProperty("body")
        val body: String?,
        @JsonProperty("attributes")
        val attributes: Attributes?,
        @JsonProperty("message_attributes")
        val messageAttributes: MessageAttributes?,
        @JsonProperty("md5_of_message_attributes")
        val md5OfMessageAttributes: String?
    )

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    data class Attributes(
        @JsonProperty("ApproximateFirstReceiveTimestamp")
        val approximateFirstReceiveTimestamp: String?,
        @JsonProperty("ApproximateReceiveCount")
        val approximateReceiveCount: String?,
        @JsonProperty("SenderId")
        val senderId: String?,
        @JsonProperty("SentTimestamp")
        val sentTimestamp: String?
    )

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    data class EventMetadata(
        @JsonProperty("event_id")
        val eventId: String?,
        @JsonProperty("event_type")
        val eventType: String?,
        @JsonProperty("created_at")
        val createdAt: String?,
        @JsonProperty("tracing_context")
        val tracingContext: String?,
        @JsonProperty("cloud_id")
        val cloudId: String?,
        @JsonProperty("folder_id")
        val folderId: String?
    )

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    data class MessageAttributes(
        @JsonProperty("messageAttributeKey")
        val messageAttributeKey: MessageAttributeKey?
    )

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    data class MessageAttributeKey(
        @JsonProperty("dataType")
        val dataType: String?,
        @JsonProperty("stringvalue")
        val stringvalue: String?
    )
}
