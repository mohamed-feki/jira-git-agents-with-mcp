package com.agentic.githubagent.client.impl;

import com.agentic.githubagent.client.GithubClient;
import com.agentic.githubagent.dto.GithubBlobRequest;
import com.agentic.githubagent.dto.GithubBlobResponse;
import com.agentic.githubagent.dto.GithubCommitResponse;
import com.agentic.githubagent.dto.GithubCreateCommitRequest;
import com.agentic.githubagent.dto.GithubCreateTreeRequest;
import com.agentic.githubagent.dto.GithubRefResponse;
import com.agentic.githubagent.dto.GithubRepositoryResponse;
import com.agentic.githubagent.dto.GithubTreeEntry;
import com.agentic.githubagent.dto.GithubTreeResponse;
import com.agentic.githubagent.dto.GithubUpdateRefRequest;
import com.agentic.githubagent.exception.GithubApiException;
import com.agentic.githubagent.exception.RepositoryNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebClientGithubClient implements GithubClient {

    private final WebClient githubWebClient;

    @Override
    public GithubRepositoryResponse getRepository(String owner, String repo) {
        try {
            return githubWebClient.get()
                    .uri("/repos/{owner}/{repo}", owner, repo)
                    .retrieve()
                    .bodyToMono(GithubRepositoryResponse.class)
                    .block();
        } catch (WebClientResponseException.NotFound ex) {
            throw new RepositoryNotFoundException(owner + "/" + repo);
        } catch (WebClientResponseException ex) {
            throw new GithubApiException("Failed to fetch repository " + owner + "/" + repo + ": " + ex.getStatusCode());
        }
    }

    @Override
    public boolean branchExists(String owner, String repo, String branch) {
        try {
            githubWebClient.get()
                    .uri("/repos/{owner}/{repo}/git/ref/heads/{branch}", owner, repo, branch)
                    .retrieve()
                    .bodyToMono(GithubRefResponse.class)
                    .block();
            return true;
        } catch (WebClientResponseException.NotFound ex) {
            return false;
        } catch (WebClientResponseException ex) {
            throw new GithubApiException("Failed to check branch existence for " + branch + ": " + ex.getStatusCode());
        }
    }

    @Override
    public GithubRefResponse getBranchRef(String owner, String repo, String branch) {
        try {
            GithubRefResponse ref = githubWebClient.get()
                    .uri("/repos/{owner}/{repo}/git/ref/heads/{branch}", owner, repo, branch)
                    .retrieve()
                    .bodyToMono(GithubRefResponse.class)
                    .block();
            if (ref == null) {
                throw new GithubApiException("Empty response resolving ref for branch " + branch);
            }
            return ref;
        } catch (WebClientResponseException.NotFound ex) {
            log.info("Branch '{}' does not exist yet in {}/{}, will create it from the default branch", branch, owner, repo);
            return createBranchFromDefault(owner, repo, branch);
        } catch (WebClientResponseException ex) {
            throw new GithubApiException("Failed to resolve ref for branch " + branch + ": " + ex.getStatusCode());
        }
    }

    private GithubRefResponse createBranchFromDefault(String owner, String repo, String branch) {
        GithubRepositoryResponse repository = getRepository(owner, repo);
        String defaultBranch = repository.defaultBranch() != null ? repository.defaultBranch() : "main";

        GithubRefResponse defaultRef;
        try {
            defaultRef = githubWebClient.get()
                    .uri("/repos/{owner}/{repo}/git/ref/heads/{branch}", owner, repo, defaultBranch)
                    .retrieve()
                    .bodyToMono(GithubRefResponse.class)
                    .block();
        } catch (WebClientResponseException ex) {
            throw new GithubApiException("Failed to resolve default branch '" + defaultBranch + "': " + ex.getStatusCode());
        }

        if (defaultRef == null || defaultRef.object() == null) {
            throw new GithubApiException("Could not resolve head commit of default branch '" + defaultBranch + "'");
        }

        createRef(owner, repo, branch, defaultRef.object().sha());
        return new GithubRefResponse("refs/heads/" + branch, defaultRef.object());
    }

    @Override
    public void createRef(String owner, String repo, String branch, String commitSha) {
        try {
            githubWebClient.post()
                    .uri("/repos/{owner}/{repo}/git/refs", owner, repo)
                    .bodyValue(new RefCreateBody("refs/heads/" + branch, commitSha))
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
            log.info("Created branch '{}' in {}/{} at commit {}", branch, owner, repo, commitSha);
        } catch (WebClientResponseException ex) {
            throw new GithubApiException("Failed to create branch " + branch + ": " + ex.getStatusCode() + " " + ex.getResponseBodyAsString());
        }
    }

    private record RefCreateBody(String ref, String sha) {
    }

    @Override
    public String createBlob(String owner, String repo, String content) {
        try {
            GithubBlobResponse blob = githubWebClient.post()
                    .uri("/repos/{owner}/{repo}/git/blobs", owner, repo)
                    .bodyValue(GithubBlobRequest.utf8(content))
                    .retrieve()
                    .bodyToMono(GithubBlobResponse.class)
                    .block();
            if (blob == null) {
                throw new GithubApiException("Empty response creating blob");
            }
            return blob.sha();
        } catch (WebClientResponseException ex) {
            throw new GithubApiException("Failed to create blob: " + ex.getStatusCode() + " " + ex.getResponseBodyAsString());
        }
    }

    @Override
    public GithubTreeResponse createTree(String owner, String repo, String baseTreeSha, List<GithubTreeEntry> entries) {
        try {
            GithubTreeResponse tree = githubWebClient.post()
                    .uri("/repos/{owner}/{repo}/git/trees", owner, repo)
                    .bodyValue(new GithubCreateTreeRequest(baseTreeSha, entries))
                    .retrieve()
                    .bodyToMono(GithubTreeResponse.class)
                    .block();
            if (tree == null) {
                throw new GithubApiException("Empty response creating tree");
            }
            return tree;
        } catch (WebClientResponseException ex) {
            throw new GithubApiException("Failed to create tree: " + ex.getStatusCode() + " " + ex.getResponseBodyAsString());
        }
    }

    @Override
    public GithubCommitResponse createCommit(String owner, String repo, String message, String treeSha, String parentCommitSha) {
        try {
            GithubCommitResponse commit = githubWebClient.post()
                    .uri("/repos/{owner}/{repo}/git/commits", owner, repo)
                    .bodyValue(new GithubCreateCommitRequest(message, treeSha, List.of(parentCommitSha)))
                    .retrieve()
                    .bodyToMono(GithubCommitResponse.class)
                    .block();
            if (commit == null) {
                throw new GithubApiException("Empty response creating commit");
            }
            return commit;
        } catch (WebClientResponseException ex) {
            throw new GithubApiException("Failed to create commit: " + ex.getStatusCode() + " " + ex.getResponseBodyAsString());
        }
    }

    @Override
    public void updateRef(String owner, String repo, String branch, String commitSha) {
        try {
            githubWebClient.patch()
                    .uri("/repos/{owner}/{repo}/git/refs/heads/{branch}", owner, repo, branch)
                    .bodyValue(new GithubUpdateRefRequest(commitSha, false))
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
        } catch (WebClientResponseException ex) {
            throw new GithubApiException("Failed to update ref for branch " + branch + ": " + ex.getStatusCode() + " " + ex.getResponseBodyAsString());
        }
    }
}
