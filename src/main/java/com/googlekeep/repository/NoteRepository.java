package com.googlekeep.repository;

import com.googlekeep.entity.Note;
import com.googlekeep.entity.enums.NoteColor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    // Active notes (not archived, not trashed)
    List<Note> findByOwnerIdAndArchivedFalseAndTrashedFalseOrderByPinnedDescCreatedAtDesc(Long ownerId);

    // Archived notes
    List<Note> findByOwnerIdAndArchivedTrueAndTrashedFalseOrderByCreatedAtDesc(Long ownerId);

    // Trashed notes
    List<Note> findByOwnerIdAndTrashedTrueOrderByCreatedAtDesc(Long ownerId);

    // Find by owner + id (for security check)
    Optional<Note> findByIdAndOwnerId(Long id, Long ownerId);

    // Search in title and content
    @Query("""
        SELECT n FROM Note n
        WHERE n.owner.id = :ownerId
          AND n.trashed = false
          AND (
            LOWER(n.title) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(n.content) LIKE LOWER(CONCAT('%', :query, '%'))
          )
        ORDER BY n.pinned DESC, n.createdAt DESC
        """)
    List<Note> searchByOwnerIdAndQuery(@Param("ownerId") Long ownerId, @Param("query") String query);

    // Notes by label
    @Query("""
        SELECT n FROM Note n
        JOIN n.labels l
        WHERE n.owner.id = :ownerId
          AND l.id = :labelId
          AND n.trashed = false
        ORDER BY n.pinned DESC, n.createdAt DESC
        """)
    List<Note> findByOwnerIdAndLabelId(@Param("ownerId") Long ownerId, @Param("labelId") Long labelId);

    // Notes by color
    List<Note> findByOwnerIdAndColorAndTrashedFalseOrderByPinnedDescCreatedAtDesc(Long ownerId, NoteColor color);

    // Collaborator access
    @Query("""
        SELECT n FROM Note n
        JOIN n.collaborators c
        WHERE c.user.id = :userId
          AND n.trashed = false
        ORDER BY n.createdAt DESC
        """)
    List<Note> findSharedWithUser(@Param("userId") Long userId);

    // Check if user has access (owner or collaborator)
    @Query("""
        SELECT COUNT(n) > 0 FROM Note n
        LEFT JOIN n.collaborators c
        WHERE n.id = :noteId
          AND (n.owner.id = :userId OR c.user.id = :userId)
        """)
    boolean hasAccess(@Param("noteId") Long noteId, @Param("userId") Long userId);
}
