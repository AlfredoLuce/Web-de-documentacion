package com.oraclebot.phase3.agent;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class RuleBasedIntentParser implements IntentParser {

    private static final Pattern CREATE_TASK_PATTERN =
        Pattern.compile("crea(?:r)? una tarea para (.+?)(?: y asigna(?:la)? a ([a-zA-ZáéíóúÁÉÍÓÚñÑ ]+))?(?: con (\\d+) puntos?)?$", Pattern.CASE_INSENSITIVE);

    @Override
    public ParsedIntent parse(String messageText) {
        String text = messageText == null ? "" : messageText.trim();
        String normalized = text.toLowerCase(Locale.ROOT);
        ParsedIntent intent = new ParsedIntent();

        if (normalized.equals("/start") || normalized.equals("ayuda") || normalized.equals("/help")) {
            intent.setIntent(IntentType.HELP);
            return intent;
        }

        if (normalized.contains("sprint actual") || normalized.contains("como va el sprint")) {
            intent.setIntent(IntentType.CURRENT_SPRINT_SUMMARY);
            return intent;
        }

        if (normalized.contains("quien tiene mas carga") || normalized.contains("carga del equipo")) {
            intent.setIntent(IntentType.TEAM_LOAD_SUMMARY);
            return intent;
        }

        if (normalized.contains("tareas tiene ")) {
            intent.setIntent(IntentType.LIST_TASKS_BY_ASSIGNEE);
            intent.setAssignee(capitalize(normalized.substring(normalized.indexOf("tareas tiene ") + "tareas tiene ".length()).trim()));
            return intent;
        }

        if (normalized.contains("tareas pendientes") || normalized.contains("tareas siguen ") || normalized.contains("tareas done")) {
            intent.setIntent(IntentType.LIST_TASKS_BY_STATUS);
            if (normalized.contains("done")) {
                intent.setStatus("DONE");
            } else if (normalized.contains("progreso")) {
                intent.setStatus("IN_PROGRESS");
            } else {
                intent.setStatus("PENDING");
            }
            return intent;
        }

        if (normalized.equals("/todolist") || normalized.equals("lista de tareas")) {
            intent.setIntent(IntentType.LIST_TASKS);
            return intent;
        }

        Matcher matcher = CREATE_TASK_PATTERN.matcher(text);
        if (matcher.find()) {
            intent.setIntent(IntentType.CREATE_TASK);
            intent.setTitle(matcher.group(1) == null ? null : matcher.group(1).trim());
            intent.setAssignee(matcher.group(2) == null ? null : capitalize(matcher.group(2).trim()));
            intent.setStoryPoints(matcher.group(3) == null ? null : Integer.parseInt(matcher.group(3)));
            return intent;
        }

        intent.setIntent(IntentType.UNKNOWN);
        intent.setClarificationNeeded(true);
        intent.setClarificationQuestion("No entendi la solicitud. Prueba con ayuda, consulta de tareas o resumen del sprint.");
        return intent;
    }

    private String capitalize(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }
        return value.substring(0, 1).toUpperCase(Locale.ROOT) + value.substring(1);
    }
}

