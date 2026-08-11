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

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Jira Controller is working!");
    }

    @GetMapping("/{key}")
    public ResponseEntity<String> getTicket(@PathVariable String key) {

        System.out.println(">>> Request received for ticket: " + key);

        String result = agentService.readTicket(key);

        System.out.println(">>> AI result: " + result);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/query")
    public ResponseEntity<String> query(@RequestBody QueryRequest request) {
        String result = agentService.query(request.message());
        return ResponseEntity.ok(result);
    }

    public record QueryRequest(String message) {}
}