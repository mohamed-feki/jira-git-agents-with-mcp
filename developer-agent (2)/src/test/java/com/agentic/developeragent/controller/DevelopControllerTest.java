package com.agentic.developeragent.controller;

import com.agentic.developeragent.dto.DevelopResponse;
import com.agentic.developeragent.dto.GeneratedFile;
import com.agentic.developeragent.dto.TokenUsage;
import com.agentic.developeragent.service.CodeGenerationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DevelopController.class)
class DevelopControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CodeGenerationService codeGenerationService;

    @Test
    void develop_returns200_withGeneratedFiles() throws Exception {
        DevelopResponse response = new DevelopResponse(
                List.of(new GeneratedFile("Foo.java", "src/main/java/Foo.java", "class Foo {}")),
                new TokenUsage(100, 200, 300));
        when(codeGenerationService.develop(any())).thenReturn(response);

        mockMvc.perform(post("/develop")
                        .contentType("application/json")
                        .content("{\"summary\":\"S\",\"description\":\"D\",\"acceptanceCriteria\":\"AC\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.files[0].filename").value("Foo.java"))
                .andExpect(jsonPath("$.tokenUsage.totalTokens").value(300));
    }

    @Test
    void develop_returns400_whenSummaryBlank() throws Exception {
        mockMvc.perform(post("/develop")
                        .contentType("application/json")
                        .content("{\"summary\":\"\",\"description\":\"D\"}"))
                .andExpect(status().isBadRequest());
    }
}
