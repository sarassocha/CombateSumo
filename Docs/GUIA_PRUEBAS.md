# Guía de Pruebas - Sistema de Combate Sumo con Base de Datos

## Requisitos Previos

1. **MySQL instalado y corriendo**
2. **Driver JDBC de MySQL** (mysql-connector-java.jar) en el classpath
3. **NetBeans** o compilador Java configurado

## Paso 1: Configurar Base de Datos

### Crear la base de datos
```sql
-- Ejecutar en MySQL Workbench o línea de comandos
CREATE DATABASE sumo_db;
USE sumo_db;

-- Importar el archivo Specs/sumo_db.sql
SOURCE /ruta/a/Specs/sumo_db.sql;

-- O copiar y pegar el contenido del archivo
```

### Verificar datos
```sql
SELECT * FROM rikishi;
```

Deberías ver 6 rikishis:
- Hakuho
- Asashoryu
- Takanohana
- Akebono
- Musashimaru
- Konishiki

## Paso 2: Configurar Conexión

Editar `Data/bd.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/sumo_db
db.usuario=root
db.contrasena=tu_contraseña_mysql
```

## Paso 3: Compilar y Ejecutar

### Desde NetBeans:
1. Abrir el proyecto
2. Limpiar y construir (Clean and Build)
3. Ejecutar `Launcher` del servidor

### Desde línea de comandos:
```bash
# Compilar
javac -d build -sourcepath src -cp "lib/*" src/com/uDistrital/avanzada/tallerTres/Servidor/Control/Launcher.java

# Ejecutar
java -cp "build:lib/*" com.uDistrital.avanzada.tallerTres.Servidor.Control.Launcher
```

## Paso 4: Probar el Sistema

### Iniciar Servidor
1. Ejecutar el servidor
2. Cargar archivo de propiedades de animaciones (Data/kimarites.properties)
3. Presionar botón "Iniciar Torneo"

### Observar Combates
El servidor ejecutará automáticamente:
- **Combate 1**: 2 rikishis aleatorios de la BD
- **Combate 2**: 2 rikishis aleatorios de la BD
- **Combate 3**: 2 rikishis aleatorios de la BD

### Verificar Actualización de Victorias
Después de los combates, verificar en MySQL:
```sql
SELECT nombre, victorias FROM rikishi ORDER BY victorias DESC;
```

Los ganadores deberían tener sus victorias incrementadas.

## Paso 5: Verificar Logs

En la consola del servidor deberías ver:
```
Luchadores cargados desde la base de datos: 6
  - Hakuho (Peso: 155kg, Altura: 192cm, Victorias: 0, Técnicas: [...])
  - ...
Presiona 'Iniciar Torneo' para comenzar los combates.

========================================
INICIANDO TORNEO - 3 COMBATES
========================================

========== COMBATE 1/3 ==========
Hakuho vs Asashoryu
Turno 1 | Hakuho aplica [Oshidashi] a Asashoryu
  -> Asashoryu resiste. Sigue en el dohyo.
...
¡Hakuho gana con Yorikiri en el turno 15!
>>> GANADOR COMBATE 1: Hakuho
Victorias de Hakuho actualizadas en BD: 1

========== COMBATE 2/3 ==========
...
```

## Solución de Problemas

### Error: "Driver MySQL no encontrado"
- Descargar mysql-connector-java.jar
- Agregarlo a las librerías del proyecto en NetBeans
- O incluirlo en el classpath al compilar

### Error: "Access denied for user"
- Verificar usuario y contraseña en Data/bd.properties
- Verificar que el usuario tenga permisos en la BD sumo_db

### Error: "Unknown database 'sumo_db'"
- Ejecutar el script SQL para crear la base de datos
- Verificar que MySQL esté corriendo

### No se cargan los rikishis
- Verificar que Data/bd.properties tenga la configuración correcta
- Verificar que la tabla rikishi tenga datos
- Revisar logs del servidor para mensajes de error

## Pruebas Adicionales

### Agregar más rikishis
```sql
INSERT INTO rikishi (nombre, peso, altura, victorias, kimarites) 
VALUES ('NuevoRikishi', 150, 180, 0, 'Oshidashi,Yorikiri');
```

### Cambiar número de combates
En `ControlGeneral.java`:
```java
private static final int MAX_COMBATES = 5; // Cambiar de 3 a 5
```

### Resetear victorias
```sql
UPDATE rikishi SET victorias = 0;
```

## Arquitectura de Hilos

El sistema mantiene la restricción de creación de hilos:

**ControlConexion** (único punto de creación):
1. Hilo para aceptar clientes (si se usan)
2. Hilo por cliente conectado (si se usan)
3. Hilos para rikishis de BD (`lanzarHilosRikishisDB()`)

**Dohyo** sincroniza los hilos pero NO los crea.

## Notas Finales

- El cliente NO participa en el torneo de BD
- Los combates son completamente automáticos
- Las victorias se persisten en la base de datos
- El sistema respeta el patrón MVC y las restricciones de hilos
