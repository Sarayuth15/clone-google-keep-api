-- Google Keep Clone - MySQL Schema
-- Tables are created in strict dependency order to avoid FK errors.
-- Using CREATE TABLE IF NOT EXISTS so re-runs are safe.

SET FOREIGN_KEY_CHECKS = 0;

-- 1. users (no FK dependencies)
CREATE TABLE IF NOT EXISTS users (
                                     id              BIGINT          NOT NULL AUTO_INCREMENT,
                                     email           VARCHAR(100)    NOT NULL,
    password        VARCHAR(255)    NOT NULL,
    name            VARCHAR(100)    NOT NULL,
    profile_picture VARCHAR(500)    NULL,
    created_at      DATETIME        NOT NULL,
    updated_at      DATETIME        NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_email (email)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. notes (depends on users)
CREATE TABLE IF NOT EXISTS notes (
                                     id          BIGINT          NOT NULL AUTO_INCREMENT,
                                     title       VARCHAR(1000)   NULL,
    content     TEXT            NULL,
    type        VARCHAR(20)     NOT NULL DEFAULT 'TEXT',
    color       VARCHAR(20)     NOT NULL DEFAULT 'DEFAULT',
    is_pinned   TINYINT(1)      NOT NULL DEFAULT 0,
    is_archived TINYINT(1)      NOT NULL DEFAULT 0,
    is_trashed  TINYINT(1)      NOT NULL DEFAULT 0,
    owner_id    BIGINT          NOT NULL,
    created_at  DATETIME        NOT NULL,
    updated_at  DATETIME        NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_notes_owner FOREIGN KEY (owner_id) REFERENCES users (id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. reminders (depends on notes)
CREATE TABLE IF NOT EXISTS reminders (
                                         id          BIGINT      NOT NULL AUTO_INCREMENT,
                                         note_id     BIGINT      NOT NULL,
                                         remind_at   DATETIME    NOT NULL,
                                         repeat_type VARCHAR(20) NOT NULL DEFAULT 'NONE',
    is_fired    TINYINT(1)  NOT NULL DEFAULT 0,
    created_at  DATETIME    NOT NULL,
    updated_at  DATETIME    NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_reminders_note (note_id),
    CONSTRAINT fk_reminders_note FOREIGN KEY (note_id) REFERENCES notes (id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. checklist_items (depends on notes)
CREATE TABLE IF NOT EXISTS checklist_items (
                                               id          BIGINT          NOT NULL AUTO_INCREMENT,
                                               note_id     BIGINT          NOT NULL,
                                               text        VARCHAR(500)    NOT NULL,
    is_checked  TINYINT(1)      NOT NULL DEFAULT 0,
    position    INT             NOT NULL DEFAULT 0,
    created_at  DATETIME        NOT NULL,
    updated_at  DATETIME        NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_checklist_note FOREIGN KEY (note_id) REFERENCES notes (id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. labels (depends on users)
CREATE TABLE IF NOT EXISTS labels (
                                      id          BIGINT          NOT NULL AUTO_INCREMENT,
                                      name        VARCHAR(100)    NOT NULL,
    user_id     BIGINT          NOT NULL,
    created_at  DATETIME        NOT NULL,
    updated_at  DATETIME        NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_labels_name_user (name, user_id),
    CONSTRAINT fk_labels_user FOREIGN KEY (user_id) REFERENCES users (id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. note_labels join table (depends on notes + labels)
CREATE TABLE IF NOT EXISTS note_labels (
                                           note_id     BIGINT NOT NULL,
                                           label_id    BIGINT NOT NULL,
                                           PRIMARY KEY (note_id, label_id),
    CONSTRAINT fk_notelabels_note  FOREIGN KEY (note_id)  REFERENCES notes (id),
    CONSTRAINT fk_notelabels_label FOREIGN KEY (label_id) REFERENCES labels (id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. collaborators (depends on notes + users)
CREATE TABLE IF NOT EXISTS collaborators (
                                             id          BIGINT      NOT NULL AUTO_INCREMENT,
                                             note_id     BIGINT      NOT NULL,
                                             user_id     BIGINT      NOT NULL,
                                             permission  VARCHAR(10) NOT NULL DEFAULT 'EDIT',
    created_at  DATETIME    NOT NULL,
    updated_at  DATETIME    NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_collaborators_note_user (note_id, user_id),
    CONSTRAINT fk_collaborators_note FOREIGN KEY (note_id) REFERENCES notes (id),
    CONSTRAINT fk_collaborators_user FOREIGN KEY (user_id) REFERENCES users (id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. note_images (depends on notes)
CREATE TABLE IF NOT EXISTS note_images (
                                           id          BIGINT          NOT NULL AUTO_INCREMENT,
                                           note_id     BIGINT          NOT NULL,
                                           image_url   VARCHAR(1000)   NOT NULL,
    alt_text    VARCHAR(500)    NULL,
    position    INT             NOT NULL DEFAULT 0,
    created_at  DATETIME        NOT NULL,
    updated_at  DATETIME        NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_noteimages_note FOREIGN KEY (note_id) REFERENCES notes (id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;