package space.maxkonkin.nasapicbot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class TimerMessage {
    @JsonProperty("messages")
    private List<Message> messages;

    @Getter
    @Setter
    public static class Message {
        @JsonProperty("event_metadata")
        private EventMetadata eventMetadata;
        @JsonProperty("details")
        private Details details;
    }

    @Getter
    @Setter
    public static class EventMetadata {
        @JsonProperty("event_id")
        private String eventId;
        @JsonProperty("event_type")
        private String eventType;
        @JsonProperty("created_at")
        private String createdAt;
        @JsonProperty("tracing_context")
        private String tracingContext;
        @JsonProperty("cloud_id")
        private String cloudId;
        @JsonProperty("folder_id")
        private String folderId;
    }

    @Getter
    @Setter
    public static class Details {
        @JsonProperty("trigger_id")
        private String triggerId;
        @JsonProperty("payload")
        private String payload;
    }
}
