# Advisor

Un advisor en Spring AI es como un interceptor alrededor de la llamada al modelo.
Cada Advisor implementa ```CallAdvisor```(no streaming) o ```StreamAdvisor```(streaming),
con 2 metodos claves: uno que modifica el request antes de llegar al modelo y otro que procesa 
el modelo y otro que procesa la respuesta antes de devolverla. 


Core Conceptos de Memoria


### 1 - ChatMemory
 Define que recordar de una conversacion.
 Ejemplo de strategia:
   - Guarde los últimos N mensajes
   - Almacene los mensajes dentro de una ventana de tiempo
   - Retener mensajes hasta que se alcance un límite de token

   ```MensajeWindowChatMemory```
  

### 2 - ChatMemoryRepository
  Define como agregar los mensajes en la base de datos.
  Spring AI provee multiples implementaciones:

    InMemoryChatMemoryRepository (default)
    JdbcChatMemoryRepository
    Neo4jChatMemoryRepository
    CassandraChatMemoryRepository

# Built-in Chat Memory Advisors

Asesores de memoria de chat integrados
### 1. MensajeChatMemoryAdvisor
Almacena el chat como mensajes estructurados
Inyecta el historial completo de la conversación en el aviso
Lo mejor para chats conversacionales en tiempo real
Utilizar cuando:
Quieres que el LLM vea todo el chat como un registro de conversación real.

### 2.PromptChatMemoryAsesor

   Convierte la memoria en texto plano
   Lo anexa al mensaje del sistema
   Más eficiente en tokens

Utilizar cuando:
El presupuesto de token es limitado o el modelo es simple.

### 3. VectorStoreChatMemoryAsesevisor

   Almacena la memoria en una base de datos vectorial
   Recupera mensajes semánticamente relevantes
   Ideal para conversaciones a largo plazo o basadas en el conocimiento

Utilizar cuando:
La historia de la conversación es grande y la relevancia importa más que la recencia.
Elegir el asesor adecuado

```MessageChatMemoryAdvisor``` — Mensajes estructurados — Chat en tiempo real

```PromptChatMemoryAdvisor``` — Texto sencillo — Chats optimizados para tokens

```VectorStoreChatMemoryAdvisor``` — Recuperación semántica — Memoria a largo plazo
  
