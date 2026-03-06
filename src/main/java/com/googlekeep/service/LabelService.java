package com.googlekeep.service;

import com.googlekeep.dto.request.LabelRequest;
import com.googlekeep.dto.response.LabelResponse;
import com.googlekeep.entity.Label;
import com.googlekeep.entity.User;
import com.googlekeep.exception.DuplicateResourceException;
import com.googlekeep.exception.ResourceNotFoundException;
import com.googlekeep.repository.LabelRepository;
import com.googlekeep.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LabelService {

    private final LabelRepository labelRepository;
    private final UserRepository userRepository;

    public List<LabelResponse> getUserLabels(Long userId) {
        return labelRepository.findByUserIdOrderByNameAsc(userId)
            .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public LabelResponse createLabel(LabelRequest.Create request, Long userId) {
        if (labelRepository.existsByNameAndUserId(request.getName(), userId)) {
            throw new DuplicateResourceException("Label already exists: " + request.getName());
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Label label = Label.builder()
            .name(request.getName())
            .user(user)
            .build();

        return mapToResponse(labelRepository.save(label));
    }

    @Transactional
    public LabelResponse updateLabel(Long labelId, LabelRequest.Update request, Long userId) {
        Label label = labelRepository.findByIdAndUserId(labelId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Label", labelId));

        if (labelRepository.existsByNameAndUserId(request.getName(), userId)
            && !label.getName().equals(request.getName())) {
            throw new DuplicateResourceException("Label already exists: " + request.getName());
        }

        label.setName(request.getName());
        return mapToResponse(labelRepository.save(label));
    }

    @Transactional
    public void deleteLabel(Long labelId, Long userId) {
        Label label = labelRepository.findByIdAndUserId(labelId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Label", labelId));
        labelRepository.delete(label);
    }

    private LabelResponse mapToResponse(Label label) {
        return LabelResponse.builder()
            .id(label.getId())
            .name(label.getName())
            .noteCount(label.getNotes().size())
            .createdAt(label.getCreatedAt())
            .build();
    }
}
