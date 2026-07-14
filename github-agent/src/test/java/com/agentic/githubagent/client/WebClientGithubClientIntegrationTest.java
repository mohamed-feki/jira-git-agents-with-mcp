package com.agentic.githubagent.client;

import com.agentic.githubagent.client.impl.WebClientGithubClient;
import com.agentic.githubagent.dto.GithubBlobResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class WebClientGithubClientIntegrationTest {

    private MockWebServer server;
    private WebClientGithubClient client;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        WebClient webClient = WebClient.builder().baseUrl(server.url("/").toString()).build();
        client = new WebClientGithubClient(webClient);
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void createBlob_returnsShaFromResponse() {
        server.enqueue(new MockResponse()
                .setResponseCode(201)
                .setHeader("Content-Type", "application/json")
                .setBody("{\"sha\":\"blob-sha-123\",\"url\":\"https://api.github.com/blobs/blob-sha-123\"}"));

        String sha = client.createBlob("owner", "repo", "hello world");

        assertThat(sha).isEqualTo("blob-sha-123");
    }

    @Test
    void branchExists_returnsFalse_on404() {
        server.enqueue(new MockResponse().setResponseCode(404));

        boolean exists = client.branchExists("owner", "repo", "feature-x");

        assertThat(exists).isFalse();
    }
}
