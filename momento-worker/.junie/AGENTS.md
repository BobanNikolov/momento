# Junie instructions for Backend Spring project

## Role

You are working as an AI coding agent inside this backend project. Before changing code, inspect the existing structure and follow the current patterns.

## Technology stack

- Java 21
- Spring Boot
- Gradle Kotlin DSL
- PostgreSQL
- Flyway for database migrations
- Docker for deployment

## Project structure

This backend is separated into modules:

- `api` contains controllers, REST endpoints, request DTOs, response DTOs, and frontend communication logic.
- `application` contains application startup, configuration, security configuration, and environment-specific settings.
- `service` contains business logic, use cases, validations, and orchestration.
- `data` contains entities, repositories, database access code, and Flyway migrations.

Respect this separation strictly.

## Coding rules

- Do not put business logic in controllers.
- Controllers should delegate to services.
- Services should contain business logic.
- Repositories should only handle database access.
- Reuse existing DTOs, mappers, validators, helper classes, and utilities where possible.
- Follow the existing naming conventions.
- Keep changes small and focused.
- Do not reformat unrelated files.
- Do not introduce new dependencies without asking first.
- Do not make large architectural changes unless explicitly requested.

## API rules

- Do not change existing public API contracts unless the task explicitly requires it.
- If changing a request or response DTO, check all usages.
- Keep validation consistent with the existing validation style.
- Return errors using the existing error-handling mechanism.

## Database and Flyway rules

- Never modify existing Flyway migration files.
- If a database change is required, create a new migration.
- Follow the existing migration naming convention.
- Place migrations in the existing Flyway migration folder.
- Do not delete, rename, or reorder migrations.
- Be careful with destructive schema changes.

## Testing rules

- Add or update tests when changing business logic.
- Follow the existing test style.
- Prefer focused tests over large unrelated test changes.
- If tests cannot be run, explain why and list what should be tested manually.

## Jira and Bitbucket rules

- Use Atlassian MCP only for reading unless explicitly instructed otherwise.
- Do not create, update, transition, assign, or comment on Jira issues unless I explicitly ask.
- Do not create branches, commits, pull requests, or Bitbucket comments unless I explicitly ask.
- When working from a Jira ticket, first summarize:
    - requirements
    - acceptance criteria
    - related comments
    - linked issues
    - linked Bitbucket branches or pull requests
- Then inspect the codebase and propose a short implementation plan before editing files.

## Security rules

- Do not expose secrets, tokens, passwords, private keys, or environment variables.
- Do not hardcode credentials.
- Use existing configuration patterns for environment-specific values.

## Response style

- Start with a short summary of the task.
- Explain the plan before making larger changes.
- After changes, summarize modified files.
- Mention tests that were run or explain why they were not run.