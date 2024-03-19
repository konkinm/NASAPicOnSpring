package space.maxkonkin.nasapicbot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class QueueMessage {
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
    public static class Details {
        @JsonProperty("queue_id")
        private String queueId;
        @JsonProperty("message")
        private MessageDetails message;
    }

    @Getter
    @Setter
    public static class MessageDetails {
        @JsonProperty("message_id")
        private String messageId;
        @JsonProperty("md5_of_body")
        private String md5OfBody;
        @JsonProperty("body")
        private String body;
        @JsonProperty("attributes")
        private Attributes attributes;
        @JsonProperty("message_attributes")
        private MessageAttributes messageAttributes;
        @JsonProperty("md5_of_message_attributes")
        private String md5OfMessageAttributes;
    }

    @Getter
    @Setter
    public static class Attributes {
        @JsonProperty("ApproximateFirstReceiveTimestamp")
        private String approximateFirstReceiveTimestamp;
        @JsonProperty("ApproximateReceiveCount")
        private String approximateReceiveCount;
        @JsonProperty("SenderId")
        private String senderId;
        @JsonProperty("SentTimestamp")
        private String sentTimestamp;
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
    private static class MessageAttributes {
        @JsonProperty("messageAttributeKey")
        private MessageAttributeKey messageAttributeKey;
    }

    @Getter
    @Setter
    private static class MessageAttributeKey {
        @JsonProperty("dataType")
        private String dataType;
        @JsonProperty("stringValue")
        private String stringValue;
    }
}
