# Sistema de Gestión de Estacionamiento - Parking Lot

## Descripción General

Diseñar e implementar un sistema de gestión eficiente para un estacionamiento moderno que permita la reserva, entrada, salida y control de vehículos. El sistema debe optimizar el uso del espacio, garantizar seguridad y proporcionar una buena experiencia de usuario.

---

## Requisitos Funcionales

### RF-01: Gestión de Tipos de Vehículos

**Descripción**: El sistema debe soportar diferentes tipos de vehículos con características específicas.

**Detalles**:
- El sistema debe permitir registrar distintos tipos de vehículos (Automóvil, Motocicleta, Camión, Bicicleta, Discapacitado, etc.)
- Cada tipo de vehículo debe tener:
  - Código único identificador
  - Nombre descriptivo
  - Tamaño de estacionamiento requerido
  - Tarifa base por hora

**Criterios de Aceptación**:
- ✓ Cada tipo de vehículo es identificable de forma única
- ✓ Se pueden crear nuevos tipos de vehículos
- ✓ Se pueden consultar todos los tipos disponibles

---

### RF-02: Gestión de Espacios de Estacionamiento

**Descripción**: El sistema debe administrar los espacios físicos disponibles en el lote.

**Detalles**:
- El sistema debe permitir crear y configurar espacios de estacionamiento
- Cada espacio debe tener:
  - Número/identificador único
  - Nivel del estacionamiento
  - Tipo de espacio (para vehículos normales, discapacitados, carga/descarga)
  - Estado actual (disponible, ocupado, reservado, mantenimiento)
  - Tipo(s) de vehículo que puede albergar
- El sistema debe poder mostrar disponibilidad en tiempo real
- Cada espacio puede cambiar de estado según la actividad

**Criterios de Aceptación**:
- ✓ Se pueden crear espacios de estacionamiento
- ✓ Cada espacio tiene un estado bien definido
- ✓ Se puede consultar la disponibilidad de espacios
- ✓ Los espacios se asignan a vehículos según su tipo y disponibilidad

---

### RF-03: Reserva de Espacio de Estacionamiento

**Descripción**: Los usuarios deben poder reservar un espacio con anticipación.

**Detalles**:
- El usuario debe especificar:
  - Tipo de vehículo
  - Fecha y hora de entrada
  - Fecha y hora de salida (duración estimada)
  - Placa/identificación del vehículo (opcional en reserva inicial)
- El sistema debe:
  - Verificar disponibilidad de espacios para el tipo de vehículo
  - Asignar automáticamente un espacio disponible
  - Crear una reserva con estado "PENDIENTE"
  - Generar un código de confirmación único
- Las reservas deben tener estado: PENDIENTE, CONFIRMADA, ACTIVA, COMPLETADA, CANCELADA, NO_SHOW

**Criterios de Aceptación**:
- ✓ Se puede crear una reserva con datos válidos
- ✓ El sistema valida disponibilidad antes de confirmar
- ✓ Se asigna un espacio compatible con el tipo de vehículo
- ✓ Se genera un código de confirmación único
- ✓ La reserva cambia de estado correctamente

---

### RF-04: Procesamiento de Pagos

**Descripción**: El sistema debe calcular y procesar el pago por la reserva.

**Detalles**:
- El sistema debe:
  - Calcular la tarifa basada en tipo de vehículo y duración
  - Aplicar descuentos (si existen tarjetas de cliente frecuente)
  - Procesar el pago (simulado)
  - Emitir un recibo/comprobante
  - Cambiar estado de reserva a "CONFIRMADA" tras pago exitoso
- Debe soportar diferentes métodos de pago (tarjeta de crédito, efectivo, billetera digital, etc.)
- Debe registrar transacciones para auditoría

**Criterios de Aceptación**:
- ✓ El cálculo de tarifa es correcto
- ✓ Se procesa el pago exitosamente
- ✓ Se genera un recibo con detalles de la transacción
- ✓ La reserva se confirma después del pago
- ✓ Hay un registro de transacciones

