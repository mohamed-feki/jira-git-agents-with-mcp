# github-agent

Agentic AI system — **GitHub Agent**.

Receives a set of generated files from the Developer Agent and pushes them
into a GitHub repository as a single atomic commit, using the GitHub
**Git Data API** (blobs -> tree -> commit -> ref update) rather than the
Contents API, so that N files land in exactly one commit instead of N
separate commits.

## Responsibilities

- Resolves the target branch, creating it from the repository's default
  branch if it does not exist yet.
- Creates a git blob for every file.
- Builds a new tree on top of the branch's current tree.
- Creates a commit referencing that tree.
- Fast-forwards (pushes) the branch ref to the new commit.

## API

### `POST /github/push`

Request:

```json
{
  "repository": "my-org/my-repo",
  "branch": "feature/PROJ-25-pagination",
  "commitMessage": "PROJ-25: add pagination to orders endpoint",
  "files": [
    {
      "path": "src/main/java/com/example/controller/OrderController.java",
      "content": "package com.example.controller;\n\n..."
    }
  ]
}
```

`repository` may be `owner/repo` or just `repo` (in which case the configured
`github.owner` default is used).

Response:

```json
{
  "commitUrl": "https://github.com/my-org/my-repo/commit/abc123...",
  "commitSha": "abc123...",
  "repositoryUrl": "https://github.com/my-org/my-repo",
  "filesPushed": 3,
  "branch": "feature/PROJ-25-pagination"
}
```

## Configuration

| Property                  | Env var                  | Default                    |
|-----------------------------|----------------------------|------------------------------|
| `github.api-base-url`       | `GITHUB_API_BASE_URL`      | `https://api.github.com`    |
| `github.token`              | `GITHUB_TOKEN`             | (required)                  |
| `github.owner`              | `GITHUB_OWNER`             | (optional default owner)    |
| `github.repository`         | `GITHUB_REPOSITORY`        | (optional default repo)     |

The GitHub token must have `repo` scope (or fine-grained `Contents: Read and
write` permission) on the target repository. The token is only ever read
from server-side configuration/environment variables — it is never accepted
in a request body.

## Running locally

```bash
GITHUB_TOKEN=ghp_xxx mvn spring-boot:run
```

Service starts on port `8083`. Swagger UI: `http://localhost:8083/swagger-ui.html`.

## Running with Docker

```bash
docker build -t github-agent .
docker run -p 8083:8083 -e GITHUB_TOKEN=ghp_xxx github-agent
```

## Tests

```bash
mvn test
```

## Architecture

```
controller/     -> REST endpoint (/github/push)
service/        -> GithubService: orchestrates blob/tree/commit/ref workflow
client/         -> GithubClient: low-level GitHub REST API wrapper (WebClient)
dto/            -> request/response contracts + GitHub API payloads
exception/      -> exception types + global handler
config/         -> GithubProperties, WebClient, OpenAPI
```
