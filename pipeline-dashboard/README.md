# Agentic Pipeline Dashboard

Angular 18 standalone app that drives a 3-stage agent pipeline
(`jira-agent` → `developer-agent` → `github-agent`) from the browser and
visualizes progress in real time.

## Run it

```bash
npm install
npm start
```

Open http://localhost:4200. Make sure the three backend services are
running on 8081/8082/8083 with CORS enabled for that origin (already the
case per the brief).

## Optional: regenerate typed API clients from the live OpenAPI docs

The app currently uses hand-written types in `src/app/api/models.ts` and
thin `HttpClient` wrappers in `src/app/api/*-agent.service.ts`, matching
the contracts in the brief exactly. If you'd rather generate clients from
the live services instead:

```bash
npm install -g @openapitools/openapi-generator-cli

openapi-generator-cli generate -i http://localhost:8081/v3/api-docs -g typescript-angular -o src/app/api/jira-agent
openapi-generator-cli generate -i http://localhost:8082/v3/api-docs -g typescript-angular -o src/app/api/developer-agent
openapi-generator-cli generate -i http://localhost:8083/v3/api-docs -g typescript-angular -o src/app/api/github-agent
```

Then swap the three service files in `src/app/api/` to call the
generated clients instead, and update the imports in
`src/app/core/pipeline.service.ts`.

## Project layout

```
src/app/
  api/                 typed HTTP clients + shared models
  core/
    pipeline-state.model.ts   stage/pipeline state types
    pipeline.service.ts       orchestrates the 3 calls, owns state as signals
  features/
    pipeline-form/            ticket key / repo / branch inputs
    pipeline-stepper/          the 3-stage timeline
      jira-step/
      develop-step/
      github-step/
    pipeline-summary/          sticky footer: elapsed time, tokens, status
  app.component.ts             composes everything
```

## Design notes

- State lives in one injectable `PipelineService`, exposed as Angular
  signals (`state`, `status`, `totalTokens`, etc.) — no NgRx needed.
- Every stage transitions `NOT_STARTED → IN_PROGRESS → DONE | FAILED`
  and the pipeline halts on the first failure. "Retry from this step"
  re-runs only the failed stage and everything after it, reusing the
  already-completed stages' results (so a GitHub failure doesn't force
  a new LLM call).
- `PipelineService.extractErrorMessage` parses the backend's
  `ApiErrorResponse` body and surfaces `message` directly; network-level
  failures (status 0, likely CORS) get a distinct, actionable message.
- Token usage fields (`tokenUsage.*`) are rendered as `—` when `null`
  rather than `NaN`/`null`.
