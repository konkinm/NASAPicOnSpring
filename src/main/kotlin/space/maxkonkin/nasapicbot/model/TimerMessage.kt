package space.maxkonkin.nasapicbot.model

import com.fasterxml.jackson.annotation.JsonProperty

data class TimerMessage(
    @JsonProperty("messages")
    val messages: List<Message>
) {
    data class Message(
        @JsonProperty("event_metadata")
        val eventMetadata: EventMetadata?,
        @JsonProperty("details")
        val details: Details?
    )

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

    data class Details(
        @JsonProperty("trigger_id")
        val triggerId: String?,
        @JsonProperty("payload")
        val payload: String?
    )
}
