Brotato Build Planner

📌 Descripción

Brotato Build Planner es una aplicación de escritorio desarrollada en Java que permite planificar y simular builds del videojuego Brotato. El usuario puede seleccionar un personaje, armas y objetos, y visualizar cómo afectan a las estadísticas en tiempo real.

🚀 Requisitos previos

Antes de ejecutar el proyecto, es necesario tener instalado:

- Java JDK 22 o superior
- Apache Maven
- Un IDE

Opcional:

- Git (para clonar el repositorio)

📥 Clonación del repositorio

Para obtener el proyecto en local, ejecutar:

git clone https://github.com/tu-usuario/BrotatoBuildPlanner.git

Acceder a la carpeta del proyecto:

cd BrotatoBuildPlanner
⚙️ Dependencias

El proyecto utiliza Maven para la gestión de dependencias.

Dependencia principal:

SQLite JDBC (org.xerial:sqlite-jdbc:3.45.1.0)

Maven se encargará automáticamente de descargar todas las dependencias necesarias al compilar el proyecto.

🛠️ Compilación del proyecto

Para compilar el proyecto, ejecutar:

mvn clean install

Esto generará el archivo .jar en la carpeta target.

▶️ Ejecución del proyecto
Opción 1: Desde Maven
mvn exec:java
Opción 2: Ejecutar el JAR
java -jar target/BrotatoBuildPlanner-1.0-SNAPSHOT.jar
🧪 Ejecución desde IDE
En NetBeans:
Abrir el proyecto desde File → Open Project
Esperar a que Maven cargue las dependencias
Ejecutar el proyecto con Run
🗄️ Base de datos
El proyecto utiliza SQLite como base de datos local.
No requiere instalación adicional.
La base de datos se gestiona automáticamente desde la aplicación.
🧰 Herramientas utilizadas
Lenguaje: Java
Gestión de dependencias: Maven
Base de datos: SQLite
IDE recomendado: NetBeans
Control de versiones: Git y GitHub
📌 Notas
Asegúrate de tener correctamente configurada la versión de Java compatible con el proyecto.
Si hay problemas con dependencias, ejecutar:
mvn clean install -U
👨‍💻 Autor

Manuel Aizpuru Apolinar
