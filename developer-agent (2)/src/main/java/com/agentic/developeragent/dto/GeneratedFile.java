package com.agentic.developeragent.dto;

/**
 * A single generated source file, ready to be committed by the GitHub Agent.
 *
 * @param filename the file's base name, e.g. "OrderController.java"
 * @param path     the relative repository path, e.g. "src/main/java/com/example/controller/OrderController.java"
 * @param content  the full file content
 */
public record GeneratedFile(
        String filename,
        String path,
        String content
) {
}
