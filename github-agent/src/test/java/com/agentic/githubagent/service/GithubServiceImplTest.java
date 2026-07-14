package com.agentic.githubagent.service;

import com.agentic.githubagent.client.GithubClient;
import com.agentic.githubagent.config.GithubProperties;
import com.agentic.githubagent.dto.FileEntry;
import com.agentic.githubagent.dto.GithubCommitResponse;
import com.agentic.githubagent.dto.GithubRefResponse;
import com.agentic.githubagent.dto.GithubRepositoryResponse;
import com.agentic.githubagent.dto.GithubTreeEntry;
import com.agentic.githubagent.dto.GithubTreeResponse;
import com.agentic.githubagent.dto.PushRequest;
import com.agentic.githubagent.dto.PushResponse;
import com.agentic.githubagent.service.impl.GithubServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GithubServiceImplTest {

    @Mock
    private GithubClient githubClient;

    private GithubServiceImpl service;

    @BeforeEach
    void setUp() {
        GithubProperties properties = new GithubProperties(
                "https://api.github.com", "token", "default-owner", null,
                "bot", "bot@example.com", 5000, 20000);
        service = new GithubServiceImpl(githubClient, properties);
    }

    @Test
    void push_createsBlobsTreeCommitAndUpdatesRef() {
        PushRequest request = new PushRequest(
                "my-org/my-repo",
                "main",
                "commit message",
                List.of(new FileEntry("src/main/java/Foo.java", "class Foo {}")));

        when(githubClient.getRepository("my-org", "my-repo"))
                .thenReturn(new GithubRepositoryResponse("my-repo", "my-org/my-repo", "https://github.com/my-org/my-repo", "main"));
        when(githubClient.getBranchRef(eq("my-org"), eq("my-repo"), eq("main")))
                .thenReturn(new GithubRefResponse("refs/heads/main", new GithubRefResponse.GithubRefObject("parent-sha", "commit", "url")));
        when(githubClient.createBlob(eq("my-org"), eq("my-repo"), anyString())).thenReturn("blob-sha");
        when(githubClient.createTree(eq("my-org"), eq("my-repo"), eq("parent-sha"), anyList()))
                .thenReturn(new GithubTreeResponse("tree-sha", "url"));
        when(githubClient.createCommit(eq("my-org"), eq("my-repo"), eq("commit message"), eq("tree-sha"), eq("parent-sha")))
                .thenReturn(new GithubCommitResponse("commit-sha", "url", "https://github.com/my-org/my-repo/commit/commit-sha"));

        PushResponse response = service.push(request);

        assertThat(response.commitSha()).isEqualTo("commit-sha");
        assertThat(response.commitUrl()).isEqualTo("https://github.com/my-org/my-repo/commit/commit-sha");
        assertThat(response.repositoryUrl()).isEqualTo("https://github.com/my-org/my-repo");
        assertThat(response.filesPushed()).isEqualTo(1);

        verify(githubClient).updateRef("my-org", "my-repo", "main", "commit-sha");
    }

    @Test
    void push_usesDefaultOwner_whenRepositoryHasNoSlash() {
        PushRequest request = new PushRequest("my-repo", "main", "msg", List.of(new FileEntry("a.txt", "content")));

        when(githubClient.getRepository("default-owner", "my-repo"))
                .thenReturn(new GithubRepositoryResponse("my-repo", "default-owner/my-repo", "https://github.com/default-owner/my-repo", "main"));
        when(githubClient.getBranchRef(eq("default-owner"), eq("my-repo"), eq("main")))
                .thenReturn(new GithubRefResponse("refs/heads/main", new GithubRefResponse.GithubRefObject("p", "commit", "u")));
        when(githubClient.createBlob(eq("default-owner"), eq("my-repo"), anyString())).thenReturn("b");
        when(githubClient.createTree(eq("default-owner"), eq("my-repo"), eq("p"), anyList()))
                .thenReturn(new GithubTreeResponse("t", "u"));
        when(githubClient.createCommit(eq("default-owner"), eq("my-repo"), eq("msg"), eq("t"), eq("p")))
                .thenReturn(new GithubCommitResponse("c", "u", "https://github.com/default-owner/my-repo/commit/c"));

        PushResponse response = service.push(request);

        assertThat(response.commitSha()).isEqualTo("c");
        verify(githubClient).getRepository("default-owner", "my-repo");
    }
}
