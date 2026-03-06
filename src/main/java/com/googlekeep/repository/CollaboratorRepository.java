package com.googlekeep.repository;

import com.googlekeep.entity.Collaborator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CollaboratorRepository extends JpaRepository<Collaborator, Long> {
    Optional<Collaborator> findByNoteIdAndUserId(Long noteId, Long userId);
    boolean existsByNoteIdAndUserId(Long noteId, Long userId);
    void deleteByNoteIdAndUserId(Long noteId, Long userId);
}
