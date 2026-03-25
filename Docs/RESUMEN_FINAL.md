# Resumen Final - Sistema de Combate Sumo

## Arquitectura Final

### Modo Único: Cliente con Registro en BD

El sistema funciona de la siguiente manera:

1. **6 clientes se conectan** al servidor
2. **Cada cliente se registra automáticamente en la BD** (si no existe)
3. **Los hilos de los clientes combaten** sincronizados en el Dohyo
4. **El ganador avanza**, el perdedor es eliminado
5. **Continúa hasta que queda 1 campeón**

## Flujo Completo

```
1. Servidor inicia
   ↓
2. Cliente 1 conecta → Hilo 1 (lee datos, registra en BD, ejecuta lógica)
3. Cliente 2 conecta → Hilo 2 (lee datos, registra en BD, ejecuta lógica)
4. Cliente 3 conecta → Hilo 3 (lee datos, registra en BD, ejecuta lógica)
5. Cliente 4 conecta → Hilo 4 (lee datos, registra en BD, ejecuta lógica)
6. Cliente 5 conecta → Hilo 5 (lee datos, registra en BD, ejecuta lógica)
7. Cliente 6 conecta → Hilo 6 (lee datos, registra en BD, ejecuta lógica)
   ↓
8. Usuario presiona "Iniciar Torneo"
   ↓
9. Ronda 1: 2 rikishis aleatorios combaten (hilos sincronizados en Dohyo)
   ↓
10. Ganador avanza, perdedor eliminado
   ↓
11. Ronda 2: Ganador vs nuevo rival aleatorio
   ↓
12. Continúa hasta 1 campeón
```

## Creación de Hilos

**SOLO 2 puntos en ControlConexion:**

1. **Hilo de aceptación** (línea 62):
   ```java
   new Thread(() -> {
       while (contadorClientes < MAX_CLIENTES) {
           Socket cliente = conexion.aceptarCliente();
           // ...
       }
   }).start();
   ```

2. **Hilo por cliente** (línea 72):
   ```java
   new Thread(() -> manejarCliente(cliente)).start();
   ```
   - Lee datos del cliente
   - Registra en BD
   - Ejecuta lógica del rikishi (rikishi.run())

## Registro en Base de Datos

Cuando un cliente se conecta:

```java
// En ControlConexion.manejarCliente()
1. Lee datos del cliente (nombre, peso, altura, victorias, técnicas)
2. Llama a controlGeneral.registrarRikishiEnDB()
3. ControlGeneral usa RikishiDAO.insertar()
4. Si ya existe en BD, no hace nada
5. Continúa con la lógica del rikishi
```

## Configuración

### Data/servidor.properties
```properties
# Animaciones
gif.entrada=Specs/imagenes/entrada.gif
gif.combate=Specs/imagenes/combate.gif
gif.victoria=Specs/imagenes/victoria.gif
gif.derrota=Specs/imagenes/derrota.gif
gif.espera=Specs/imagenes/espera.gif
jpg.dohyo=Specs/imagenes/dohyo.jpg.jpeg

# Base de Datos
db.url=jdbc:mysql://localhost:3306/sumo_db
db.usuario=root
db.contrasena=
```

## Archivos Principales

### Servidor
- `ControlGeneral.java` - Coordinador principal (LIMPIO, sin modo BD)
- `ControlConexion.java` - Gestión de hilos y conexiones
- `ControlRikishi.java` - Lógica del luchador (Runnable)
- `Dohyo.java` - Sincronización de combates
- `RikishiDAO.java` - Acceso a base de datos
- `ConexionDB.java` - Singleton de conexión

### Cliente
- Sin cambios

## Base de Datos

### Tabla: rikishi
```sql
CREATE TABLE rikishi (
  nombre VARCHAR(100) PRIMARY KEY,
  peso INT NOT NULL,
  altura INT NOT NULL,
  victorias INT DEFAULT 0,
  kimarites TEXT NOT NULL
);
```

### Uso
- Los clientes se registran automáticamente al conectar
- No necesitas importar datos pre-cargados
- La BD se llena dinámicamente con los clientes que se conectan

## Patrón MVC

- **Modelo**: Rikishi, ConexionServidor
- **Vista**: VentanaPrincipal
- **Control**: ControlGeneral, ControlRikishi, Dohyo, ControlConexion
- **DAO**: RikishiDAO, ConexionDB

## Patrones Implementados

1. **DAO** (Data Access Object):
   - IReader<T>
   - IWriter<T>
   - RikishiDAO

2. **Singleton**:
   - ConexionDB

3. **MVC** (Model-View-Controller)

## Ventajas de esta Arquitectura

1. **Simple**: Un solo modo de operación
2. **Automático**: Registro en BD sin intervención manual
3. **Limpio**: Sin código duplicado ni deprecated
4. **Eficiente**: Solo 2 puntos de creación de hilos
5. **Persistente**: Datos guardados en BD automáticamente

## Cómo Usar

1. Asegúrate de que MySQL esté corriendo
2. Crea la base de datos `sumo_db` (vacía está bien)
3. Configura `Data/servidor.properties` con tus credenciales
4. Inicia el servidor
5. Carga el archivo de propiedades de animaciones
6. Ejecuta 6 clientes
7. Los clientes se registran automáticamente en BD
8. Presiona "Iniciar Torneo"
9. ¡Disfruta el combate!

## Notas Importantes

- NO necesitas importar `sumo_db.sql` (aunque puedes si quieres)
- Los clientes se registran automáticamente
- La BD se usa solo para persistencia
- Los combates son con hilos sincronizados (no secuenciales)
- Solo 2 puntos de creación de hilos en ControlConexion

## Archivos Eliminados

- Modo BD completo (iniciarTorneoDB, simularCombateSecuencial, etc.)
- test_db_connection.sql
- SOLUCION_PROBLEMAS_BD.md
- Código deprecated
- Métodos duplicados