---

### RF-05: Entrada de Vehículo al Estacionamiento

**Descripción**: El sistema debe registrar la entrada de un vehículo con una reserva válida.

**Detalles**:
- El usuario presenta su código de confirmación o placa del vehículo en la garita
- El sistema debe:
  - Validar que exista una reserva confirmada
  - Validar que la placa coincida con la reserva (si está registrada)
  - Validar que sea dentro del horario permitido (ventana de tolerancia)
  - Registrar la hora exacta de entrada
  - Cambiar estado de la reserva a "ACTIVA"
  - Cambiar estado del espacio asignado a "OCUPADO"
  - Emitir un ticket de entrada
- Debe permitir tolerancia de tiempo (ej: 15 minutos antes/después de la hora reservada)

**Criterios de Aceptación**:
- ✓ Se valida la reserva antes de permitir entrada
- ✓ Se registra la hora exacta de entrada
- ✓ El espacio cambia a ocupado
- ✓ Se emite un ticket de confirmación
- ✓ Se permite tolerancia de tiempo

---

### RF-06: Salida de Vehículo (Salida Normal)

**Descripción**: El sistema debe registrar la salida de un vehículo al término de su reserva.

**Detalles**:
- El usuario presenta su ticket de entrada o código
- El sistema debe:
  - Validar que el vehículo está registrado como "ACTIVO" en el espacio
  - Registrar la hora exacta de salida
  - Calcular duración real del estacionamiento
  - Liberar el espacio (cambiar a "DISPONIBLE")
  - Cambiar estado de reserva a "COMPLETADA"
  - Emitir ticket de salida con resumen
- Si la salida es dentro del tiempo reservado: se completa sin cargo adicional
- Si hay exceso de tiempo: se cobra tarifa adicional

**Criterios de Aceptación**:
- ✓ Se valida que el vehículo está activo
- ✓ Se registra hora de salida correctamente
- ✓ El espacio se libera correctamente
- ✓ Se emite ticket de salida
- ✓ Se calcula correctamente cargos por tiempo extra (si aplica)

---

### RF-07: Salida Anticipada

**Descripción**: El sistema debe permitir que un usuario se vaya antes de la hora reservada.

**Detalles**:
- El usuario solicita salida anticipada antes de la hora reservada
- El sistema debe:
  - Registrar la nueva hora de salida
  - Calcular duración real
  - Procesar reembolso (si aplica según política)
  - Liberar el espacio inmediatamente
  - Cambiar estado de reserva a "COMPLETADA"
  - Emitir ticket actualizado
- La política de reembolso debe ser configurable (ej: 50% del tiempo no utilizado)

**Criterios de Aceptación**:
- ✓ Se permite solicitar salida anticipada
- ✓ Se calcula reembolso según política
- ✓ El espacio se libera inmediatamente
- ✓ Se emite ticket actualizado

---

### RF-08: Manejo de No-Show (Reserva No Utilizada)

**Descripción**: El sistema debe detectar y manejar reservas que no fueron utilizadas.

**Detalles**:
- Si un usuario no se presenta dentro de la ventana de tolerancia:
  - El sistema debe cambiar estado de la reserva a "NO_SHOW"
  - El espacio debe ser liberado automáticamente tras tiempo de espera
  - Se aplica política de penalización (sin reembolso, reembolso parcial, etc.)
  - Se registra el no-show para historial del usuario
- Ventana de tolerancia: configurable (ej: 15 minutos después de hora reservada)

**Criterios de Aceptación**:
- ✓ Se detecta automáticamente no-show tras ventana de tolerancia
- ✓ El espacio se libera
- ✓ Se aplica penalización según política
- ✓ Se registra en historial

---

### RF-09: Cancelación de Reserva

**Descripción**: El sistema debe permitir cancelar una reserva antes de que se confirme.

