package com.example.jiraagent.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

@Service
public class JiraAgentService {

    private final ChatClient chatClient;

    public JiraAgentService(ChatClient.Builder builder, ToolCallbackProvider tools) {

        if (tools == null) {
            System.out.println("❌ ToolCallbackProvider is NULL");
        } else {
            var callbacks = tools.getToolCallbacks();

            if (callbacks.length == 0) {
                System.out.println("⚠️ No MCP tools found!");
            } else {
                System.out.println("✅ MCP tools found: {}"+ callbacks.length);

                for (var callback : callbacks) {
                    System.out.println("🔧 Tool: {}"+ callback.getToolDefinition().name());
                }
            }
        }

        this.chatClient = builder
                .defaultToolCallbacks(tools)
                .defaultSystem("""
                        You are a helpful assistant with access to Jira.
                        When asked about a Jira ticket, use the available tools to fetch
                        the ticket details and provide a clear, structured summary.
                        """)
                .build();
    }


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
} // <-- Properly cl osing the class here