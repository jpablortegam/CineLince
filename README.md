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
8. [Estructura del Proyecto](#estructura-del-proyecto)
9. [Resolución de Problemas Comunes](#resolución-de-problemas-comunes)
10. [Contribuciones y Mejoras Futuras](#contribuciones-y-mejoras-futuras)

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

* **Lenguaje de Programación:** Java (JDK 17 o superior recomendado).
* **Framework UI:** JavaFX (versión acorde al JDK).
* **Base de Datos:** MySQL (versión 8.x recomendada).
* **Sistema de Build y Gestión de Dependencias:** Gradle.
* **Conector Base de Datos:** MySQL Connector/J.
* **Gestión de Variables de Entorno:** Biblioteca dotenv-java.
* **Control de Versiones:** Git.
* **IDE Recomendado:** IntelliJ IDEA.

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
CREATE DATABASE IF NOT EXISTS CinemaSystem;
USE CinemaSystem;
-- Agrega aquí todos los comandos CREATE TABLE para Cine, Pelicula, Funcion, etc.

-- (Opcional) Inserta datos de ejemplo:
-- INSERT INTO Cine (...) VALUES (...);
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
git clone <URL_DEL_REPOSITORIO_GIT_DE_CINELINCES> CineLinces
cd CineLinces
```

### Importar en IntelliJ IDEA

1. Abre IntelliJ IDEA.
2. Selecciona **File > Open...** y elige la carpeta `CineLinces`.
3. Confirma la importación del proyecto Gradle cuando se solicite.

### Dependencias (Gradle)

En `build.gradle` (Groovy DSL) asegúrate de incluir:

```groovy
dependencies {
    implementation 'org.openjfx:javafx-controls:17.0.10'
    implementation 'org.openjfx:javafx-fxml:17.0.10'
    implementation 'mysql:mysql-connector-java:8.0.32'
    implementation 'io.github.cdimascio:dotenv-java:2.2.0'
    // Otras dependencias...
}
```

*Sincroniza Gradle tras cualquier cambio.*

## Ejecución de la Aplicación

1. Verifica que MySQL esté en ejecución.
2. Confirma que el archivo `.env` esté configurado.
3. En IntelliJ IDEA, localiza la clase principal (e.g., `HelloApplication.java`).
4. Haz clic derecho y selecciona **Run 'HelloApplication.main()'**.

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

## Contribuciones y Mejoras Futuras

* Implementación de **Próximos Estrenos**.
* Funcionalidad de compra de boletos con selección de asientos.
* Módulo de autenticación y gestión de usuarios.
* Panel de administración de contenido.
* Integración de logging avanzado.
* Pruebas unitarias e de integración.
* Mejoras en la experiencia de usuario.
