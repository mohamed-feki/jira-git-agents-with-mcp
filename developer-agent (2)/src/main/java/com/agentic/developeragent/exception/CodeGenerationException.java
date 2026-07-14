package com.agentic.developeragent.exception;

/**
 * Raised when the LLM call fails or its response cannot be parsed into
 * a valid set of generated files.
 */
public class CodeGenerationException extends RuntimeException {
    public CodeGenerationException(String message) {
        super(message);
    }

    public CodeGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
