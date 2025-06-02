# CineLinces - Sistema de Gestión de Cines

*CineLinces es una aplicación de escritorio desarrollada en Java con JavaFX, diseñada para la gestión y consulta de información relacionada con cines y películas.*

---

🔶 **Índice**

1. [Descripción General](#descripción-general)
2. [Características Principales](#características-principales)
3. [Tecnologías Utilizadas](#tecnologías-utilizadas)
4. [Prerrequisitos de Instalación](#prerrequisitos-de-instalación)
5. [Configuración de la Base de Datos](#configuración-de-la-base-de-datos)
    1. [Creación del Esquema y Tablas](#creación-del-esquema-y-tablas)
    2. [Creación de un Usuario Dedicado en MySQL](#creación-de-un-usuario-dedicado-en-mysql)
    3. [Configuración del Archivo .env](#configuración-del-archivo-env)
6. [Configuración del Proyecto](#configuración-del-proyecto)
    1. [Clonar el Repositorio](#clonar-el-repositorio)
    2. [Importar en IntelliJ IDEA](#importar-en-intellij-idea)
    3. [Dependencias (Gradle)](#dependencias-gradle)
7. [Ejecución de la Aplicación](#ejecución-de-la-aplicación)
    1. [Ejecución desde IntelliJ IDEA](#ejecución-desde-intellij-idea)
    2. [Ejecución con Gradle](#ejecución-con-gradle)
8. [Configuración con Docker](#configuración-con-docker)
    1. [Requisitos para Docker](#requisitos-para-docker)
    2. [Ejecución con Docker Compose](#ejecución-con-docker-compose)
9. [Estructura del Proyecto](#estructura-del-proyecto)
10. [Resolución de Problemas Comunes](#resolución-de-problemas-comunes)
11. [Cómo Contribuir](#cómo-contribuir)
12. [Mejoras Futuras](#mejoras-futuras)

---

## Descripción General

El proyecto CineLinces busca ofrecer una solución integral para la visualización de información cinematográfica. Los usuarios pueden seleccionar cines, ver las películas en cartelera, sus horarios y detalles. La arquitectura está basada en el patrón DAO (Data Access Object) para la interacción con una base de datos MySQL, y utiliza JavaFX para la interfaz gráfica.

## Características Principales

* **Selección de Cine:** Permite al usuario elegir un cine de una lista desplegable.
* **Visualización de Cartelera:** Muestra las funciones (películas, horarios, salas) disponibles para el cine seleccionado.
* **Tarjetas de Película Interactivas:** Presenta información detallada de cada película/función, con posibilidad de expansión para ver más detalles.
* **Interfaz Gráfica Moderna:** Desarrollada con JavaFX y FXML para una separación clara entre la lógica y la presentación.
* **Persistencia de Datos:** Utiliza una base de datos MySQL para almacenar y recuperar información sobre cines, películas, funciones, etc.
* **Configuración Flexible:** Emplea un archivo `.env` para gestionar las credenciales de la base de datos y otros parámetros.

*Total (Potencial para expandir a):*

* Gestión de **Próximos Estrenos**.
* Sistema de venta de boletos.
* Gestión de usuarios y membresías.
* Administración de productos de dulcería.

## Tecnologías Utilizadas

* **Lenguaje de Programación:** Java (JDK 23 utilizado en el proyecto, JDK 17 mínimo recomendado).
* **Framework UI:** JavaFX 17.0.12.
* **Base de Datos:** MySQL (versión 8.x recomendada).
* **Sistema de Build y Gestión de Dependencias:** Gradle.
* **Conector Base de Datos:** MySQL Connector/J 8.0.32.
* **Gestión de Variables de Entorno:** Biblioteca dotenv-java 3.0.0.
* **Iconos UI:** Ikonli para JavaFX 12.3.1.
* **Seguridad:** jBCrypt 0.4 para hash de contraseñas.
* **Control de Versiones:** Git.
* **IDE Recomendado:** IntelliJ IDEA.
* **Contenedorización:** Docker y Docker Compose (opcional).

## Prerrequisitos de Instalación

Antes de comenzar, asegúrate de tener instalado en tu sistema:

* **JDK (Java Development Kit):** Versión 17 o compatible.
* **Servidor MySQL:** Versión 8.x, en ejecución.
* **IntelliJ IDEA:** Con soporte para Java y Gradle.
* **Git:** Para clonar el repositorio.

## Configuración de la Base de Datos

### Creación del Esquema y Tablas

Conéctate a tu servidor MySQL usando tu cliente preferido (MySQL Workbench, DBeaver, CLI).

```sql
-- Crear la base de datos
CREATE DATABASE IF NOT EXISTS CinemaSystem;
USE CinemaSystem;

-- Crear tabla de Cines
CREATE TABLE IF NOT EXISTS Cine (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(255) NOT NULL,
    ciudad VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    email VARCHAR(100),
    horario_apertura TIME,
    horario_cierre TIME,
    activo BOOLEAN DEFAULT TRUE
);

-- Crear tabla de Salas
CREATE TABLE IF NOT EXISTS Sala (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_cine INT NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    capacidad INT NOT NULL,
    tipo VARCHAR(50),
    FOREIGN KEY (id_cine) REFERENCES Cine(id)
);

-- Crear tabla de Películas
CREATE TABLE IF NOT EXISTS Pelicula (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    sinopsis TEXT,
    duracion INT,
    clasificacion VARCHAR(10),
    genero VARCHAR(100),
    director VARCHAR(100),
    actores TEXT,
    poster_url VARCHAR(255),
    trailer_url VARCHAR(255),
    fecha_estreno DATE,
    activa BOOLEAN DEFAULT TRUE
);

-- Crear tabla de Funciones
CREATE TABLE IF NOT EXISTS Funcion (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_pelicula INT NOT NULL,
    id_sala INT NOT NULL,
    fecha DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    formato VARCHAR(50),
    idioma VARCHAR(50),
    subtitulada BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (id_pelicula) REFERENCES Pelicula(id),
    FOREIGN KEY (id_sala) REFERENCES Sala(id)
);

-- Crear tabla de Asientos
CREATE TABLE IF NOT EXISTS Asiento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_sala INT NOT NULL,
    fila VARCHAR(5) NOT NULL,
    numero INT NOT NULL,
    tipo VARCHAR(50),
    FOREIGN KEY (id_sala) REFERENCES Sala(id),
    UNIQUE KEY (id_sala, fila, numero)
);

-- Crear tabla de Promociones
CREATE TABLE IF NOT EXISTS Promocion (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    descuento DECIMAL(5,2) NOT NULL,
    fecha_inicio DATE,
    fecha_fin DATE,
    codigo VARCHAR(50),
    activa BOOLEAN DEFAULT TRUE
);

-- (Opcional) Inserta datos de ejemplo:
INSERT INTO Cine (nombre, direccion, ciudad, telefono, email, horario_apertura, horario_cierre) 
VALUES ('CineLinces Central', 'Av. Principal 123', 'Ciudad de México', '555-123-4567', 'contacto@cinelinces.com', '10:00:00', '23:00:00');

INSERT INTO Sala (id_cine, nombre, capacidad, tipo) 
VALUES (1, 'Sala 1', 120, 'Standard'),
       (1, 'Sala 2', 100, 'Standard'),
       (1, 'Sala VIP', 60, 'VIP');

INSERT INTO Pelicula (titulo, sinopsis, duracion, clasificacion, genero, director, actores, poster_url) 
VALUES ('El Código Secreto', 'Un programador descubre un código que podría cambiar el mundo.', 120, 'B', 'Thriller, Ciencia Ficción', 'Ana Directora', 'Juan Actor, María Actriz', '/images/posters/codigo_secreto.jpg');
```

### Creación de un Usuario Dedicado en MySQL

Por seguridad, evita usar el usuario `root` para la aplicación:

```sql
CREATE USER 'cinelince_user'@'localhost' IDENTIFIED BY 'TuContraseñaSegura123!';
GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON cinemasystem.* TO 'cinelince_user'@'localhost';
-- Si necesitas migraciones futuras:
-- GRANT CREATE, ALTER, DROP, INDEX, REFERENCES ON cinemasystem.* TO 'cinelince_user'@'localhost';
FLUSH PRIVILEGES;
```

### Configuración del Archivo .env

Crea un archivo `.env` en la raíz del proyecto y añade:

```env
DB_HOST="localhost"
DB_PORT="3306"
DB_NAME="cinemasystem"
DB_USER="cinelince_user"
DB_PASS="TuContraseñaSegura123!"
```

*Asegúrate de no incluir espacios extra en la contraseña.*

## Configuración del Proyecto

### Clonar el Repositorio

```bash
git clone https://github.com/tu-usuario/CineLinces.git
cd CineLinces
```

### Importar en IntelliJ IDEA

1. Abre IntelliJ IDEA.
2. Selecciona **File > Open...** y elige la carpeta `CineLinces`.
3. Confirma la importación del proyecto Gradle cuando se solicite.
4. IntelliJ IDEA debería detectar automáticamente la configuración de Gradle y descargar las dependencias necesarias.

### Dependencias (Gradle)

El proyecto utiliza Gradle con Kotlin DSL (`build.gradle.kts`). Las dependencias ya están configuradas en el archivo:

```kotlin
dependencies {
    implementation("org.kordamp.ikonli:ikonli-javafx:12.3.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")

    //DotEnv
    implementation("io.github.cdimascio:dotenv-java:3.0.0")
    //JDBC de MySQL
    implementation("mysql:mysql-connector-java:8.0.32")
    // jBCrypt
    implementation("org.mindrot:jbcrypt:0.4")
}
```

El plugin de JavaFX también está configurado:

```kotlin
javafx {
    version = "17.0.12"
    modules = listOf("javafx.controls", "javafx.fxml")
}
```

*Sincroniza Gradle tras cualquier cambio haciendo clic en el icono de recarga en la pestaña de Gradle o ejecutando la tarea `gradle build`.*

## Ejecución de la Aplicación

Antes de ejecutar la aplicación, asegúrate de:

1. Tener MySQL en ejecución.
2. Haber creado la base de datos y las tablas necesarias.
3. Haber configurado correctamente el archivo `.env` con las credenciales de la base de datos.

### Ejecución desde IntelliJ IDEA

1. Abre el proyecto en IntelliJ IDEA.
2. Localiza la clase principal `HelloApplication.java` en `src/main/java/com/example/cinelinces/`.
3. Haz clic derecho sobre el archivo y selecciona **Run 'HelloApplication.main()'**.
4. Alternativamente, puedes abrir el archivo y hacer clic en el icono de ejecución (triángulo verde) junto al método `main`.

### Ejecución con Gradle

También puedes ejecutar la aplicación utilizando Gradle:

1. Abre una terminal en la raíz del proyecto.
2. Ejecuta el siguiente comando:

```bash
# En Windows
gradlew.bat run

# En Linux/Mac
./gradlew run
```

Esto compilará el proyecto y ejecutará la clase principal configurada en el archivo `build.gradle.kts`.

## Configuración con Docker

El proyecto incluye archivos de configuración para Docker, lo que permite ejecutar la aplicación en un entorno containerizado sin necesidad de instalar Java o MySQL directamente en tu sistema.

### Requisitos para Docker

Antes de comenzar, asegúrate de tener instalado:

* [Docker](https://www.docker.com/products/docker-desktop/)
* [Docker Compose](https://docs.docker.com/compose/install/) (incluido en Docker Desktop para Windows y Mac)

### Ejecución con Docker Compose

1. Asegúrate de tener el archivo `.env` configurado correctamente en la raíz del proyecto.

2. Construye y ejecuta los contenedores con Docker Compose:

```bash
docker-compose up --build
```

3. Para ejecutar en segundo plano (modo detached):

```bash
docker-compose up -d
```

4. Para detener los contenedores:

```bash
docker-compose down
```

#### Notas importantes sobre Docker

* La aplicación utiliza X11 forwarding para mostrar la interfaz gráfica. En Windows, necesitarás instalar [VcXsrv](https://sourceforge.net/projects/vcxsrv/) y en Mac, [XQuartz](https://www.xquartz.org/).

* El Dockerfile está configurado para usar OpenJDK 17 y JavaFX 17.0.12.

* El archivo `docker-compose.yml` configura la conexión entre la aplicación y la base de datos, utilizando las variables de entorno del archivo `.env`.

## Estructura del Proyecto

```text
CineLinces/
├── .env
├── .gitignore
├── build.gradle
├── gradlew
├── gradlew.bat
├── src/
│   ├── main/
│   │   ├── java/com/example/cinelinces/
│   │   │   ├── HelloApplication.java
│   │   │   ├── controllers/
│   │   │   ├── dao/
│   │   │   │   └── impl/
│   │   │   ├── database/
│   │   │   ├── model/
│   │   │   │   └── dto/
│   │   │   ├── services/
│   │   │   └── utils/
│   │   └── resources/
│   │       ├── com/example/cinelinces/
│   │       ├── images/
│   │       │   └── posters/
│   │       ├── icons/
│   │       └── styles/
│   └── test/
└── README.md
```

## Resolución de Problemas Comunes

* **`ClassNotFoundException: com.mysql.cj.jdbc.Driver`:**

    * Asegura la dependencia de MySQL Connector en Gradle.
    * Sincroniza el proyecto.
    * Verifica que el JAR aparezca en **External Libraries**.

* **Errores de conexión (SQLException, NPE en DAOs):**

    * Confirma que MySQL esté activo.
    * Revisa valores en `.env`.
    * Verifica permisos del usuario MySQL.

* **Imágenes no cargan:**

    * Comprueba rutas en `src/main/resources/images`.
    * Asegura que la carpeta esté marcada como recurso.

* **NPE en controladores FXML:**

    * Verifica `fx:id` y `@FXML` coincidan.
    * Chequea `fx:controller` en los archivos FXML.

## Cómo Contribuir

¡Las contribuciones son bienvenidas! Si deseas contribuir al proyecto, sigue estos pasos:

1. **Fork del repositorio**: Crea una copia del repositorio en tu cuenta de GitHub.

2. **Clona tu fork**: 
   ```bash
   git clone https://github.com/tu-usuario/CineLinces.git
   cd CineLinces
   ```

3. **Crea una rama para tu contribución**:
   ```bash
   git checkout -b feature/nueva-funcionalidad
   ```

4. **Realiza tus cambios**: Implementa las mejoras o correcciones.

5. **Prueba tus cambios**: Asegúrate de que todo funciona correctamente.

6. **Haz commit de tus cambios**:
   ```bash
   git add .
   git commit -m "Añade nueva funcionalidad: descripción breve"
   ```

7. **Sube tus cambios a GitHub**:
   ```bash
   git push origin feature/nueva-funcionalidad
   ```

8. **Crea un Pull Request**: Desde tu fork en GitHub, crea un Pull Request hacia el repositorio original.

### Guía de estilo

* Sigue las convenciones de nomenclatura de Java.
* Utiliza el patrón DAO para acceso a datos.
* Mantén la separación entre la lógica de negocio y la interfaz de usuario.
* Documenta el código con comentarios claros.
* Escribe mensajes de commit descriptivos.

## Mejoras Futuras

* Implementación de **Próximos Estrenos**.
* Funcionalidad de compra de boletos con selección de asientos.
* Módulo de autenticación y gestión de usuarios.
* Panel de administración de contenido.
* Integración de logging avanzado.
* Pruebas unitarias e de integración.
* Mejoras en la experiencia de usuario.
* Soporte para múltiples idiomas.
* Integración con servicios de pago en línea.
* Aplicación móvil complementaria.
