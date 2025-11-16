# Sistema de Estacionamiento (Parking Lot System) - OOD

## Concepto

Un **Sistema de Estacionamiento** es una aplicación que gestiona un estacionamiento (parking lot) permitiendo a los usuarios:
- Entrar/salir del estacionamiento
- Buscar espacios disponibles
- Registrar tickets de entrada y salida
- Calcular tarifas según el tiempo de estancia
- Procesar pagos
- Generar reportes

El objetivo es optimizar el uso del espacio disponible y maximizar la eficiencia en la gestión de vehículos.

---

## Requisitos Funcionales

### 1. **Gestión de Espacios de Estacionamiento**
- Crear y registrar espacios de estacionamiento
- Clasificar espacios por tipo:
  - Compactos (para vehículos pequeños)
  - Regulares (para vehículos estándar)
  - Grandes (para vehículos de tamaño grande)
  - Handicap (para personas con discapacidad)
  - EV (para vehículos eléctricos)
- Cada espacio debe tener un ID único
- Cada espacio tiene un estado: Disponible, Ocupado, Fuera de servicio
- Mostrar disponibilidad en tiempo real

### 2. **Gestión de Vehículos**
- Registrar vehículos con:
  - Número de placa
  - Tipo de vehículo
  - Color
  - Marca/Modelo
- Validar que el tipo de vehículo sea compatible con el espacio asignado
- Permitir que el mismo vehículo estacione múltiples veces

### 3. **Entrada de Vehículos**
- Buscar espacios disponibles del tipo correcto
- Asignar un espacio al vehículo
- Generar ticket de entrada con:
  - Número de espacio
  - Hora de entrada
  - Número de placa del vehículo
- Actualizar estado del espacio a "Ocupado"

### 4. **Salida de Vehículos**
- Aceptar número de espacio o placa del vehículo
- Validar el ticket
- Registrar hora de salida
- Calcular costo de estacionamiento
- Marcar espacio como "Disponible"

### 5. **Cálculo de Tarifas**
- Implementar política de precios flexible
- Opciones:
  - Tarifa por hora
  - Tarifa por día completo
  - Tarifa por mes
  - Descuentos por largo estacionamiento
- Calcular automáticamente el costo según tiempo de estancia

### 6. **Procesamiento de Pagos**
- Aceptar múltiples métodos de pago:
  - Efectivo
  - Tarjeta de crédito/débito
  - Billetera digital
- Registrar comprobantes de pago
- Generar recibos

### 7. **Consultas y Reportes**
- Consultar espacios disponibles por tipo
- Consultar historial de estacionamientos
- Reportes de ingresos
- Reportes de ocupación

---

## Requisitos No Funcionales

### 1. **Rendimiento**
- Búsqueda de espacios disponibles < 100ms
- Procesamiento de entrada/salida < 500ms
- Sistema debe soportar múltiples usuarios simultáneamente

**Tips de trabajo:**
- Usa HashMap o ConcurrentHashMap para búsquedas O(1)
- Mantén índices separados por tipo de espacio
- Implementa caché de espacios disponibles

### 2. **Disponibilidad**
- Alta disponibilidad del sistema (99.9%)
- Gestión de fallos graceful
- Recuperación automática ante errores

**Tips de trabajo:**
- Implementa logging completo de todas las operaciones
- Usa excepciones custom para diferentes tipos de errores
- Diseña para fallback graceful (si no hay espacio, rechaza entrada clara)

### 3. **Escalabilidad**
- Soportar múltiples pisos y secciones
- Crecer sin rediseño de arquitectura
- Soportar un crecimiento del 100% de usuarios

**Tips de trabajo:**
- Usa Enums para tipos de espacios y vehículos (type-safe)
- Diseña con interfaces para abstraer comportamientos
- Implementa Strategy Pattern para precios flexible

### 4. **Seguridad**
- Proteger datos de vehículos y usuarios
- Auditoría de transacciones financieras
- Validar autenticación de usuarios

**Tips de trabajo:**
- Valida entrada temprana (placa válida, tipo soportado)
- Registra todas las transacciones financieras
- Implementa DAO (Data Access Objects) para abstraer persistencia

### 5. **Mantenibilidad**
- Código limpio y bien estructurado (OOD)
- Fácil de extender
- Documentación clara
- Patrones de diseño reconocibles

**Tips de trabajo:**
- Separa lógica de negocio de persistencia
- Crea interfaces claras entre componentes
- Usa naming consistente (Vehicle vs Vehículo - elige uno)
- Documenta decisiones de diseño importantes

### 6. **Usabilidad**
- Interfaz intuitiva
- Mensajes de error claros
- Tiempos de respuesta aceptables

**Tips de trabajo:**
- Define excepciones claras con mensajes descriptivos
- Maneja los casos de error de forma explícita
- Proporciona feedback claro al usuario (entrada exitosa, pago completado, etc.)