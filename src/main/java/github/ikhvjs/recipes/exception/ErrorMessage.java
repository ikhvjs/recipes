package github.ikhvjs.recipes.exception;

import java.time.LocalDateTime;
import java.util.List;

public class ErrorMessage {
        final private int statusCode;
        final private LocalDateTime timestamp;
        final private List<String> messages;
        final private String description;

        public ErrorMessage(int statusCode, LocalDateTime timestamp, List<String> messages, String description) {
            this.statusCode = statusCode;
            this.timestamp = timestamp;
            this.messages = messages;
            this.description = description;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public List<String> getMessages() {
            return messages;
        }

        public String getDescription() {
            return description;
        }

}
