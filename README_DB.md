# Sistema de Combate Sumo - Integración con Base de Datos

## Descripción
Sistema de combate de sumo con integración a base de datos MySQL usando patrones DAO y Singleton. El servidor carga luchadores desde la BD y ejecuta torneos automáticos de 3 combates.

## Características Principales

✅ Patrón DAO (IReader, IWriter, RikishiDAO)
✅ Patrón Singleton (ConexionDB)
✅ Carga de luchadores desde MySQL
✅ Torneo automático de 3 combates
✅ Actualización de victorias en BD
✅ Arquitectura de hilos centralizada en ControlConexion
✅ Sincronización de combates en Dohyo
✅ Patrón MVC respetado

## Inicio Rápido

### 0. Instalar Driver MySQL (REQUERIDO)

**El proyecto NO funcionará sin este paso:**

1. Descarga el driver: https://dev.mysql.com/downloads/connector/j/
2. Extrae el ZIP y busca `mysql-connector-j-8.x.xx.jar`
3. En NetBeans: Click derecho en "Libraries" → "Add JAR/Folder..."
4. Selecciona el JAR descargado
5. Clean and Build el proyecto

**Guía detallada:** Ver archivo `INSTALAR_DRIVER_MYSQL.md`

### 1. Configurar Base de Datos
```bash
# Conectar a MySQL
mysql -u root -p

# Importar esquema
source Specs/sumo_db.sql
```

### 2. Configurar Conexión
Editar `Data/servidor.properties`:
```properties
# Configuración de Base de Datos
db.url=jdbc:mysql://localhost:3306/sumo_db
db.usuario=root
db.contrasena=tu_contraseña
```

### 3. Ejecutar
1. Abrir proyecto en NetBeans
2. Ejecutar `Launcher` del servidor
3. Cargar archivo de propiedades (Data/kimarites.properties)
4. Presionar "Iniciar Torneo"

## Estructura del Proyecto

```
src/
├── com/uDistrital/avanzada/tallerTres/
│   ├── Servidor/
│   │   ├── Control/
│   │   │   ├── ControlGeneral.java      (Coordinador principal)
│   │   │   ├── ControlConexion.java     (Gestión de hilos)
│   │   │   ├── ControlRikishi.java      (Lógica del luchador)
│   │   │   ├── Dohyo.java               (Sincronización de combate)
│   │   │   └── ...
│   │   ├── DAO/
│   │   │   ├── IReader.java             (Interfaz lectura)
│   │   │   ├── IWriter.java             (Interfaz escritura)
│   │   │   ├── ConexionDB.java          (Singleton)
│   │   │   └── RikishiDAO.java          (CRUD rikishis)
│   │   ├── Modelo/
│   │   │   ├── Rikishi.java             (Modelo luchador)
│   │   │   └── ...
│   │   └── Vista/
│   │       └── VentanaPrincipal.java
│   └── Cliente/
│       └── ... (sin cambios)
Data/
├── bd.properties                         (Configuración BD)
└── kimarites.properties                  (Animaciones)
Specs/
└── sumo_db.sql                           (Esquema BD)
Docs/
├── RESUMEN_CAMBIOS.md                    (Resumen en español)
├── DATABASE_INTEGRATION.md               (Documentación técnica)
├── GUIA_PRUEBAS.md                       (Guía de pruebas)
└── ARQUITECTURA_HILOS.md                 (Arquitectura de hilos)
```

## Flujo del Torneo

### Modo Torneo de 6 Jugadores (Actual)
```
1. Servidor inicia y espera 6 clientes
   ↓
2. Cada cliente conecta y envía sus datos
   ↓
3. Hilo lee datos del cliente
   ↓
4. Registra rikishi en BD (si no existe)
   ↓
5. Mismo hilo ejecuta lógica del rikishi
   ↓
6. Cuando hay 6 conectados, usuario presiona "Iniciar Torneo"
   ↓
7. RONDA 1: Se escogen 2 rikishis aleatorios
   ↓
8. Combaten sincronizados en Dohyo
   ↓
9. Ganador incrementa victorias, perdedor es eliminado
   ↓
10. RONDA 2+: Ganador anterior vs nuevo rival aleatorio
   ↓
11. Continúa hasta que solo queda 1 rikishi (campeón)
   ↓
12. Cada victoria se actualiza en BD
```

