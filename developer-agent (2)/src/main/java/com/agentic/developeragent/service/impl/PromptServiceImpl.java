package com.agentic.developeragent.service.impl;

import com.agentic.developeragent.dto.LlmGenerationRequest;
import com.agentic.developeragent.service.PromptService;
import org.springframework.stereotype.Service;

/**
 * Builds the prompt template used to instruct the LLM to produce
 * production-ready code for a Jira ticket. The prompt strictly asks the
 * model to respond as a JSON document so the response can be parsed
 * deterministically into {@code GeneratedFile} entries.
 */
@Service
public class PromptServiceImpl implements PromptService {

    private static final String SYSTEM_INSTRUCTIONS = """
            You are an experienced Senior Software Engineer.

            Read the following Jira task and generate production-ready code that fully \
            satisfies it. Only generate the code required by the ticket - do not invent \
            unrelated functionality.

            You may generate, when relevant to the ticket:
            - Java classes (controllers, services, repositories, DTOs)
            - Unit tests
            - Configuration
            - Swagger / OpenAPI annotations
            - Bean Validation annotations
            - Logging statements

            CRITICAL OUTPUT FORMAT REQUIREMENT:
            Respond with ONLY a single valid JSON object and nothing else - no markdown \
            fences, no commentary, no preamble, no trailing explanation. The JSON object \
            must have this exact shape:

            {
              "files": [
                {
                  "filename": "OrderController.java",
                  "path": "src/main/java/com/example/controller/OrderController.java",
                  "content": "<full file content as a JSON string, newlines escaped as \\n>"
                }
              ]
            }

            Rules:
            - "path" must be the full relative path of the file within a standard Maven \
              project layout (e.g. src/main/java/..., src/test/java/...).
            - "content" must be the complete, compilable file content. Never write \
              placeholders such as "// implement later" or "TODO".
            - Include as many files as necessary to satisfy the ticket, and no more.
            - Do not wrap the JSON in markdown code fences.
            """;

    @Override
    public String buildPrompt(LlmGenerationRequest request) {
        return SYSTEM_INSTRUCTIONS + """

                ----------------------------------------
                JIRA TICKET
                ----------------------------------------
                Summary: %s

                Description:
                %s

                Acceptance Criteria:
                %s
                ----------------------------------------
                """.formatted(
                nullToEmpty(request.summary()),
                nullToEmpty(request.description()),
                nullToEmpty(request.acceptanceCriteria())
        );
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
