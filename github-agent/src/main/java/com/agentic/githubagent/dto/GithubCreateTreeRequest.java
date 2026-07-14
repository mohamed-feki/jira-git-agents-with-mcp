package com.agentic.githubagent.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GithubCreateTreeRequest(
        @JsonProperty("base_tree") String baseTree,
        List<GithubTreeEntry> tree
) {
}
