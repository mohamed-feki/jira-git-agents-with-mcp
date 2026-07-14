package com.example.jiraagent.controller;

import com.example.jiraagent.service.JiraAgentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tickets")
public class JiraController {

    private final JiraAgentService agentService;

    public JiraController(JiraAgentService agentService) {
        this.agentService = agentService;
    }

    /**
     * GET /tickets/{key}
     * Fetches and summarizes a single Jira ticket.
     * Example: GET /tickets/PROJ-123
     */
    @GetMapping("/{key}")
    public ResponseEntity<String> getTicket(@PathVariable String key) {
        String result = agentService.readTicket(key);
        return ResponseEntity.ok(result);
    }

    /**
     * POST /tickets/query
     * Free-form natural language query against Jira.
     * Body: { "message": "List all open bugs assigned to me" }
     */
    @PostMapping("/query")
    public ResponseEntity<String> query(@RequestBody QueryRequest request) {
        String result = agentService.query(request.message());
        return ResponseEntity.ok(result);
    }

    public record QueryRequest(String message) {}
}
