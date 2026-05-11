package com.oraclebot.phase3.controller;

import com.oraclebot.phase3.agent.AgentOrchestrator;
import com.oraclebot.phase3.dto.ChatRequest;
import com.oraclebot.phase3.dto.ChatResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/assistant")
public class AssistantController {

    private final AgentOrchestrator orchestrator;

    public AssistantController(AgentOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        return new ChatResponse(orchestrator.handleMessage(request.getMessage()));
    }
}

