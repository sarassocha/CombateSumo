# Arquitectura de Hilos - Sistema de Combate Sumo

## Puntos de Creación de Hilos

**SOLO 2 puntos de creación de hilos, ambos en `ControlConexion`:**

### 1. Hilo de Aceptación
```java
// Línea 62 en ControlConexion.java
new Thread(() -> {
    while (contadorClientes < MAX_CLIENTES) {
        Socket cliente = conexion.aceptarCliente();
        // ...
    }
}).start();
```
- **Propósito**: Loop infinito que acepta conexiones entrantes
- **Cantidad**: 1 hilo único
- **Ciclo de vida**: Vive mientras el servidor esté activo

### 2. Hilo por Cliente
```java
// Línea 72 en ControlConexion.java
new Thread(() -> manejarCliente(cliente)).start();
```
- **Propósito**: 
  1. Lee datos del cliente
  2. Registra el rikishi en la base de datos
  3. Crea ControlRikishi
  4. Ejecuta `rikishi.run()` directamente (sin crear nuevo hilo)
- **Cantidad**: 1 hilo por cada cliente conectado (máximo 6)
- **Ciclo de vida**: 
  1. Lee datos del cliente
  2. Registra en BD
  3. Se convierte en el hilo del rikishi
  4. Ejecuta lógica de combate
  5. Termina cuando el rikishi es eliminado

## Modo Base de Datos

El modo BD NO crea hilos adicionales. Los combates se ejecutan de forma secuencial:

```java
// En ControlGeneral.java
private void simularCombateSecuencial(ControlRikishi c1, ControlRikishi c2) {
    // Combate secuencial sin hilos
    // Turnos alternados entre combatientes
    // Actualiza BD al finalizar
}
```

**Características:**
- NO crea hilos adicionales
- Respeta la restricción de solo 2 puntos de creación
- Combates ejecutados en el hilo principal
- Actualiza victorias en BD después de cada combate

## Sincronización de Hilos

### Dohyo - Coordinador de Combate (Modo Cliente)
```java
public synchronized void aplicarTecnica(ControlRikishi atacante) {
    // Método sincronizado que coordina los turnos
    // Solo permite que un rikishi ataque a la vez
}
```

**Características:**
- NO crea hilos
- Sincroniza el acceso de los hilos existentes
- Usa `synchronized` para evitar condiciones de carrera
- Solo se usa en modo cliente (con hilos)

### ControlRikishi - Lógica del Luchador
```java
@Override
public void run() {
    while (!detenido) {
        if (!enCombate) {
            Thread.sleep(100); // Espera a ser activado
            continue;
        }
        // Solicita turno al ControlGeneral
        controlGeneral.solicitarTurno(this);
        Thread.sleep(random.nextInt(500));
    }
}
```

**Características:**
- Implementa `Runnable` (no extiende Thread)
- Loop que solicita turnos mientras esté activo
- Espera aleatoria entre turnos para simular tiempo de reacción
- Se detiene cuando es eliminado del torneo

## Flujo de Ejecución

### Modo Cliente (Con Hilos)
```
1. Servidor inicia → Crea hilo de aceptación
2. Cliente conecta → Crea hilo para ese cliente
3. Hilo lee datos → Registra en BD
4. Hilo crea ControlRikishi
5. Hilo ejecuta rikishi.run() → Se convierte en hilo del rikishi
6. Rikishi solicita turnos → Dohyo sincroniza
7. Combate termina → Hilo del rikishi perdedor termina
8. Ganador espera siguiente combate
```

### Modo Base de Datos (Sin Hilos Adicionales)
```
1. Servidor carga rikishis desde BD
2. Usuario presiona "Iniciar Torneo"
3. Para cada combate (3 total):
   a. Se seleccionan 2 rikishis aleatorios
   b. Se crean ControlRikishi con datos de BD
   c. Combate secuencial (sin hilos)
   d. Turnos alternados entre combatientes
   e. Combate termina cuando hay ganador
   f. Victorias se actualizan en BD
4. Torneo finalizado
```

## Diagrama de Hilos

### Modo Cliente
```
ControlConexion
    │
    ├─ Thread 1: Aceptar Clientes (loop infinito)
    │       │
    │       ├─ Thread 2: Cliente 1 → Registra BD → ControlRikishi 1
    │       ├─ Thread 3: Cliente 2 → Registra BD → ControlRikishi 2
    │       ├─ Thread 4: Cliente 3 → Registra BD → ControlRikishi 3
    │       ├─ Thread 5: Cliente 4 → Registra BD → ControlRikishi 4
    │       ├─ Thread 6: Cliente 5 → Registra BD → ControlRikishi 5
    │       └─ Thread 7: Cliente 6 → Registra BD → ControlRikishi 6
    │
    └─ Todos los hilos se sincronizan en Dohyo
```

### Modo Base de Datos
```
Hilo Principal (UI)
    │
    └─ Combates Secuenciales (sin hilos adicionales)
        ├─ Combate 1: Rikishi A vs Rikishi B
        ├─ Combate 2: Rikishi C vs Rikishi D
        └─ Combate 3: Rikishi E vs Rikishi F
```

## Registro en Base de Datos

Cuando un cliente se conecta:

```java
// En ControlConexion.manejarCliente()
1. Lee datos del cliente
2. Llama a controlGeneral.registrarRikishiEnDB()
3. ControlGeneral usa RikishiDAO.insertar()
4. Si ya existe, no hace nada
5. Continúa con la lógica del rikishi
```

**Ventajas:**
- Todos los rikishis conectados quedan registrados en BD
- Persistencia automática de datos
- No requiere hilos adicionales
- Se ejecuta en el mismo hilo del cliente

## Ventajas de esta Arquitectura

1. **Centralización**: Todos los hilos se crean en un solo lugar (ControlConexion)
2. **Simplicidad**: Solo 2 puntos de creación de hilos
3. **Control**: Fácil de rastrear y debuggear
4. **Sincronización**: Dohyo coordina sin crear hilos adicionales
5. **Flexibilidad**: Modo BD sin hilos adicionales
6. **Persistencia**: Registro automático en BD al conectar
7. **Mantenibilidad**: Cambios en threading solo afectan ControlConexion

## Consideraciones de Seguridad

### Variables Volátiles
```java
private volatile boolean combateTerminado; // En Dohyo
private volatile boolean enCombate;        // En ControlRikishi
private volatile boolean detenido;         // En ControlRikishi
```
- Garantizan visibilidad entre hilos
- Evitan caching de valores en registros de CPU

### Métodos Sincronizados
```java
public synchronized void aplicarTecnica(...)  // En Dohyo
public synchronized void setEliminado(...)    // En Rikishi
public synchronized boolean isEliminado()     // En Rikishi
```
- Previenen condiciones de carrera
- Garantizan acceso exclusivo a recursos compartidos

### Bloques Sincronizados
```java
synchronized (this) {
    clientes.add(cliente);
    socketsPorRikishi.put(rikishi, cliente);
}
```
- Protegen colecciones compartidas
- Minimizan el tiempo de bloqueo

## Resumen

**Total de puntos de creación de hilos: 2** ✓
1. Hilo de aceptación de clientes (1 hilo)
2. Hilo por cliente conectado (hasta 6 hilos)

**Todos en ControlConexion** ✓
**Dohyo NO crea hilos** ✓
**Modo BD sin hilos adicionales** ✓
**Registro automático en BD** ✓
**Sincronización correcta** ✓
**Patrón MVC respetado** ✓
