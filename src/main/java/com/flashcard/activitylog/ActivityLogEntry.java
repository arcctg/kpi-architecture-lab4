package com.flashcard.activitylog;

import java.time.LocalDateTime;

public class ActivityLogEntry {

    private Long id;
    private final ActivityLogAction action;
    private final String entityType;
    private final Long entityId;
    private final Long ownerId;
    private final String details;
    private final LocalDateTime timestamp;

    public ActivityLogEntry(Long id, ActivityLogAction action, String entityType,
                            Long entityId, Long ownerId, String details,
                            LocalDateTime timestamp) {
        this.id = id;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.ownerId = ownerId;
        this.details = details;
        this.timestamp = timestamp;
    }

    public static ActivityLogEntry create(ActivityLogAction action, String entityType,
                                          Long entityId, Long ownerId, String details) {
        return new ActivityLogEntry(null, action, entityType, entityId, ownerId,
                details, LocalDateTime.now());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ActivityLogAction getAction() {
        return action;
    }

    public String getEntityType() {
        return entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public String getDetails() {
        return details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
