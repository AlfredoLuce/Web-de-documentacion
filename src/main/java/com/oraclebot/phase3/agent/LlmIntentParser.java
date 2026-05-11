package com.oraclebot.phase3.agent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oraclebot.phase3.config.AiProps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class LlmIntentParser implements IntentParser {

    private static final Logger logger = LoggerFactory.getLogger(LlmIntentParser.class);

    private final AiProps aiProps;
    private final ObjectMapper objectMapper;
    private final RuleBasedIntentParser fallbackParser;

    public LlmIntentParser(AiProps aiProps, ObjectMapper objectMapper, RuleBasedIntentParser fallbackParser) {
        this.aiProps = aiProps;
        this.objectMapper = objectMapper;
        this.fallbackParser = fallbackParser;
    }

    public String askGeneral(String userMessage) {
        if (!aiProps.isEnabled() || aiProps.getApiKey() == null || aiProps.getApiKey().isBlank()) {
            return "No pude interpretar la solicitud. Escribe ayuda para ver ejemplos.";
        }
        try {
            RestClient client = RestClient.builder()
                    .baseUrl(aiProps.getBaseUrl())
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + aiProps.getApiKey())
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build();

            Map<String, Object> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage == null ? "" : userMessage); // guardia

            Map<String, Object> payload = new HashMap<>();
            payload.put("model", aiProps.getModel());
            payload.put("messages", List.of(
                    Map.of("role", "system", "content",
                            """
                                    Eres un asistente especializado. Solo puedes responder preguntas sobre:
                                    1. Gestion agil de proyectos (Scrum, Kanban, sprints, tareas, story points, metodologias agiles).
                                    2. La receta de un buen brownie.
                                    Si el usuario pregunta sobre cualquier otro tema, responde exactamente:
                                    "Solo puedo ayudarte con temas de gestion agil del proyecto o con la receta del brownie."
                                    No respondas ninguna otra pregunta fuera de esos dos temas, sin excepciones.
                                    Responde en español."""),
                    userMsg));
            payload.put("temperature", 0.7);

            String responseBody = client.post()
                    .uri("/chat/completions")
                    .body(payload)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode content = root.path("choices").path(0).path("message").path("content");
            if (content.isMissingNode() || content.asText().isBlank()) {
                return "No obtuve respuesta del modelo.";
            }

            return content.asText()
                    .replaceAll("(?s)<think>.*?</think>", "")
                    .replaceAll("(?s)```[a-zA-Z]*\\s*", "")
                    .trim();
        } catch (Exception ex) {
            logger.warn("Fallo la consulta general LLM: {} - {}", ex.getClass().getSimpleName(), ex.getMessage());
            return "Ocurrio un error al consultar el modelo. Intenta de nuevo.";
        }
    }

    @Override
    public ParsedIntent parse(String messageText) {
        if (!aiProps.isEnabled() || aiProps.getApiKey() == null || aiProps.getApiKey().isBlank()) {
            return fallbackParser.parse(messageText);
        }

        try {
            RestClient client = RestClient.builder()
                    .baseUrl(aiProps.getBaseUrl())
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + aiProps.getApiKey())
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build();

            String systemPrompt = """
                    Eres un clasificador de intenciones para un asistente de gestion agile.
                    Debes responder solo JSON valido.
                    Intenciones permitidas:
                    HELP
                    LIST_TASKS
                    LIST_TASKS_BY_ASSIGNEE
                    LIST_TASKS_BY_STATUS
                    CREATE_TASK
                    CURRENT_SPRINT_SUMMARY
                    TEAM_LOAD_SUMMARY
                    UNKNOWN

                    Devuelve JSON con:
                    intent, assignee, status, title, storyPoints, sprintName, clarificationNeeded, clarificationQuestion.
                    Si falta informacion importante, pide aclaracion.
                    """;

            Map<String, Object> payload = Map.of(
                    "model", aiProps.getModel(),
                    "messages", List.of(
                            Map.of("role", "system", "content", systemPrompt),
                            Map.of("role", "user", "content", messageText)),
                    "temperature", 0);

            String responseBody = client.post()
                    .uri("/chat/completions")
                    .body(payload)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode content = root.path("choices").path(0).path("message").path("content");
            if (content.isMissingNode() || content.asText().isBlank()) {
                return fallbackParser.parse(messageText);
            }

            // Strip <think>...</think> blocks (Qwen3 thinking mode) and markdown code
            // fences
            String rawContent = content.asText()
                    .replaceAll("(?s)<think>.*?</think>", "")
                    .replaceAll("(?s)```[a-zA-Z]*\\s*", "")
                    .trim();
            return objectMapper.readValue(rawContent, ParsedIntent.class);
        } catch (Exception ex) {
            logger.warn("Fallo el parser LLM [url={}, model={}]: {} - {}. Uso fallback local.",
                    aiProps.getBaseUrl(), aiProps.getModel(), ex.getClass().getSimpleName(), ex.getMessage());
            return fallbackParser.parse(messageText);
        }
    }
}
