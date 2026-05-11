# Telegram Agent UI Phase 3

Tercera variante del ejemplo didactico.

Esta version toma como base el sistema agentico de `telegram-agent-phase2` y agrega una interfaz web para:

- gestionar tareas desde navegador
- consultar el sistema desde un panel de chat
- reutilizar el mismo asistente que responde en Telegram

## Objetivo

Mostrar una evolucion completa:

1. bot con comandos
2. bot agente con lenguaje natural
3. sistema multicanal: Telegram + UI web + asistente web

## Canales incluidos

- Telegram: mensajes al bot
- Web UI: lista y alta de tareas
- Web Chat: asistente que consulta y actua sobre el sistema

## Arquitectura

- `bot/TelegramAgentBot.java`: canal Telegram
- `agent/AgentOrchestrator.java`: cerebro del asistente
- `service/InMemoryProjectWorkspaceService.java`: dominio demo
- `controller/TaskController.java`: API REST de tareas
- `controller/AssistantController.java`: API REST del chat
- `src/main/resources/static/index.html`: UI web
- `src/main/resources/static/app.js`: frontend de tareas y chat
- `src/main/resources/static/styles.css`: estilos

## Que demuestra

- un mismo orquestador puede servir a multiples interfaces
- Telegram no tiene que ser el unico canal del bot
- la UI web y el bot pueden compartir servicios y reglas

## Configuracion

Entrar al proyecto:

```bash
cd starter/telegram-agent-ui-phase3
```

Copiar propiedades:

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Editar:

```properties
telegram.bot.name=MiOraclePlannerBot
telegram.bot.token=TU_TOKEN
```

Opcional para parser con LLM:

```properties
agent.ai.enabled=true
agent.ai.base-url=https://api.openai.com/v1
agent.ai.api-key=TU_API_KEY
agent.ai.model=gpt-4o-mini
```

## Ejecutar localmente

```bash
mvn spring-boot:run
```

## Probar

- UI web: [http://localhost:8080](http://localhost:8080)
- Telegram: escribir al bot

## Flujo recomendado en clase

1. crear tareas desde la UI
2. consultarlas desde el chat web
3. consultarlas tambien desde Telegram
4. explicar que ambos canales usan el mismo `AgentOrchestrator`

## Siguiente evolucion

Reemplazar la implementacion en memoria por una capa real conectada al backend y la base de datos del proyecto.

