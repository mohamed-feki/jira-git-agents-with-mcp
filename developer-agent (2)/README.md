# developer-agent

Agentic AI system — **Developer Agent**.

Receives a Jira ticket description (summary, description, acceptance criteria)
from the Jira Agent, sends it to an LLM through **Spring AI**, and parses the
model's response into a set of generated source files ready to be committed
by the GitHub Agent.

## Responsibilities

- Builds a structured prompt instructing the LLM to act as a Senior Software
  Engineer and return production-ready code as JSON.
- Supports both **OpenAI** and **Ollama** as the underlying LLM, switchable
  via configuration — no code changes required.
- Parses the LLM's JSON response into `GeneratedFile { filename, path, content }`.

## API

### `POST /develop`

Request:

```json
{
  "summary": "Add pagination to the orders endpoint",
  "description": "As a client I want paginated results from GET /orders",
  "acceptanceCriteria": "Given a page size of 20, when I call GET /orders, then I receive 20 results and a next-page token"
}
```

Response:

```json
{
  "files": [
    {
      "filename": "OrderController.java",
      "path": "src/main/java/com/example/controller/OrderController.java",
      "content": "package com.example.controller;\n\n..."
    },
    {
      "filename": "OrderControllerTest.java",
      "path": "src/test/java/com/example/controller/OrderControllerTest.java",
      "content": "package com.example.controller;\n\n..."
    }
  ]
}
```

## Choosing the LLM provider

**Default: Ollama, no API key required.** The app boots with `agent.llm.provider=ollama`
and the OpenAI autoconfiguration is excluded via `spring.autoconfigure.exclude`
in `application.yml`, so Spring never tries to construct an `OpenAiChatModel`
bean at startup — which is what previously failed fast with "OpenAI API key
must be set" even when you only intended to use Ollama.

Make sure Ollama itself is running and has the model pulled:

```bash
ollama pull llama3.1
ollama serve
```

Then just:

```bash
mvn spring-boot:run
```

### Switching to OpenAI

Activate the `openai` Spring profile, which both re-enables the OpenAI
autoconfiguration and points `agent.llm.provider` back at `openai`:

```bash
OPENAI_API_KEY=sk-... mvn spring-boot:run -Dspring-boot.run.profiles=openai
```

or, in IntelliJ's run configuration, set:
- **Active profiles:** `openai`
- **Environment variables:** `OPENAI_API_KEY=sk-...`

| Property                     | Env var           | Default            |
|-------------------------------|--------------------|---------------------|
| `agent.llm.provider`          | `LLM_PROVIDER`     | `ollama`            |
| `spring.ai.ollama.base-url`   | `OLLAMA_BASE_URL`  | `http://localhost:11434` |
| `spring.ai.ollama.chat.options.model` | `OLLAMA_MODEL` | `llama3.1`       |
| `spring.ai.openai.api-key`    | `OPENAI_API_KEY`   | (required only when the `openai` profile is active) |
| `spring.ai.openai.chat.options.model` | `OPENAI_MODEL` | `gpt-4o`         |

## Running locally

```bash
mvn spring-boot:run
```

Service starts on port `8082`. Swagger UI: `http://localhost:8082/swagger-ui.html`.

## Running with Docker

```bash
docker build -t developer-agent .
docker run -p 8082:8082 -e OPENAI_API_KEY=sk-... developer-agent
```

## Tests

```bash
mvn test
```

## Architecture

```
controller/  -> REST endpoint (/develop)
service/     -> PromptService, LlmService, ResponseParserService, CodeGenerationService (orchestrator)
dto/         -> request/response contracts, GeneratedFile
exception/   -> exception types + global handler
config/      -> LlmProperties, ChatClient bean selection (OpenAI/Ollama), OpenAPI
```

> Note: Spring AI bean names for the underlying `OpenAiChatModel` / `OllamaChatModel`
> may vary slightly across Spring AI releases. If autoconfiguration bean names differ
> in the Spring AI version you pin, adjust the `@Qualifier` values in `ChatClientConfig`
> to match.
