package com.googlekeep.service;

import com.googlekeep.dto.request.ReminderRequest;
import com.googlekeep.dto.response.NoteResponse;
import com.googlekeep.entity.Note;
import com.googlekeep.entity.Reminder;
import com.googlekeep.exception.ResourceNotFoundException;
import com.googlekeep.repository.NoteRepository;
import com.googlekeep.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReminderService {

    private final NoteRepository noteRepository;
    private final ReminderRepository reminderRepository;
    private final NoteService noteService;

    @Transactional
    public NoteResponse setReminder(Long noteId, ReminderRequest.Create request, Long userId) {
        Note note = noteRepository.findByIdAndOwnerId(noteId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Note", noteId));

        Reminder reminder = note.getReminder();
        if (reminder == null) {
            reminder = Reminder.builder()
                .note(note)
                .remindAt(request.getRemindAt())
                .repeat(request.getRepeat())
                .build();
            note.setReminder(reminder);
        } else {
            reminder.setRemindAt(request.getRemindAt());
            reminder.setRepeat(request.getRepeat());
            reminder.setFired(false);
        }

        return noteService.mapToResponse(noteRepository.save(note));
    }

    @Transactional
    public NoteResponse updateReminder(Long noteId, Long reminderId, ReminderRequest.Update request, Long userId) {
        Note note = noteRepository.findByIdAndOwnerId(noteId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Note", noteId));

        Reminder reminder = reminderRepository.findById(reminderId)
            .orElseThrow(() -> new ResourceNotFoundException("Reminder", reminderId));

        if (request.getRemindAt() != null) reminder.setRemindAt(request.getRemindAt());
        if (request.getRepeat() != null) reminder.setRepeat(request.getRepeat());
        reminder.setFired(false);

        reminderRepository.save(reminder);
        return noteService.mapToResponse(note);
    }

    @Transactional
    public NoteResponse deleteReminder(Long noteId, Long userId) {
        Note note = noteRepository.findByIdAndOwnerId(noteId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Note", noteId));

        if (note.getReminder() != null) {
            reminderRepository.delete(note.getReminder());
            note.setReminder(null);
        }

        return noteService.mapToResponse(noteRepository.save(note));
    }
}
