package com.flashcard.activitylog.persistence;

import com.flashcard.activitylog.ActivityLogAction;
import com.flashcard.activitylog.ActivityLogEntry;
import com.flashcard.activitylog.ActivityLogRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ActivityLogRepositoryAdapter implements ActivityLogRepository {

    private final JpaActivityLogRepository jpa;

    public ActivityLogRepositoryAdapter(JpaActivityLogRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public ActivityLogEntry save(ActivityLogEntry entry) {
        ActivityLogEntity entity = toEntity(entry);
        ActivityLogEntity saved = jpa.save(entity);
        return toDomain(saved);
    }

    private ActivityLogEntity toEntity(ActivityLogEntry entry) {
        ActivityLogEntity entity = new ActivityLogEntity();
        entity.setId(entry.getId());
        entity.setAction(entry.getAction().name());
        entity.setEntityType(entry.getEntityType());
        entity.setEntityId(entry.getEntityId());
        entity.setOwnerId(entry.getOwnerId());
        entity.setDetails(entry.getDetails());
        entity.setTimestamp(entry.getTimestamp());
        return entity;
    }

    private ActivityLogEntry toDomain(ActivityLogEntity entity) {
        return new ActivityLogEntry(
                entity.getId(),
                ActivityLogAction.valueOf(entity.getAction()),
                entity.getEntityType(),
                entity.getEntityId(),
                entity.getOwnerId(),
                entity.getDetails(),
                entity.getTimestamp()
        );
    }
}
