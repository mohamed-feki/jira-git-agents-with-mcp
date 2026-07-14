package com.agentic.developeragent.service.impl;

import com.agentic.developeragent.dto.GeneratedFile;
import com.agentic.developeragent.exception.CodeGenerationException;
import com.agentic.developeragent.service.ResponseParserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses the LLM's raw text response into a list of {@link GeneratedFile}.
 * The prompt instructs the model to reply with a single JSON object of the
 * shape {@code {"files":[{"filename":..,"path":..,"content":..}]}}. This
 * parser is defensive: it strips markdown code fences if the model adds
 * them anyway, and it extracts the outermost JSON object if there is
 * stray text around it.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResponseParserServiceImpl implements ResponseParserService {

    private static final Pattern CODE_FENCE = Pattern.compile("```(?:json)?\\s*([\\s\\S]*?)```", Pattern.MULTILINE);

    private final ObjectMapper objectMapper;

    @Override
    public List<GeneratedFile> parse(String rawLlmResponse) {
        String jsonCandidate = extractJson(rawLlmResponse);

        try {
            JsonNode root = objectMapper.readTree(jsonCandidate);
            JsonNode filesNode = root.get("files");
            if (filesNode == null || !filesNode.isArray() || filesNode.isEmpty()) {
                throw new CodeGenerationException("LLM response did not contain a non-empty 'files' array");
            }

            List<GeneratedFile> files = new ArrayList<>();
            for (JsonNode fileNode : filesNode) {
                String filename = textOrNull(fileNode, "filename");
                String path = textOrNull(fileNode, "path");
                String content = textOrNull(fileNode, "content");

                if (path == null || content == null) {
                    throw new CodeGenerationException("Generated file entry missing required 'path' or 'content'");
                }
                if (filename == null) {
                    filename = derivedFilename(path);
                }
                files.add(new GeneratedFile(filename, path, content));
            }
            return files;
        } catch (CodeGenerationException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Failed to parse LLM response as JSON. Raw response: {}", rawLlmResponse);
            throw new CodeGenerationException("Failed to parse LLM response as JSON: " + ex.getMessage(), ex);
        }
    }

    private String extractJson(String raw) {
        String trimmed = raw.trim();

        Matcher fenceMatcher = CODE_FENCE.matcher(trimmed);
        if (fenceMatcher.find()) {
            trimmed = fenceMatcher.group(1).trim();
        }

        int firstBrace = trimmed.indexOf('{');
        int lastBrace = trimmed.lastIndexOf('}');
        if (firstBrace == -1 || lastBrace == -1 || lastBrace < firstBrace) {
            throw new CodeGenerationException("LLM response does not contain a JSON object");
        }
        return trimmed.substring(firstBrace, lastBrace + 1);
    }

    private String textOrNull(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return (value != null && !value.isNull()) ? value.asText() : null;
    }

    private String derivedFilename(String path) {
        int lastSlash = path.lastIndexOf('/');
        return lastSlash >= 0 ? path.substring(lastSlash + 1) : path;
    }
}
