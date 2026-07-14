package com.agentic.githubagent.service.impl;

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
import com.agentic.githubagent.service.GithubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Orchestrates a single atomic commit that creates or updates multiple
 * files, using the GitHub "Git Data" API:
 *
 *   1. Resolve (or create) the target branch and its head commit.
 *   2. Create a git blob for each file's content.
 *   3. Create a new tree on top of the current tree, referencing the blobs.
 *   4. Create a new commit pointing at the new tree, with the previous
 *      head commit as its parent.
 *   5. Fast-forward the branch ref to the new commit (push).
 *
 * This class is a stateless singleton: all per-request data (owner, repo)
 * is passed explicitly rather than stored on instance fields, so it is
 * safe under concurrent requests.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GithubServiceImpl implements GithubService {

    private final GithubClient githubClient;
    private final GithubProperties githubProperties;

    @Override
    public PushResponse push(PushRequest request) {
        String owner = extractOwner(request.repository());
        String repoName = extractRepoName(request.repository());

        log.info("Pushing {} file(s) to {}/{} on branch '{}'", request.files().size(), owner, repoName, request.branch());

        GithubRepositoryResponse repository = githubClient.getRepository(owner, repoName);
        GithubRefResponse branchRef = githubClient.getBranchRef(owner, repoName, request.branch());
        String parentCommitSha = branchRef.object().sha();

        List<GithubTreeEntry> treeEntries = request.files().stream()
                .map(file -> toTreeEntry(owner, repoName, file))
                .toList();

        GithubTreeResponse tree = githubClient.createTree(owner, repoName, parentCommitSha, treeEntries);
        GithubCommitResponse commit = githubClient.createCommit(owner, repoName, request.commitMessage(), tree.sha(), parentCommitSha);
        githubClient.updateRef(owner, repoName, request.branch(), commit.sha());

        log.info("Pushed commit {} to {}/{}@{}", commit.sha(), owner, repoName, request.branch());

        return new PushResponse(
                commit.htmlUrl(),
                commit.sha(),
                repository.htmlUrl(),
                request.files().size(),
                request.branch()
        );
    }

    private GithubTreeEntry toTreeEntry(String owner, String repoName, FileEntry file) {
        String blobSha = githubClient.createBlob(owner, repoName, file.content());
        return GithubTreeEntry.blob(file.path(), blobSha);
    }

    /**
     * Accepts either "owner/repo" or just "repo" (falling back to the
     * configured default owner).
     */
    private String extractOwner(String repository) {
        if (repository.contains("/")) {
            return repository.substring(0, repository.indexOf('/'));
        }
        if (githubProperties.owner() == null || githubProperties.owner().isBlank()) {
            throw new IllegalArgumentException(
                    "repository '" + repository + "' does not specify an owner and no default github.owner is configured");
        }
        return githubProperties.owner();
    }

    private String extractRepoName(String repository) {
        if (repository.contains("/")) {
            return repository.substring(repository.indexOf('/') + 1);
        }
        return repository;
    }
}
