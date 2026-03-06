package com.googlekeep.entity;

import com.googlekeep.entity.enums.CollaboratorPermission;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "collaborators", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"note_id", "user_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Collaborator extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id", nullable = false)
    private Note note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CollaboratorPermission permission = CollaboratorPermission.EDIT;
}
