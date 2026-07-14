package com.example.jiraagent.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

@Service
public class JiraAgentService {

    private final ChatClient chatClient;

    public JiraAgentService(ChatClient.Builder builder, ToolCallbackProvider tools) {
        this.chatClient = builder
                .defaultToolCallbacks(tools)
                .defaultSystem("""
                        You are a helpful assistant with access to Jira.
                        When asked about a Jira ticket, use the available tools to fetch
                        the ticket details and provide a clear, structured summary.
                        """)
                .build();
    } // <-- Properly closing the constructor here

    /**
     * Fetches and summarizes a Jira ticket by key (e.g. "PROJ-123").
     */
    public String readTicket(String ticketKey) {
        return chatClient.prompt()
                .user("Fetch the Jira ticket " + ticketKey + " and provide a structured summary including: "
                        + "title, status, priority, assignee, reporter, description, and any comments.")
                .call()
                .content();
    }

    /**
     * Runs a free-form query against Jira (e.g. search, list, etc.).
     */
    public String query(String userMessage) {
        return chatClient.prompt()
                .user(userMessage)
                .call()
                .content();
    }
} // <-- Properly closing the class here