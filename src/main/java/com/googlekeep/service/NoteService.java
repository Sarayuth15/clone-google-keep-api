package com.googlekeep.service;

import com.googlekeep.dto.request.NoteRequest;
import com.googlekeep.dto.response.NoteResponse;
import com.googlekeep.entity.*;
import com.googlekeep.entity.enums.NoteColor;
import com.googlekeep.exception.ResourceNotFoundException;
import com.googlekeep.exception.UnauthorizedException;
import com.googlekeep.repository.LabelRepository;
import com.googlekeep.repository.NoteRepository;
import com.googlekeep.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final LabelRepository labelRepository;

    // ── READ ──────────────────────────────────────────────────────────────────

    public List<NoteResponse> getActiveNotes(Long userId) {
        return noteRepository
            .findByOwnerIdAndArchivedFalseAndTrashedFalseOrderByPinnedDescCreatedAtDesc(userId)
            .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<NoteResponse> getArchivedNotes(Long userId) {
        return noteRepository
            .findByOwnerIdAndArchivedTrueAndTrashedFalseOrderByCreatedAtDesc(userId)
            .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<NoteResponse> getTrashedNotes(Long userId) {
        return noteRepository
            .findByOwnerIdAndTrashedTrueOrderByCreatedAtDesc(userId)
            .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public NoteResponse getNoteById(Long noteId, Long userId) {
        Note note = getAccessibleNote(noteId, userId);
        return mapToResponse(note);
    }

    public List<NoteResponse> searchNotes(String query, Long userId) {
        return noteRepository.searchByOwnerIdAndQuery(userId, query)
            .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<NoteResponse> getNotesByLabel(Long labelId, Long userId) {
        return noteRepository.findByOwnerIdAndLabelId(userId, labelId)
            .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<NoteResponse> getNotesByColor(NoteColor color, Long userId) {
        return noteRepository
            .findByOwnerIdAndColorAndTrashedFalseOrderByPinnedDescCreatedAtDesc(userId, color)
            .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<NoteResponse> getSharedNotes(Long userId) {
        return noteRepository.findSharedWithUser(userId)
            .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // ── CREATE ────────────────────────────────────────────────────────────────

    @Transactional
    public NoteResponse createNote(NoteRequest.Create request, Long userId) {
        User owner = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Note note = Note.builder()
            .title(request.getTitle())
            .content(request.getContent())
            .type(request.getType())
            .color(request.getColor())
            .pinned(request.isPinned())
            .owner(owner)
            .build();

        // Attach labels
        if (request.getLabelIds() != null && !request.getLabelIds().isEmpty()) {
            Set<Label> labels = new HashSet<>(labelRepository.findAllById(request.getLabelIds()));
            note.setLabels(labels);
        }

        // Attach checklist items
        if (request.getChecklistItems() != null) {
            request.getChecklistItems().forEach(item -> {
                ChecklistItem ci = ChecklistItem.builder()
                    .text(item.getText())
                    .checked(item.isChecked())
                    .position(item.getPosition())
                    .note(note)
                    .build();
                note.getChecklistItems().add(ci);
            });
        }

        // Attach images
        if (request.getImageUrls() != null) {
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                NoteImage img = NoteImage.builder()
                    .imageUrl(request.getImageUrls().get(i))
                    .position(i)
                    .note(note)
                    .build();
                note.getImages().add(img);
            }
        }

        return mapToResponse(noteRepository.save(note));
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    @Transactional
    public NoteResponse updateNote(Long noteId, NoteRequest.Update request, Long userId) {
        Note note = getOwnedNote(noteId, userId);

        if (request.getTitle() != null) note.setTitle(request.getTitle());
        if (request.getContent() != null) note.setContent(request.getContent());
        if (request.getColor() != null) note.setColor(request.getColor());
        if (request.getPinned() != null) note.setPinned(request.getPinned());

        if (request.getLabelIds() != null) {
            Set<Label> labels = new HashSet<>(labelRepository.findAllById(request.getLabelIds()));
            note.setLabels(labels);
        }

        return mapToResponse(noteRepository.save(note));
    }

    @Transactional
    public NoteResponse updateColor(Long noteId, NoteColor color, Long userId) {
        Note note = getOwnedNote(noteId, userId);
        note.setColor(color);
        return mapToResponse(noteRepository.save(note));
    }

    @Transactional
    public NoteResponse togglePin(Long noteId, Long userId) {
        Note note = getOwnedNote(noteId, userId);
        note.setPinned(!note.isPinned());
        return mapToResponse(noteRepository.save(note));
    }

    @Transactional
    public NoteResponse toggleArchive(Long noteId, Long userId) {
        Note note = getOwnedNote(noteId, userId);
        note.setArchived(!note.isArchived());
        if (note.isArchived()) note.setPinned(false); // unpin when archiving
        return mapToResponse(noteRepository.save(note));
    }

    @Transactional
    public NoteResponse trashNote(Long noteId, Long userId) {
        Note note = getOwnedNote(noteId, userId);
        note.setTrashed(true);
        note.setPinned(false);
        note.setArchived(false);
        return mapToResponse(noteRepository.save(note));
    }

    @Transactional
    public NoteResponse restoreNote(Long noteId, Long userId) {
        Note note = getOwnedNote(noteId, userId);
        note.setTrashed(false);
        return mapToResponse(noteRepository.save(note));
    }

    @Transactional
    public void deleteNotePermanently(Long noteId, Long userId) {
        Note note = getOwnedNote(noteId, userId);
        if (!note.isTrashed()) {
            throw new UnauthorizedException("Note must be in trash before permanent deletion");
        }
        noteRepository.delete(note);
    }

    @Transactional
    public void emptyTrash(Long userId) {
        List<Note> trashed = noteRepository.findByOwnerIdAndTrashedTrueOrderByCreatedAtDesc(userId);
        noteRepository.deleteAll(trashed);
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────

    private Note getOwnedNote(Long noteId, Long userId) {
        return noteRepository.findByIdAndOwnerId(noteId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Note", noteId));
    }

    private Note getAccessibleNote(Long noteId, Long userId) {
        if (!noteRepository.hasAccess(noteId, userId)) {
            throw new UnauthorizedException("Access denied for note: " + noteId);
        }
        return noteRepository.findById(noteId)
            .orElseThrow(() -> new ResourceNotFoundException("Note", noteId));
    }

    public NoteResponse mapToResponse(Note note) {
        return NoteResponse.builder()
            .id(note.getId())
            .title(note.getTitle())
            .content(note.getContent())
            .type(note.getType())
            .color(note.getColor())
            .pinned(note.isPinned())
            .archived(note.isArchived())
            .trashed(note.isTrashed())
            .ownerId(note.getOwner().getId())
            .ownerName(note.getOwner().getName())
            .createdAt(note.getCreatedAt())
            .updatedAt(note.getUpdatedAt())
            .checklistItems(note.getChecklistItems().stream()
                .map(ci -> NoteResponse.ChecklistItemResponse.builder()
                    .id(ci.getId())
                    .text(ci.getText())
                    .checked(ci.isChecked())
                    .position(ci.getPosition())
                    .build())
                .collect(Collectors.toList()))
            .labels(note.getLabels().stream()
                .map(l -> NoteResponse.LabelResponse.builder()
                    .id(l.getId())
                    .name(l.getName())
                    .build())
                .collect(Collectors.toSet()))
            .collaborators(note.getCollaborators().stream()
                .map(c -> NoteResponse.CollaboratorResponse.builder()
                    .userId(c.getUser().getId())
                    .name(c.getUser().getName())
                    .email(c.getUser().getEmail())
                    .permission(c.getPermission())
                    .build())
                .collect(Collectors.toList()))
            .reminder(note.getReminder() != null ? NoteResponse.ReminderResponse.builder()
                .id(note.getReminder().getId())
                .remindAt(note.getReminder().getRemindAt())
                .repeat(note.getReminder().getRepeat())
                .fired(note.getReminder().isFired())
                .build() : null)
            .images(note.getImages().stream()
                .map(img -> NoteResponse.NoteImageResponse.builder()
                    .id(img.getId())
                    .imageUrl(img.getImageUrl())
                    .altText(img.getAltText())
                    .position(img.getPosition())
                    .build())
                .collect(Collectors.toList()))
            .build();
    }
}
