# Junie instructions for React frontend project

## Role

You are working as an AI coding agent inside this React frontend project. Before changing code, inspect the existing structure and follow the current patterns.

## Technology stack

- React
- TypeScript
- Vite
- CSS / SCSS / Tailwind if already used in the project
- REST API integration with the backend

## Project structure

Follow the existing folder structure. Common responsibilities:

```text
map-frontend/
├─ README.md
├─ package.json
├─ vite.config.ts
├─ tsconfig*.json
├─ public/
├─ src/
│  ├─ index.tsx        # app entry
│  ├─ App.tsx          # root component
│  ├─ components/      # shared reusable UI
│  ├─ features/        # feature modules (pages, slices, services)
│  ├─ store/           # Redux Toolkit setup
│  ├─ utils/           # helpers (e.g., routes)
│  ├─ styles/          # theme/styles
│  └─ i18n/            # translations
└─ (Dockerfile, nginx.conf, etc.)
```

## Coding rules

- Use TypeScript correctly.
- Avoid using `any` unless there is a clear reason.
- Reuse existing components before creating new ones.
- Reuse existing API services before adding new fetch/axios logic.
- Keep components small and readable.
- Avoid duplicating UI logic.
- Do not introduce new dependencies without asking first.
- Do not reformat unrelated files.
- Follow the existing naming and styling conventions.

## UI rules

- Match the existing design system.
- Reuse existing buttons, inputs, modals, tables, cards, and layout components.
- Keep spacing, typography, colors, and responsiveness consistent.
- Do not invent a new design style unless explicitly requested.

## API integration rules

- Use existing API client/service patterns.
- Keep request and response types explicit.
- Handle loading, empty, success, and error states.
- Do not hardcode backend URLs.
- Use existing environment/configuration patterns.

## Jira and Bitbucket rules

- Use Atlassian MCP only for reading unless explicitly instructed otherwise.
- Do not create, update, transition, assign, or comment on Jira issues unless I explicitly ask.
- Do not create branches, commits, pull requests, or Bitbucket comments unless I explicitly ask.
- When working from a Jira ticket, first summarize the requirements and acceptance criteria.
- Then inspect the codebase and propose a short implementation plan before editing files.

## Testing rules

- Add or update tests when changing important logic.
- Follow the existing test framework and style.
- If tests cannot be run, explain why and list what should be checked manually.

## Response style

- Start with a short summary of the task.
- Explain the plan before making larger changes.
- After changes, summarize modified files.
- Mention tests that were run or explain why they were not run.