### Características del Torneo
- **6 jugadores** conectados simultáneamente
- **Torneo acumulativo**: El ganador sigue combatiendo
- **Selección aleatoria**: Primer combate y rivales subsecuentes
- **Eliminación directa**: El perdedor sale del torneo
- **Contador de victorias**: Cada rikishi acumula victorias en el torneo
- **Mensajes por ronda**: "Ronda X - ¡Ganaste! Victorias totales: Y"
- **Registro en BD**: Cada rikishi se registra automáticamente al conectar

## Patrones Implementados

### DAO (Data Access Object)
```java
IReader<T>  → obtenerTodos(), obtenerPorId()
IWriter<T>  → insertar(), actualizar(), eliminar()
RikishiDAO  → Implementa ambas interfaces
```

### Singleton
```java
ConexionDB.getInstancia() → Única instancia de conexión
```

### MVC (Model-View-Controller)
```
Modelo:     Rikishi, ConexionServidor
Vista:      VentanaPrincipal
Control:    ControlGeneral, ControlRikishi, Dohyo
DAO:        RikishiDAO, ConexionDB
```

## Arquitectura de Hilos

**SOLO 2 puntos de creación de hilos en ControlConexion:**

1. **Hilo de aceptación** (modo cliente)
   - Loop infinito que acepta conexiones

2. **Hilo por cliente** (modo cliente)
   - Lee datos del cliente
   - Registra en BD
   - Ejecuta lógica del rikishi en el mismo hilo

**Modo BD:** Sin hilos adicionales, combates secuenciales

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

### Datos de Ejemplo
- Hakuho (155kg, 192cm)
- Asashoryu (148kg, 184cm)
- Takanohana (160kg, 185cm)
- Akebono (233kg, 203cm)
- Musashimaru (235kg, 192cm)
- Konishiki (287kg, 184cm)

## Registro en Base de Datos

### Automático al Conectar Cliente
Cuando un cliente se conecta:
1. El hilo del cliente lee los datos
2. Llama a `controlGeneral.registrarRikishiEnDB()`
3. Usa `RikishiDAO.insertar()` si no existe
4. Continúa con la lógica del rikishi en el mismo hilo

### bd.properties
```properties
# Dentro de Data/servidor.properties
db.url=jdbc:mysql://localhost:3306/sumo_db
db.usuario=root
db.contrasena=
```

### Cambiar número de combates
En `ControlGeneral.java`:
```java
private static final int MAX_COMBATES = 3; // Modificar aquí
```

## Requisitos

- Java 8 o superior
- MySQL 5.7 o superior
- MySQL Connector/J (JDBC Driver)
- NetBeans (opcional)

## Solución de Problemas

### Driver MySQL no encontrado
```bash
# Descargar mysql-connector-java.jar
# Agregar a librerías del proyecto en NetBeans
```

### Error de conexión a BD
```bash
# Verificar MySQL esté corriendo
mysql -u root -p

# Verificar credenciales en Data/servidor.properties
# Verificar que la base de datos 'sumo_db' exista
```

### No se registran rikishis en BD
1. Verifica los logs del servidor en la consola
2. Busca mensajes como:
   - `Configuración BD cargada`
   - `Intentando conectar a BD`
   - `Conexión a BD establecida exitosamente`
   - `Intentando registrar rikishi en BD: [nombre]`
   - `✓ Rikishi registrado exitosamente en BD: [nombre]`

3. Si ves errores:
   - `Driver MySQL no encontrado` → Agrega el JAR del driver
   - `Access denied` → Verifica usuario/contraseña
   - `Unknown database` → Ejecuta el script SQL
   - `Communications link failure` → Verifica que MySQL esté corriendo

### Verificar datos en BD
```sql
-- Conectar a MySQL
mysql -u root -p

-- Usar la base de datos
USE sumo_db;

-- Ver todos los rikishis
SELECT * FROM rikishi;

-- Ver rikishis registrados por clientes (victorias = 0 inicialmente)
SELECT nombre, peso, altura, victorias FROM rikishi WHERE victorias = 0;
```

## Documentación Adicional

- `Docs/RESUMEN_CAMBIOS.md` - Resumen completo en español
- `Docs/DATABASE_INTEGRATION.md` - Documentación técnica
- `Docs/GUIA_PRUEBAS.md` - Guía de pruebas detallada
- `Docs/ARQUITECTURA_HILOS.md` - Arquitectura de hilos

## Autor

Proyecto desarrollado para la Universidad Distrital Francisco José de Caldas
Programación Avanzada - Taller 3

## Licencia

Proyecto académico - Universidad Distrital
