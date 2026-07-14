package com.agentic.githubagent.controller;

import com.agentic.githubagent.dto.PushResponse;
import com.agentic.githubagent.service.GithubService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GithubController.class)
class GithubControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GithubService githubService;

    @Test
    void push_returns200_withValidRequest() throws Exception {
        when(githubService.push(any())).thenReturn(
                new PushResponse("https://github.com/o/r/commit/sha", "sha", "https://github.com/o/r", 1, "main"));

        mockMvc.perform(post("/github/push")
                        .contentType("application/json")
                        .content("""
                                {
                                  "repository": "o/r",
                                  "branch": "main",
                                  "commitMessage": "msg",
                                  "files": [ { "path": "a.txt", "content": "hello" } ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commitSha").value("sha"));
    }

    @Test
    void push_returns400_whenFilesEmpty() throws Exception {
        mockMvc.perform(post("/github/push")
                        .contentType("application/json")
                        .content("""
                                {
                                  "repository": "o/r",
                                  "branch": "main",
                                  "commitMessage": "msg",
                                  "files": []
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}
