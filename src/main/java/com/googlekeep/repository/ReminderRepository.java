package com.googlekeep.repository;

import com.googlekeep.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    Optional<Reminder> findByNoteId(Long noteId);

    @Query("""
        SELECT r FROM Reminder r
        WHERE r.fired = false
          AND r.remindAt <= :now
        """)
    List<Reminder> findDueReminders(@Param("now") LocalDateTime now);
}
