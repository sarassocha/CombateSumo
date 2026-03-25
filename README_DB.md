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

### Modo Cliente (Con Hilos)
```
1. Servidor inicia
   ↓
2. Cliente conecta
   ↓
3. Hilo lee datos del cliente
   ↓
4. Registra rikishi en BD (si no existe)
   ↓
5. Mismo hilo ejecuta lógica del rikishi
   ↓
6. Rikishis combaten sincronizados en Dohyo
   ↓
7. Ganador incrementa victorias
   ↓
8. Actualiza BD
```

### Modo Base de Datos (Sin Hilos Adicionales)
```
1. Servidor inicia
   ↓
2. Carga rikishis desde BD (RikishiDAO.obtenerTodos())
   ↓
3. Usuario presiona "Iniciar Torneo"
   ↓
4. Para cada combate (3 total):
   a. Selecciona 2 rikishis aleatorios
   b. Combate secuencial (sin hilos)
   c. Turnos alternados
   d. Ganador incrementa victorias
   e. Actualiza BD (RikishiDAO.actualizar())
   ↓
5. Torneo finalizado
```

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
# Agregar a librerías del proyecto
```

### Error de conexión
```bash
# Verificar MySQL esté corriendo
mysql -u root -p

# Verificar credenciales en servidor.properties
```

### No se cargan rikishis
```sql
-- Verificar datos en BD
SELECT * FROM rikishi;

-- Insertar datos si es necesario
INSERT INTO rikishi VALUES ('Nombre', 150, 180, 0, 'Oshidashi,Yorikiri');
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
