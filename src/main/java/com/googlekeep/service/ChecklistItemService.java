package com.googlekeep.service;

import com.googlekeep.dto.request.ChecklistItemRequest;
import com.googlekeep.dto.response.NoteResponse;
import com.googlekeep.entity.ChecklistItem;
import com.googlekeep.entity.Note;
import com.googlekeep.exception.ResourceNotFoundException;
import com.googlekeep.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChecklistItemService {

    private final NoteRepository noteRepository;
    private final NoteService noteService;

    @Transactional
    public NoteResponse addItem(Long noteId, ChecklistItemRequest.Create request, Long userId) {
        Note note = noteRepository.findByIdAndOwnerId(noteId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Note", noteId));

        ChecklistItem item = ChecklistItem.builder()
            .text(request.getText())
            .checked(request.isChecked())
            .position(request.getPosition())
            .note(note)
            .build();

        note.getChecklistItems().add(item);
        return noteService.mapToResponse(noteRepository.save(note));
    }

    @Transactional
    public NoteResponse updateItem(Long noteId, Long itemId, ChecklistItemRequest.Update request, Long userId) {
        Note note = noteRepository.findByIdAndOwnerId(noteId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Note", noteId));

        ChecklistItem item = note.getChecklistItems().stream()
            .filter(ci -> ci.getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("ChecklistItem", itemId));

        if (request.getText() != null) item.setText(request.getText());
        if (request.getChecked() != null) item.setChecked(request.getChecked());
        if (request.getPosition() != null) item.setPosition(request.getPosition());

        return noteService.mapToResponse(noteRepository.save(note));
    }

    @Transactional
    public NoteResponse deleteItem(Long noteId, Long itemId, Long userId) {
        Note note = noteRepository.findByIdAndOwnerId(noteId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Note", noteId));

        note.getChecklistItems().removeIf(ci -> ci.getId().equals(itemId));
        return noteService.mapToResponse(noteRepository.save(note));
    }

    @Transactional
    public NoteResponse clearChecked(Long noteId, Long userId) {
        Note note = noteRepository.findByIdAndOwnerId(noteId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Note", noteId));

        note.getChecklistItems().removeIf(ChecklistItem::isChecked);
        return noteService.mapToResponse(noteRepository.save(note));
    }
}
