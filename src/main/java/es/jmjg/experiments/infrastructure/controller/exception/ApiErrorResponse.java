package es.jmjg.experiments.infrastructure.controller.exception;

import java.time.LocalDateTime;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> details;

    // Private constructor for builder pattern
    private ApiErrorResponse() {}

    // Getters
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    // Builder class
    public static class Builder {
        private final ApiErrorResponse errorResponse;

        public Builder() {
            this.errorResponse = new ApiErrorResponse();
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.errorResponse.timestamp = timestamp;
            return this;
        }

        public Builder status(int status) {
            this.errorResponse.status = status;
            return this;
        }

        public Builder error(String error) {
            this.errorResponse.error = error;
            return this;
        }

        public Builder message(String message) {
            this.errorResponse.message = message;
            return this;
        }

        public Builder path(String path) {
            this.errorResponse.path = path;
            return this;
        }

        public Builder details(Map<String, String> details) {
            this.errorResponse.details = details;
            return this;
        }

        public ApiErrorResponse build() {
            return this.errorResponse;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
