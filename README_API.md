# Google Keep Clone REST API

A production-ready REST API that replicates core Google Keep functionality, built with **Spring Boot 3**, **PostgreSQL**, **JPA/Hibernate**, and **Lombok**.

---

## 🚀 Tech Stack

| Layer | Technology |
|-------|-----------|
| Framework | Spring Boot 3.2 |
| Database | PostgreSQL 16 |
| ORM | Spring Data JPA / Hibernate |
| Auth | JWT (jjwt 0.12) |
| Docs | SpringDoc OpenAPI (Swagger UI) |
| Code gen | Lombok |
| Build | Maven |

---

## 📦 Features

| Feature | Details |
|---------|---------|
| 🔐 Authentication | Register, Login, Refresh Token (JWT) |
| 📝 Notes | Text, Checklist, Image note types |
| 🎨 Colors | 13 note colors (DEFAULT, RED, ORANGE, …) |
| 📌 Pin | Pin/unpin notes (always shown first) |
| 🗄️ Archive | Archive/unarchive notes |
| 🗑️ Trash | Soft delete → restore or permanently delete |
| 🔍 Search | Full-text search across title + content |
| 🏷️ Labels | Create, rename, delete labels; filter notes by label |
| ⏰ Reminders | Set/update/delete reminders with repeat options |
| 👥 Collaborators | Share notes by email with VIEW or EDIT permission |
| 🖼️ Images | Attach image URLs to notes |
| ✅ Checklist | Add, update, check/uncheck, reorder, clear items |
| 📖 OpenAPI | Swagger UI at `/swagger-ui.html` |

---

## 🏗️ Project Structure

```
src/main/java/com/googlekeep/
├── config/              # Security, OpenAPI config
├── controller/          # REST controllers
│   ├── AuthController
│   ├── NoteController
│   ├── LabelController
│   ├── ChecklistItemController
│   ├── ReminderController
│   └── CollaboratorController
├── dto/
│   ├── request/         # Input DTOs (validated)
│   └── response/        # Output DTOs
├── entity/              # JPA entities
│   └── enums/           # NoteType, NoteColor, ReminderRepeat, etc.
├── exception/           # Custom exceptions + GlobalExceptionHandler
├── repository/          # Spring Data JPA repositories
├── security/            # JWT filter, JwtUtil, UserDetailsService
├── service/             # Business logic
└── util/                # SecurityUtils (current user helper)
```

---

## ⚡ Quick Start

### Option 1 — Docker Compose (recommended)
```bash
docker-compose up -d
```

### Option 2 — Local
1. Create database:
```sql
CREATE DATABASE google_keep_db;
```
2. Set env variables (or edit `application.yml`):
```bash
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
export JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
```
3. Run:
```bash
mvn spring-boot:run
```

API: `http://localhost:8080`  
Swagger: `http://localhost:8080/swagger-ui.html`

---

## 🌐 API Endpoints

### Auth — `/api/auth`
| Method | Path | Description |
|--------|------|-------------|
| POST | `/register` | Register new user |
| POST | `/login` | Login → get access + refresh token |
| POST | `/refresh` | Get new access token using refresh token |

### Notes — `/api/notes` *(Bearer token required)*
| Method | Path | Description |
|--------|------|-------------|
| GET | `/` | Get active notes |
| GET | `/archived` | Get archived notes |
| GET | `/trash` | Get trashed notes |
| GET | `/shared` | Get notes shared with you |
| GET | `/{id}` | Get single note |
| GET | `/search?q=text` | Search notes |
| GET | `/by-label/{labelId}` | Filter by label |
| GET | `/by-color/{color}` | Filter by color |
| POST | `/` | Create note |
| PUT | `/{id}` | Update note |
| PATCH | `/{id}/color` | Change color |
| PATCH | `/{id}/pin` | Toggle pin |
| PATCH | `/{id}/archive` | Toggle archive |
| PATCH | `/{id}/trash` | Move to trash |
| PATCH | `/{id}/restore` | Restore from trash |
| DELETE | `/{id}` | Permanent delete (must be in trash) |
| DELETE | `/trash` | Empty trash |

### Labels — `/api/labels`
| Method | Path | Description |
|--------|------|-------------|
| GET | `/` | Get all user labels |
| POST | `/` | Create label |
| PUT | `/{id}` | Rename label |
| DELETE | `/{id}` | Delete label |

### Checklist — `/api/notes/{noteId}/checklist`
| Method | Path | Description |
|--------|------|-------------|
| POST | `/` | Add item |
| PATCH | `/{itemId}` | Update item |
| DELETE | `/{itemId}` | Delete item |
| DELETE | `/checked` | Clear all checked items |

### Reminders — `/api/notes/{noteId}/reminder`
| Method | Path | Description |
|--------|------|-------------|
| POST | `/` | Set reminder |
| PATCH | `/{reminderId}` | Update reminder |
| DELETE | `/` | Remove reminder |

### Collaborators — `/api/notes/{noteId}/collaborators`
| Method | Path | Description |
|--------|------|-------------|
| POST | `/` | Add collaborator by email |
| PATCH | `/{userId}/permission` | Update permission |
| DELETE | `/{userId}` | Remove collaborator |
| DELETE | `/leave` | Leave a shared note |

---

## 🗄️ Database Schema

```
users ──< notes ──< checklist_items
              ├──< note_labels >── labels
              ├──< collaborators >── users
              ├──< note_images
              └──  reminders (1:1)
```

---

## 🔒 Auth Flow

```
POST /api/auth/register  →  { accessToken, refreshToken, user }
POST /api/auth/login     →  { accessToken, refreshToken, user }

All protected endpoints:
Authorization: Bearer <accessToken>

POST /api/auth/refresh   →  new { accessToken, refreshToken }
```

---

## 📋 Example Request — Create Note

```json
POST /api/notes
Authorization: Bearer <token>

{
  "title": "Shopping List",
  "type": "CHECKLIST",
  "color": "YELLOW",
  "pinned": true,
  "checklistItems": [
    { "text": "Milk", "checked": false, "position": 0 },
    { "text": "Eggs", "checked": false, "position": 1 }
  ],
  "labelIds": [1, 2]
}
```

---

## 📋 Example Response

```json
{
  "success": true,
  "message": "Note created",
  "data": {
    "id": 42,
    "title": "Shopping List",
    "type": "CHECKLIST",
    "color": "YELLOW",
    "pinned": true,
    "archived": false,
    "trashed": false,
    "checklistItems": [
      { "id": 1, "text": "Milk", "checked": false, "position": 0 },
      { "id": 2, "text": "Eggs", "checked": false, "position": 1 }
    ],
    "labels": [{ "id": 1, "name": "Groceries" }],
    "collaborators": [],
    "reminder": null,
    "images": [],
    "ownerId": 7,
    "ownerName": "Alice",
    "createdAt": "2026-03-05T10:00:00",
    "updatedAt": "2026-03-05T10:00:00"
  },
  "timestamp": "2026-03-05T10:00:00"
}
```
