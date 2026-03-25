# Guía de Integración con Base de Datos

## Resumen

El servidor ahora registra automáticamente cada rikishi en la base de datos MySQL cuando un cliente se conecta. Esta guía te ayudará a configurar y verificar que la integración funcione correctamente.

## Configuración Paso a Paso

### 1. Instalar MySQL

Si no tienes MySQL instalado:

**Windows:**
- Descarga MySQL Community Server desde: https://dev.mysql.com/downloads/mysql/
- Instala y configura con contraseña para el usuario `root` (o sin contraseña)

**Linux:**
```bash
sudo apt-get install mysql-server
```

**macOS:**
```bash
brew install mysql
```

### 2. Crear la Base de Datos

Abre una terminal/consola y ejecuta:

```bash
# Conectar a MySQL
mysql -u root -p
# (Ingresa tu contraseña si la configuraste)
```

Dentro de MySQL, ejecuta:

```sql
-- Crear la base de datos
CREATE DATABASE sumo_db;

-- Usar la base de datos
USE sumo_db;

-- Crear la tabla
CREATE TABLE rikishi (
  nombre varchar(100) NOT NULL,
  peso int(11) NOT NULL,
  altura int(11) NOT NULL,
  victorias int(11) NOT NULL DEFAULT 0,
  kimarites text NOT NULL,
  PRIMARY KEY (nombre)
);

-- Verificar que se creó correctamente
SHOW TABLES;
DESCRIBE rikishi;
```

O simplemente importa el archivo SQL:

```bash
mysql -u root -p < Specs/sumo_db.sql
```

### 3. Configurar la Conexión

Edita el archivo `Data/servidor.properties` y verifica/ajusta estas líneas:

```properties
# Configuración de Base de Datos
db.url=jdbc:mysql://localhost:3306/sumo_db
db.usuario=root
db.contrasena=
```

**Importante:**
- Si tu MySQL usa un puerto diferente a 3306, cámbialo en `db.url`
- Si tu usuario no es `root`, cámbialo en `db.usuario`
- Si configuraste una contraseña, agrégala en `db.contrasena`

### 4. Agregar el Driver MySQL al Proyecto

El proyecto necesita el driver JDBC de MySQL para conectarse a la base de datos.

**Opción A: Descargar manualmente**
1. Descarga `mysql-connector-java` desde: https://dev.mysql.com/downloads/connector/j/
2. Extrae el archivo JAR
3. En NetBeans:
   - Click derecho en el proyecto → Properties
   - Libraries → Add JAR/Folder
   - Selecciona el archivo `mysql-connector-java-X.X.XX.jar`

**Opción B: Maven (si usas Maven)**
```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>
```

## Verificar la Integración

### Paso 1: Iniciar el Servidor

1. Ejecuta `Launcher.java` del servidor
2. Carga el archivo de propiedades (`Data/kimarites.properties`)
3. Observa la consola del servidor

**Deberías ver:**
```
Configuración BD cargada:
  URL: jdbc:mysql://localhost:3306/sumo_db
  Usuario: root
  Contraseña: (vacía)
Servidor escuchando en puerto 5000
```

### Paso 2: Conectar Clientes

1. Ejecuta 6 instancias del cliente
2. En cada cliente, ingresa:
   - Nombre del rikishi
   - Peso (kg)
   - Altura (cm)
   - Victorias iniciales (normalmente 0)
   - Selecciona técnicas

3. Presiona "Conectar"

### Paso 3: Verificar Logs del Servidor

En la consola del servidor deberías ver:

```
Rikishi conectado #1
Intentando conectar a BD: jdbc:mysql://localhost:3306/sumo_db
Conexión a BD establecida exitosamente
Intentando registrar rikishi en BD: NombreRikishi
Rikishi NO encontrado en BD: NombreRikishi
Rikishi insertado en BD: NombreRikishi (filas afectadas: 1)
✓ Rikishi registrado exitosamente en BD: NombreRikishi
Rikishi conectado: NombreRikishi (Peso: 150kg, Altura: 180cm, Victorias: 0)
```

**Si el rikishi ya existe:**
```
Rikishi encontrado en BD: NombreRikishi
Rikishi ya existe en BD: NombreRikishi
```

### Paso 4: Verificar en la Base de Datos

Abre MySQL y ejecuta:

```sql
USE sumo_db;
SELECT * FROM rikishi;
```

Deberías ver los rikishis que se conectaron:

```
+---------------+------+--------+-----------+---------------------------+
| nombre        | peso | altura | victorias | kimarites                 |
+---------------+------+--------+-----------+---------------------------+
| Mateo         | 150  | 180    | 0         | Oshidashi,Yorikiri,...    |
| Juan          | 160  | 185    | 0         | Tsukidashi,Hatakikomi,... |
| ...           | ...  | ...    | ...       | ...                       |
+---------------+------+--------+-----------+---------------------------+
```

