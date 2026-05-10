package com.flashcard.activitylog.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaActivityLogRepository extends JpaRepository<ActivityLogEntity, Long> {
}
