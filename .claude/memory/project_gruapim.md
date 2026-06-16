---
name: project-gruapim
description: Context about the GRUAPIM Scrum management platform project being built iteratively in 10% increments
metadata:
  type: project
---

GRUAPIM is a Scrum management platform (academic project, IFSP Guarulhos, Group 6) being built as Spring Boot + JPA + Hibernate backend. The PDF is `GRUAPIM_HISTORIAS_DE_USUARIO_GRUPO6.pdf` in `C:\Users\Eduardo\Desktop\GRUAPIM_PROJ`.

**Why:** Academic project needing iterative delivery in 10% increments per conversation turn.

**How to apply:** Always continue from the last completed % and deliver the next 10% when asked.

## 10% Delivery Plan
- **10% ✅ done:** pom.xml, GruapimApplication.java, application.yml (dev/prod), all 7 enums, all 16 entities (BaseEntity + 15 domain entities), DATABASE_ARCHITECTURE.md
- **20% next:** All 16 Spring Data JPA repositories + first Flyway migration scripts (V1–V5)
- **30%:** Security layer — JWT (JwtService, JwtAuthFilter), Spring Security config, UserDetailsServiceImpl, AuthService, AuthController, auth DTOs
- **40%:** UserStory + Sprint services, controllers, DTOs (request/response)
- **50%:** Task management + Kanban service/controller/DTOs
- **60%:** MeetingNote + WebSocket Chat service/controller
- **70%:** Notification service + Burndown service/controller
- **80%:** Git integration service/controller + Project service/controller + MapStruct mappers
- **90%:** Exception handling (GlobalExceptionHandler, custom exceptions) + OpenAPI config + CORS + Audit config
- **100%:** Flyway migrations V6–V16 + unit tests + integration tests + Dockerfile

## Project Structure
`C:\Users\Eduardo\Desktop\GRUAPIM_PROJ\gruapim-backend\` — Spring Boot 3.3, Java 21, PostgreSQL, Flyway, MapStruct, Lombok, JJWT 0.12.5, springdoc 2.5.0

## Roles
- ADMIN (global), PRODUCT_OWNER, SCRUM_MASTER, DEVELOPER (per-project via ProjectMember)

## 16 Entities
User, Project, ProjectMember, UserStory, Sprint, SprintStory, KanbanColumn, Task, TaskStatusHistory, MeetingNote, ChatMessage, StoryComment, Notification, GitRepository, GitCommitLink — all in `com.gruapim.domain.entity`

## 7 Enums
UserRole, Priority, StoryStatus, SprintStatus, TaskStatus, MeetingType, NotificationType — all in `com.gruapim.domain.enums`