## Solución de Problemas

### Error: "Driver MySQL no encontrado"

**Causa:** El JAR del driver MySQL no está en el classpath.

**Solución:**
1. Descarga `mysql-connector-java-8.0.33.jar`
2. Agrégalo a las librerías del proyecto en NetBeans
3. Reinicia el servidor

### Error: "Access denied for user 'root'@'localhost'"

**Causa:** Usuario o contraseña incorrectos.

**Solución:**
1. Verifica tu usuario y contraseña de MySQL
2. Actualiza `Data/servidor.properties`:
   ```properties
   db.usuario=tu_usuario
   db.contrasena=tu_contraseña
   ```

### Error: "Unknown database 'sumo_db'"

**Causa:** La base de datos no existe.

**Solución:**
```bash
mysql -u root -p < Specs/sumo_db.sql
```

O manualmente:
```sql
CREATE DATABASE sumo_db;
```

### Error: "Communications link failure"

**Causa:** MySQL no está ejecutándose o el puerto es incorrecto.

**Solución:**
1. Verifica que MySQL esté corriendo:
   ```bash
   # Windows
   net start MySQL
   
   # Linux/macOS
   sudo systemctl start mysql
   ```

2. Verifica el puerto en `Data/servidor.properties`

### No se registran los rikishis

**Diagnóstico:**
1. Revisa los logs del servidor en la consola
2. Busca mensajes de error específicos
3. Verifica que el archivo `Data/servidor.properties` exista y tenga la configuración correcta

**Solución:**
- Si no ves ningún mensaje de BD, verifica que el archivo de propiedades esté bien configurado
- Si ves errores de conexión, sigue los pasos anteriores según el error

## Flujo Completo de Registro

```
Cliente conecta
    ↓
ControlConexion.manejarCliente()
    ↓
Lee datos del cliente (nombre, peso, altura, victorias, técnicas)
    ↓
ControlGeneral.registrarRikishiEnDB()
    ↓
RikishiDAO.obtenerPorId(nombre)
    ↓
¿Existe en BD?
    ├─ SÍ → Log: "Rikishi ya existe en BD"
    └─ NO → RikishiDAO.insertar()
            ↓
            ConexionDB.getConexion()
            ↓
            INSERT INTO rikishi VALUES (...)
            ↓
            Log: "✓ Rikishi registrado exitosamente en BD"
    ↓
ControlGeneral.conectarRikishi()
    ↓
Crea ControlRikishi y lo agrega a la lista
    ↓
Hilo ejecuta la lógica del rikishi
```

## Logs de Depuración

El sistema ahora incluye logs detallados para facilitar la depuración:

| Log | Significado |
|-----|-------------|
| `Configuración BD cargada` | Configuración leída correctamente |
| `Intentando conectar a BD` | Iniciando conexión a MySQL |
| `Conexión a BD establecida exitosamente` | Conexión exitosa |
| `Intentando registrar rikishi en BD: X` | Iniciando registro |
| `Rikishi NO encontrado en BD: X` | Rikishi nuevo, se insertará |
| `Rikishi encontrado en BD: X` | Rikishi ya existe |
| `Rikishi insertado en BD: X (filas afectadas: 1)` | Inserción exitosa |
| `✓ Rikishi registrado exitosamente en BD: X` | Registro completo |
| `✗ Error: No se pudo insertar rikishi en BD: X` | Error en inserción |

## Arquitectura DAO

El proyecto implementa el patrón DAO (Data Access Object):

```
ConexionDB (Singleton)
    ↓
    Gestiona la conexión a MySQL
    ↓
RikishiDAO implements IReader<Rikishi>, IWriter<Rikishi>
    ↓
    ├─ obtenerTodos() → SELECT * FROM rikishi
    ├─ obtenerPorId(nombre) → SELECT * FROM rikishi WHERE nombre = ?
    ├─ insertar(rikishi) → INSERT INTO rikishi VALUES (...)
    ├─ actualizar(rikishi) → UPDATE rikishi SET ... WHERE nombre = ?
    └─ eliminar(nombre) → DELETE FROM rikishi WHERE nombre = ?
```

## Próximos Pasos

Una vez que la integración funcione correctamente:

1. Los rikishis se registran automáticamente al conectar
2. Puedes iniciar el torneo con 6 jugadores
3. Las victorias se acumulan durante el torneo
4. (Futuro) Actualizar victorias en BD después de cada combate

## Contacto

Si tienes problemas, revisa:
- `README_DB.md` - Documentación general
- `Docs/ARQUITECTURA_HILOS.md` - Arquitectura de hilos
- `Docs/RESUMEN_CAMBIOS.md` - Resumen de cambios
