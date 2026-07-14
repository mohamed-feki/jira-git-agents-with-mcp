package com.agentic.developeragent.service;

import com.agentic.developeragent.dto.DevelopRequest;
import com.agentic.developeragent.dto.DevelopResponse;
import com.agentic.developeragent.dto.GeneratedFile;
import com.agentic.developeragent.dto.LlmGenerationResult;
import com.agentic.developeragent.dto.TokenUsage;
import com.agentic.developeragent.service.impl.CodeGenerationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CodeGenerationServiceImplTest {

    @Mock
    private PromptService promptService;
    @Mock
    private LlmService llmService;
    @Mock
    private ResponseParserService responseParserService;

    private CodeGenerationServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CodeGenerationServiceImpl(promptService, llmService, responseParserService);
    }

    @Test
    void develop_orchestratesPromptLlmParsingAndTokenUsage() {
        DevelopRequest request = new DevelopRequest("Summary", "Description", "AC");
        when(promptService.buildPrompt(any())).thenReturn("PROMPT");

        TokenUsage usage = new TokenUsage(120, 340, 460);
        LlmGenerationResult llmResult = new LlmGenerationResult("RAW_RESPONSE", usage);
        when(llmService.generate("PROMPT")).thenReturn(llmResult);

        List<GeneratedFile> expectedFiles = List.of(new GeneratedFile("Foo.java", "src/main/java/Foo.java", "class Foo {}"));
        when(responseParserService.parse("RAW_RESPONSE")).thenReturn(expectedFiles);

        DevelopResponse response = service.develop(request);

        assertThat(response.files()).isEqualTo(expectedFiles);
        assertThat(response.tokenUsage()).isEqualTo(usage);
        assertThat(response.tokenUsage().totalTokens()).isEqualTo(460);
    }
}
