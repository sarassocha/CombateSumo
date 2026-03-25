# Cómo Instalar el Driver MySQL en NetBeans

## Problema
```
Driver MySQL no encontrado: com.mysql.cj.jdbc.Driver
```

## Solución

### Opción 1: Descargar e Instalar Manualmente (Recomendado)

1. **Descargar el Driver**
   - Ve a: https://dev.mysql.com/downloads/connector/j/
   - Selecciona "Platform Independent"
   - Descarga el archivo ZIP

2. **Extraer el JAR**
   - Descomprime el archivo descargado
   - Busca el archivo `mysql-connector-j-8.x.xx.jar` (o `mysql-connector-java-8.x.xx.jar`)

3. **Agregar al Proyecto en NetBeans**
   - Abre tu proyecto en NetBeans
   - En el panel "Projects", expande tu proyecto
   - Click derecho en "Libraries" → "Add JAR/Folder..."
   - Navega hasta el JAR que descargaste
   - Selecciónalo y presiona "Open"

4. **Verificar**
   - Deberías ver el JAR listado bajo "Libraries"
   - Limpia y construye el proyecto (Clean and Build)

### Opción 2: Usar Maven (Si tu proyecto usa Maven)

Agrega esta dependencia al `pom.xml`:

```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>8.0.33</version>
</dependency>
```

### Opción 3: Descargar Directamente

Descarga desde este enlace directo (versión 8.0.33):
https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar

Luego sigue los pasos 3 y 4 de la Opción 1.

## Verificar la Instalación

Después de agregar el driver:

1. Limpia y construye el proyecto en NetBeans
2. Ejecuta el servidor
3. Conecta un cliente
4. Deberías ver en la consola:
   ```
   Rikishi registrado en BD: [nombre]
   ```

## Estructura del Proyecto

Después de agregar el driver, tu estructura debería verse así:

```
Tu Proyecto
├── Source Packages
├── Test Packages
├── Libraries
│   ├── JDK [versión]
│   └── mysql-connector-j-8.0.33.jar  ← NUEVO
├── Test Libraries
└── ...
```

## Problemas Comunes

### "No suitable driver found"
- El JAR no está en el classpath
- Solución: Verifica que el JAR esté en "Libraries"

### "Access denied for user"
- El driver está instalado pero las credenciales son incorrectas
- Solución: Verifica `Data/servidor.properties`

### "Unknown database 'sumo_db'"
- El driver está instalado pero la base de datos no existe
- Solución: Ejecuta `Specs/sumo_db.sql` en MySQL

## Siguiente Paso

Una vez instalado el driver, ejecuta:

```bash
# En MySQL
mysql -u root -p < Specs/sumo_db.sql
```

Luego reinicia el servidor y conecta los clientes.