**Detalles**:
- El usuario puede cancelar una reserva en estado "PENDIENTE" o "CONFIRMADA"
- El sistema debe:
  - Cambiar estado a "CANCELADA"
  - Liberar el espacio asignado
  - Procesar reembolso según política de cancelación (variable según proximidad)
  - Registrar la cancelación
- Políticas de cancelación:
  - Cancelación 24+ horas antes: reembolso completo
  - Cancelación 2-24 horas antes: reembolso 50%
  - Cancelación menos de 2 horas: sin reembolso

**Criterios de Aceptación**:
- ✓ Se puede cancelar una reserva válida
- ✓ El espacio se libera
- ✓ Se calcula reembolso según política
- ✓ Se registra la cancelación

---

### RF-10: Check-in/Check-out en Garita

**Descripción**: Sistema de control en garita para entrada y salida de vehículos.

**Detalles**:
- Personal de garita debe poder:
  - Registrar entrada escaneando código/placa
  - Registrar salida escaneando código/placa
  - Ver datos de la reserva (horario, tipo vehículo, tarifa)
  - Manejar excepciones (vehículos sin reserva, errores de lectura)
- El sistema debe:
  - Validar datos contra base de datos
  - Registrar toda actividad para auditoría
  - Generar alertas si hay discrepancias
  - Permitir entrada manual en caso de lectura fallida

**Criterios de Aceptación**:
- ✓ Se puede registrar entrada mediante código/placa
- ✓ Se puede registrar salida mediante código/placa
- ✓ Se valida información contra reserva
- ✓ Hay auditoría completa de transacciones

---

### RF-11: Consultas y Reportes

**Descripción**: El sistema debe proporcionar consultas de información.

**Detalles**:
- Debe permitir consultar:
  - Disponibilidad actual de espacios (por tipo)
  - Histórico de reservas de un usuario
  - Ocupación del estacionamiento (total y por tipo)
  - Estadísticas de uso
  - Ingresos generados
- Filtros: por fecha, tipo de vehículo, usuario, estado

**Criterios de Aceptación**:
- ✓ Se puede consultar disponibilidad en tiempo real
- ✓ Se puede obtener histórico de reservas
- ✓ Se puede generar estadísticas

---

## Requisitos No Funcionales

### RNF-01: Disponibilidad
- El sistema debe estar disponible 24/7 con máximo 99.5% uptime

### RNF-02: Rendimiento
- Tiempo de respuesta < 200ms para operaciones críticas (asignación de espacio, entrada/salida)
- El sistema debe soportar al menos 100 transacciones concurrentes

### RNF-03: Escalabilidad
- Debe poder manejar estacionamientos con hasta 5,000 espacios
- Debe ser fácil agregar nuevos estacionamientos

### RNF-04: Seguridad
- Autenticación y autorización de usuarios
- Encriptación de datos sensibles (pagos)
- Auditoría completa de operaciones
- Validación de entrada en todas las interfaces

### RNF-05: Mantenibilidad
- Código modular y bien documentado
- Principios SOLID
- Patrones de diseño apropiados
- Fácil extensión para nuevas características

### RNF-06: Confiabilidad
- Recuperación ante fallos
- Consistencia de datos
- Transacciones atómicas para pagos

---

## Restricciones y Consideraciones

- Un vehículo solo puede ocupar un espacio a la vez
- Un espacio solo puede tener un vehículo a la vez
- Las reservas son nominales (para un usuario específico)
- Los datos de pagos son sensibles y requieren encriptación
- El sistema debe mantener auditoría completa de operaciones
- Debe haber tolerancia de tiempo para entrada/salida (configurable)

---

## Próximos Pasos

Basándote en estos requisitos funcionales, implementa las clases necesarias considerando:
1. Modelado del dominio (clases principales)
2. Relaciones entre entidades
3. Estados y transiciones
4. Manejo de errores y excepciones
5. Principios SOLID y patrones de diseño
