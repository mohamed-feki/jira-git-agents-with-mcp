package com.agentic.developeragent.service;

import com.agentic.developeragent.dto.GeneratedFile;
import com.agentic.developeragent.exception.CodeGenerationException;
import com.agentic.developeragent.service.impl.ResponseParserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResponseParserServiceImplTest {

    private final ResponseParserServiceImpl parser = new ResponseParserServiceImpl(new ObjectMapper());

    @Test
    void parse_parsesPlainJsonResponse() {
        String raw = """
                {
                  "files": [
                    { "filename": "Foo.java", "path": "src/main/java/com/example/Foo.java", "content": "class Foo {}" }
                  ]
                }
                """;

        List<GeneratedFile> files = parser.parse(raw);

        assertThat(files).hasSize(1);
        assertThat(files.get(0).filename()).isEqualTo("Foo.java");
        assertThat(files.get(0).path()).isEqualTo("src/main/java/com/example/Foo.java");
        assertThat(files.get(0).content()).isEqualTo("class Foo {}");
    }

    @Test
    void parse_stripsMarkdownCodeFences() {
        String raw = """
                Here is the code:
                ```json
                { "files": [ { "path": "src/main/java/Foo.java", "content": "class Foo {}" } ] }
                ```
                """;

        List<GeneratedFile> files = parser.parse(raw);

        assertThat(files).hasSize(1);
        assertThat(files.get(0).filename()).isEqualTo("Foo.java");
    }

    @Test
    void parse_throwsCodeGenerationException_whenNoJsonPresent() {
        assertThatThrownBy(() -> parser.parse("no json here"))
                .isInstanceOf(CodeGenerationException.class);
    }

    @Test
    void parse_throwsCodeGenerationException_whenFilesArrayMissing() {
        assertThatThrownBy(() -> parser.parse("{\"notFiles\": []}"))
                .isInstanceOf(CodeGenerationException.class);
    }
}
