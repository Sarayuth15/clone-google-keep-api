package com.googlekeep.service;

import com.googlekeep.dto.request.CollaboratorRequest;
import com.googlekeep.dto.response.NoteResponse;
import com.googlekeep.entity.Collaborator;
import com.googlekeep.entity.Note;
import com.googlekeep.entity.User;
import com.googlekeep.exception.DuplicateResourceException;
import com.googlekeep.exception.ResourceNotFoundException;
import com.googlekeep.exception.UnauthorizedException;
import com.googlekeep.repository.CollaboratorRepository;
import com.googlekeep.repository.NoteRepository;
import com.googlekeep.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CollaboratorService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final CollaboratorRepository collaboratorRepository;
    private final NoteService noteService;

    @Transactional
    public NoteResponse addCollaborator(Long noteId, CollaboratorRequest.Add request, Long ownerId) {
        Note note = noteRepository.findByIdAndOwnerId(noteId, ownerId)
            .orElseThrow(() -> new ResourceNotFoundException("Note", noteId));

        User collaboratorUser = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

        if (collaboratorUser.getId().equals(ownerId)) {
            throw new UnauthorizedException("Cannot add yourself as a collaborator");
        }

        if (collaboratorRepository.existsByNoteIdAndUserId(noteId, collaboratorUser.getId())) {
            throw new DuplicateResourceException("User is already a collaborator on this note");
        }

        Collaborator collaborator = Collaborator.builder()
            .note(note)
            .user(collaboratorUser)
            .permission(request.getPermission())
            .build();

        note.getCollaborators().add(collaborator);
        return noteService.mapToResponse(noteRepository.save(note));
    }

    @Transactional
    public NoteResponse updateCollaboratorPermission(Long noteId, Long collaboratorUserId,
                                                     CollaboratorRequest.UpdatePermission request, Long ownerId) {
        noteRepository.findByIdAndOwnerId(noteId, ownerId)
            .orElseThrow(() -> new ResourceNotFoundException("Note", noteId));

        Collaborator collaborator = collaboratorRepository.findByNoteIdAndUserId(noteId, collaboratorUserId)
            .orElseThrow(() -> new ResourceNotFoundException("Collaborator not found"));

        collaborator.setPermission(request.getPermission());
        collaboratorRepository.save(collaborator);

        Note note = noteRepository.findById(noteId).orElseThrow();
        return noteService.mapToResponse(note);
    }

    @Transactional
    public NoteResponse removeCollaborator(Long noteId, Long collaboratorUserId, Long ownerId) {
        Note note = noteRepository.findByIdAndOwnerId(noteId, ownerId)
            .orElseThrow(() -> new ResourceNotFoundException("Note", noteId));

        collaboratorRepository.deleteByNoteIdAndUserId(noteId, collaboratorUserId);
        note.getCollaborators().removeIf(c -> c.getUser().getId().equals(collaboratorUserId));

        return noteService.mapToResponse(noteRepository.save(note));
    }

    @Transactional
    public void leaveNote(Long noteId, Long userId) {
        noteRepository.findById(noteId)
            .orElseThrow(() -> new ResourceNotFoundException("Note", noteId));
        collaboratorRepository.deleteByNoteIdAndUserId(noteId, userId);
    }
}
