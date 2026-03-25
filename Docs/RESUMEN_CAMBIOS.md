# Resumen de Cambios - Integración Base de Datos

## Implementación Completada

### Patrón DAO
Se implementaron las interfaces y clases solicitadas:

**Interfaces:**
- `IReader<T>` - Operaciones de lectura (obtenerTodos, obtenerPorId)
- `IWriter<T>` - Operaciones de escritura (insertar, actualizar, eliminar)

**Implementación:**
- `RikishiDAO` - Implementa IReader e IWriter para gestionar rikishis en la BD

### Configuración de Base de Datos

**Archivo único:** `Data/servidor.properties`

Contiene tanto la configuración de animaciones como de base de datos:
```properties
# Animaciones
gif.entrada=Specs/imagenes/entrada.gif
...

# Base de Datos
db.url=jdbc:mysql://localhost:3306/sumo_db
db.usuario=root
db.contrasena=
```

### Lógica del Torneo

**Configuración:**
- Se realizan 3 combates (configurable con MAX_COMBATES)
- Los luchadores se cargan desde la base de datos
- El cliente permanece exactamente igual (sin cambios)
- Los clientes que se conectan se registran automáticamente en la BD

**Flujo de Combate (Modo Cliente):**
1. Cliente se conecta al servidor
2. El hilo del cliente lee los datos
3. Registra el rikishi en la BD (si no existe)
4. El mismo hilo ejecuta la lógica del rikishi
5. Los rikishis combaten sincronizados en el Dohyo
6. El ganador acumula victorias
7. Se actualiza la BD con las nuevas victorias

**Flujo de Combate (Modo BD - Sin Hilos Adicionales):**
1. El servidor carga todos los rikishis desde la BD al iniciar
2. Para cada combate:
   - Se seleccionan 2 luchadores aleatorios de la BD
   - Se ejecuta combate secuencial (sin crear hilos)
   - Turnos alternados entre combatientes
   - El ganador acumula victorias
   - Se actualiza la BD con las nuevas victorias

### Creación de Hilos
**SOLO 2 puntos de creación en `ControlConexion`:**
1. Hilo para aceptar clientes (loop de aceptación)
2. Hilo por cliente conectado (lee datos, registra en BD, ejecuta lógica del rikishi)

**Modo BD:** No crea hilos adicionales, combates secuenciales

### Archivos Modificados

**Nuevos:**
- `src/com/uDistrital/avanzada/tallerTres/Servidor/DAO/IReader.java`
- `src/com/uDistrital/avanzada/tallerTres/Servidor/DAO/IWriter.java`
- `src/com/uDistrital/avanzada/tallerTres/Servidor/DAO/ConexionDB.java`
- `src/com/uDistrital/avanzada/tallerTres/Servidor/DAO/RikishiDAO.java`

**Modificados:**
- `Data/servidor.properties` - Agregada configuración de BD
- `src/com/uDistrital/avanzada/tallerTres/Servidor/Control/ControlGeneral.java`
  - Agregado campo `rikishiDAO` y `rikishisDB`
  - Método `registrarRikishiEnDB()` para registrar clientes en BD
  - Método `cargarRikishisDesdeDB()` para cargar desde BD
  - Método `iniciarTorneoDB()` para gestionar torneo con BD
  - Método `iniciarCombateDB()` para cada combate
  - Método `simularCombateSecuencial()` para combates sin hilos
  - Método `notificarFinCombateDBSecuencial()` para actualizar victorias en BD
  
- `src/com/uDistrital/avanzada/tallerTres/Servidor/Control/ControlConexion.java`
  - Modificado `manejarCliente()` para registrar en BD dentro del mismo hilo
  - Eliminado método `lanzarHilosRikishisDB()` (ya no necesario)
  
- `src/com/uDistrital/avanzada/tallerTres/Servidor/Control/Dohyo.java`
  - Cambiado `notificarFinCombate()` por `notificarFinCombateDB()`
  
- `src/com/uDistrital/avanzada/tallerTres/Servidor/Modelo/Rikishi.java`
  - Campo `victorias` ahora es mutable (no final)
  - Agregado método `setVictorias()`

- `Specs/sumo_db.sql`
  - Agregados 6 rikishis de ejemplo

**Eliminados:**
- `Data/bd.properties` - Configuración movida a servidor.properties

## Instrucciones de Uso

### 1. Configurar Base de Datos
```sql
-- Importar el archivo Specs/sumo_db.sql en MySQL
-- Esto creará la base de datos 'sumo_db' y la tabla 'rikishi' con datos de ejemplo
```

### 2. Configurar Conexión
Editar `Data/servidor.properties` con tus credenciales:
```properties
db.url=jdbc:mysql://localhost:3306/sumo_db
db.usuario=root
db.contrasena=tu_contraseña
```

### 3. Ejecutar
1. Iniciar el servidor
2. Cargar el archivo de propiedades de animaciones
3. Presionar "Iniciar Torneo"
4. El servidor ejecutará 3 combates automáticamente
5. Las victorias se actualizan en la BD después de cada combate

## Arquitectura

### Comunicación
```
ControlGeneral
    ↓
RikishiDAO → ConexionDB → MySQL
    ↓
Rikishi (Modelo)
    ↓
ControlRikishi (Hilos)
    ↓
Dohyo (Sincronización)
```

### Patrón MVC Mantenido
- **Modelo**: Rikishi, ConexionServidor, ArchivoPropiedades
- **Vista**: VentanaPrincipal
- **Control**: ControlGeneral, ControlRikishi, Dohyo, ControlConexion
- **DAO**: RikishiDAO, ConexionDB (capa de acceso a datos)

## Notas Importantes
- El cliente NO fue modificado (como solicitaste)
- Los clientes que se conectan se registran automáticamente en la BD
- El modo BD ejecuta combates secuenciales sin crear hilos adicionales
- SOLO 2 puntos de creación de hilos en ControlConexion (cumple con la restricción)
- La comunicación servidor-cliente sigue usando ConexionServidor y ConexionCliente
- Configuración unificada en servidor.properties
