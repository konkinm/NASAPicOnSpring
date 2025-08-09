package space.maxkonkin.nasapicbot.model

import com.fasterxml.jackson.annotation.JsonProperty

data class TimerMessage(
    @JsonProperty("messages")
    var messages: List<Message>
) {
    data class Message(
        @JsonProperty("event_metadata")
        var eventMetadata: EventMetadata?,
        @JsonProperty("details")
        var details: Details?
    )

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

    data class Details(
        @JsonProperty("trigger_id")
        var triggerId: String?,
        @JsonProperty("payload")
        var payload: String?
    )
}
