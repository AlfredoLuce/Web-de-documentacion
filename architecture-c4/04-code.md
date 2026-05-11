# 04. Code View

## Vista de codigo

El diseño de codigo de fase 3 extiende la fase 2 con controladores REST, DTOs y una UI estatica.

## Diagrama de clases

```mermaid
classDiagram
    class TelegramAgentUiPhase3Application
    class TelegramAgentBot
    class TaskController
    class AssistantController
    class AgentOrchestrator
    class IntentParser {
        <<interface>>
    }
    class LlmIntentParser
    class RuleBasedIntentParser
    class ParsedIntent
    class IntentType {
        <<enum>>
    }
    class ProjectWorkspaceService {
        <<interface>>
    }
    class InMemoryProjectWorkspaceService
    class TaskItem
    class SprintInfo
    class CreateTaskRequest
    class ChatRequest
    class ChatResponse

    TelegramAgentBot --> AgentOrchestrator
    TaskController --> ProjectWorkspaceService
    AssistantController --> AgentOrchestrator
    AgentOrchestrator --> LlmIntentParser
    AgentOrchestrator --> ProjectWorkspaceService
    LlmIntentParser ..|> IntentParser
    RuleBasedIntentParser ..|> IntentParser
    LlmIntentParser --> RuleBasedIntentParser
    LlmIntentParser --> ParsedIntent
    RuleBasedIntentParser --> ParsedIntent
    ParsedIntent --> IntentType
    InMemoryProjectWorkspaceService ..|> ProjectWorkspaceService
    InMemoryProjectWorkspaceService --> TaskItem
    InMemoryProjectWorkspaceService --> SprintInfo
    TaskController --> CreateTaskRequest
    AssistantController --> ChatRequest
    AssistantController --> ChatResponse
```

## Diagrama de secuencia: crear tarea desde web

```mermaid
sequenceDiagram
    actor U as Usuario web
    participant UI as app.js
    participant TC as TaskController
    participant WS as InMemoryProjectWorkspaceService

    U->>UI: submit formulario
    UI->>TC: POST /api/tasks
    TC->>WS: createTask(title, assignee, pts, sprint)
    WS-->>TC: TaskItem
    TC-->>UI: JSON TaskItem
    UI->>TC: GET /api/tasks
    TC->>WS: findAllTasks()
    WS-->>TC: List<TaskItem>
    TC-->>UI: JSON lista
```

## Diagrama de secuencia: chat web

```mermaid
sequenceDiagram
    actor U as Usuario web
    participant UI as app.js
    participant AC as AssistantController
    participant O as AgentOrchestrator
    participant P as LlmIntentParser
    participant W as InMemoryProjectWorkspaceService

    U->>UI: pregunta en chat
    UI->>AC: POST /api/assistant/chat
    AC->>O: handleMessage(message)
    O->>P: parse(message)
    P-->>O: ParsedIntent
    O->>W: consulta o accion
    W-->>O: resultado
    O-->>AC: respuesta textual
    AC-->>UI: ChatResponse
```

## Diagrama de secuencia: estado compartido entre canales

```mermaid
sequenceDiagram
    actor WU as Usuario web
    actor TU as Usuario Telegram
    participant UI as UI web
    participant TC as TaskController
    participant BOT as TelegramAgentBot
    participant ORCH as AgentOrchestrator
    participant WS as InMemoryProjectWorkspaceService

    WU->>UI: crea tarea
    UI->>TC: POST /api/tasks
    TC->>WS: createTask(...)
    WS-->>TC: TaskItem

    TU->>BOT: "lista de tareas"
    BOT->>ORCH: handleMessage(...)
    ORCH->>WS: findAllTasks()
    WS-->>ORCH: lista actualizada
    ORCH-->>BOT: respuesta con tarea creada en web
```

## Mapeo codigo -> capas

| Capa | Elementos | Comentario |
|---|---|---|
| Bootstrap | `TelegramAgentUiPhase3Application` | inicializa todo el sistema |
| Canal Telegram | `TelegramAgentBot` | integra mensajeria Telegram |
| API REST | `TaskController`, `AssistantController`, DTOs | expone tareas y chat |
| Aplicacion | `AgentOrchestrator` | orquesta intenciones y dominio |
| NLU | `IntentParser`, `LlmIntentParser`, `RuleBasedIntentParser`, `ParsedIntent`, `IntentType` | interpreta lenguaje natural |
| Dominio | `ProjectWorkspaceService`, `TaskItem`, `SprintInfo` | herramientas y entidades del proyecto |
| Persistencia demo | `InMemoryProjectWorkspaceService` | estado compartido |
| Frontend | `index.html`, `app.js`, `styles.css` | interfaz de usuario |

## Puntos de extension

- reemplazar la UI vanilla por un frontend desacoplado
- exponer mas endpoints REST
- reutilizar `AgentOrchestrator` en otros canales como Slack o WhatsApp
- cambiar `ProjectWorkspaceService` por backend real
