package github.ikhvjs.recipes.exception;

import java.time.LocalDateTime;

public class ErrorMessage {
        final private int statusCode;
        final private LocalDateTime timestamp;
        final private String message;
        final private String description;

        public ErrorMessage(int statusCode, LocalDateTime timestamp, String message, String description) {
            this.statusCode = statusCode;
            this.timestamp = timestamp;
            this.message = message;
            this.description = description;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public String getMessage() {
            return message;
        }

        public String getDescription() {
            return description;
        }

}
