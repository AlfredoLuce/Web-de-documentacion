# Cambios de UI

Este documento resume los ajustes visuales aplicados a la interfaz web con un objetivo claro:

- proyectar una imagen mas profesional
- mantener una experiencia simple, ordenada y facil de usar

## Objetivo de diseno

Se priorizo que la UI fuera clara en 3 niveles:

1. jerarquia visual (que se entiende primero)
2. legibilidad (campos y mensajes faciles de leer)
3. simplicidad operativa (acciones directas, menos ruido)

## Resumen de cambios aplicados

### 1) Jerarquia de la pantalla

Que se cambio:

- El panel para agregar tareas y listarlas se puso como foco principal de la pantalla
- el chat se llevo a un panel lateral derecho como asistente

Por que se aplico:

- la tarea principal del usuario es gestionar trabajo
- el chat me pareció mejor como un complemento ya que esta parte la veo más para agregar y listar tareas

Archivos:

- src/main/resources/static/index.html
- src/main/resources/static/styles.css

### 2) Formularios mas ordenados

Que se cambio:

- se agregaron labels visibles
- se agruparon campos por bloques de formulario
- se mejoraron placeholders con ejemplos
- se ajustaron estados de foco y estilos de entrada

Por que se aplico:

- reduce errores al capturar datos
- mejora la lectura y la sensacion de calidad visual
- conserva una interaccion simple para el usuario

Archivos:

- src/main/resources/static/index.html
- src/main/resources/static/styles.css

### 3) Fondo y paleta mas profesional

Que se cambio:

- se reemplazo la paleta inicial por tonos azul/gris neutros
- se suavizaron sombras, bordes y contrastes

Por que se aplico:

- da una identidad mas sobria y corporativa
- evita un look recargado o informal

Archivo:

- src/main/resources/static/styles.css

### 4) Chat con estructura de mensajeria

Que se cambio:

- historial y caja de envio se integraron en una sola ventana de chat
- el composer se ubico al final del panel como en apps de mensajeria
- las burbujas se separaron por rol (usuario/asistente)

Por que se aplico:

- buscaba un patron visual más familiar (tipo Telegram/WhatsApp)
- la conversacion se percibe mas natural y fluida

Archivos:

- src/main/resources/static/index.html
- src/main/resources/static/styles.css

### 5) Mensajes compactos y ajustados al contenido

Que se cambio:

- burbujas con ancho por contenido y maximo controlado
- alineacion de burbujas por rol para evitar estiramientos
- reduccion de espacio entre mensajes
- normalizacion de saltos de linea excesivos en render

Por que se aplico:

- evita burbujas visualmente grandes para mensajes cortos

Archivos:

- src/main/resources/static/styles.css
- src/main/resources/static/app.js

### 6) Lista de tareas en columna unica

Que se cambio:

- se paso de distribucion multi-columna a lista vertical unica

Por que se aplico:

- facilita lectura secuencial
- mejora foco en cada tarea y su estado

Archivo:

- src/main/resources/static/styles.css

### 7) Renderizado conversacional del chat (typing)

Que se cambio:

- se agrego una burbuja temporal del asistente en estado "escribiendo" con animacion de 3 puntos
- cuando llega la respuesta del backend, esa misma burbuja se reutiliza y el texto aparece letra por letra

Por que se aplico:

- hace que la conversacion se sienta mas natural y cercana
- comunica al usuario que el sistema esta procesando la respuesta
- reduce la sensacion de salto brusco entre enviar mensaje y recibir respuesta

Archivos:

- src/main/resources/static/app.js
- src/main/resources/static/styles.css

## Resultado esperado

Con estos ajustes, la aplicacion mantiene su misma funcionalidad, pero con una presentacion mas profesional y simple:

- interfaz visualmente mas limpia
- formularios mas claros
- chat mas familiar para el usuario
- tareas mas faciles de recorrer

## Alcance tecnico

Estos cambios fueron de presentacion (HTML/CSS y ajuste de render de texto en frontend).
No se modifico la logica de negocio ni los endpoints del backend.
