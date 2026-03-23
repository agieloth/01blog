package io.aotchoun.blog.dto.request;

import jakarta.validation.constraints.NotNull;

public class ReportRequest {

    @NotNull(message = "Reason is required")
    private String reason; // SPAM, HARASSMENT, etc.

    private String description; // Optionnel

    public ReportRequest() {}

    public String getReason() { return reason; }
    public String getDescription() { return description; }

    public void setReason(String reason) { this.reason = reason; }
    public void setDescription(String description) { this.description = description; }
}