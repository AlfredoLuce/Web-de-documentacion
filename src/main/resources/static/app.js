const taskList = document.getElementById("task-list");
const taskForm = document.getElementById("task-form");
const refreshTasksButton = document.getElementById("refresh-tasks");
const chatMessages = document.getElementById("chat-messages");
const chatForm = document.getElementById("chat-form");
const chatInput = document.getElementById("chat-input");

async function fetchTasks() {
  if (!taskList) {
    return;
  }

  taskList.innerHTML = "<p>Cargando tareas...</p>";

  try {
    const response = await fetch("/api/tasks");
    if (!response.ok) {
      throw new Error(`HTTP ${response.status}`);
    }

    const tasks = await response.json();
    renderTasks(Array.isArray(tasks) ? tasks : []);
  } catch (error) {
    taskList.innerHTML = "<p>No fue posible cargar la lista de tareas.</p>";
    console.error("No se pudieron cargar las tareas:", error);
  }
}

function renderTasks(tasks) {
  if (!taskList) {
    return;
  }

  taskList.innerHTML = "";

  if (!tasks.length) {
    taskList.innerHTML = "<p>No hay tareas registradas.</p>";
    return;
  }

  tasks.forEach((task) => {
    const card = document.createElement("article");
    card.className = "task-card";
    const badgeClass = task.status === "DONE" ? "done" : task.status === "IN_PROGRESS" ? "progress" : "pending";

    card.innerHTML = `
      <h3>${escapeHtml(task.title)}</h3>
      <div class="task-meta">
        <span class="badge ${badgeClass}">${escapeHtml(task.status)}</span>
        <span>Responsable: ${escapeHtml(task.assignee)}</span>
        <span>${task.storyPoints} pts</span>
        <span>${escapeHtml(task.sprintName)}</span>
        <span>Entrega: ${escapeHtml(task.dueDate)}</span>
      </div>
    `;
    taskList.appendChild(card);
  });
}

if (taskForm) {
  taskForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const payload = {
      title: document.getElementById("title").value.trim(),
      assignee: document.getElementById("assignee").value.trim(),
      storyPoints: Number(document.getElementById("storyPoints").value || 3),
      sprintName: document.getElementById("sprintName").value.trim(),
    };

    await fetch("/api/tasks", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });

    taskForm.reset();
    await fetchTasks();
  });
}

if (refreshTasksButton) {
  refreshTasksButton.addEventListener("click", fetchTasks);
}

if (chatForm) {
  chatForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    if (!chatInput) {
      return;
    }

    const message = chatInput.value.trim();
    if (!message) return;

    appendMessage("user", message);
    chatInput.value = "";

    const typingBubble = appendTypingBubble();

    try {
      const response = await fetch("/api/assistant/chat", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ message }),
      });

      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
      }

      const payload = await response.json();
      await typeAssistantMessage(typingBubble, payload.response);
      await fetchTasks();
    } catch (error) {
      console.error("No se pudo responder el chat:", error);
      setAssistantMessageText(typingBubble, "No pude responder en este momento.");
    }
  });
}

document.querySelectorAll(".hint").forEach((button) => {
  button.addEventListener("click", () => {
    if (!chatInput) {
      return;
    }

    chatInput.value = button.dataset.message;
    chatInput.focus();
  });
});

function appendMessage(role, text) {
  if (!chatMessages) {
    return null;
  }

  const item = document.createElement("div");
  item.className = `message ${role}`;
  item.textContent = normalizeMessageText(text);
  chatMessages.appendChild(item);
  chatMessages.scrollTop = chatMessages.scrollHeight;
  return item;
}

function appendTypingBubble() {
  if (!chatMessages) {
    return null;
  }

  const item = document.createElement("div");
  item.className = "message assistant thinking";
  item.innerHTML = "<span class=\"typing-dots\" aria-label=\"Escribiendo\"><span></span><span></span><span></span></span>";
  chatMessages.appendChild(item);
  chatMessages.scrollTop = chatMessages.scrollHeight;
  return item;
}

function setAssistantMessageText(messageElement, text) {
  if (!messageElement) {
    return;
  }

  messageElement.classList.remove("thinking");
  messageElement.classList.remove("is-typing");
  messageElement.textContent = normalizeMessageText(text);
  if (chatMessages) {
    chatMessages.scrollTop = chatMessages.scrollHeight;
  }
}

async function typeAssistantMessage(messageElement, text) {
  if (!messageElement) {
    return;
  }

  const content = normalizeMessageText(text);
  messageElement.classList.remove("thinking");
  messageElement.classList.add("is-typing");
  messageElement.textContent = "";

  for (const char of content) {
    messageElement.textContent += char;
    if (chatMessages) {
      chatMessages.scrollTop = chatMessages.scrollHeight;
    }

    const pause = /[.,!?;:\n]/.test(char) ? 35 : 14;
    await new Promise((resolve) => setTimeout(resolve, pause));
  }

  messageElement.classList.remove("is-typing");
}

function normalizeMessageText(text) {
  return String(text ?? "")
    .replace(/\r\n/g, "\n")
    .replace(/\n{3,}/g, "\n\n")
    .trim();
}

function escapeHtml(value) {
  return String(value ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#39;");
}

appendMessage("assistant", "Hola. Puedo ayudarte con tareas, sprint y carga del equipo.");
fetchTasks();

