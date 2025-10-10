# Proyecto: Aplicación de Gestión de Gastos

**Facultad de Informática - Universidad de Murcia**
**Asignatura:** Tecnologías de Desarrollo de Software
**Curso:** 2025/2026

---

## 📝 Descripción del Proyecto

Este proyecto consiste en el desarrollo de una aplicación de escritorio para la gestión y el control de gastos personales y compartidos. La aplicación permite a los usuarios registrar, visualizar, filtrar y analizar sus finanzas de manera sencilla y efectiva, tanto a través de una interfaz gráfica intuitiva como de una línea de comandos.

La persistencia de los datos se realiza en formato JSON y el sistema está diseñado siguiendo principios de buena arquitectura de software y patrones de diseño para garantizar su mantenibilidad y extensibilidad.

---

## 👥 Integrantes del Grupo

| Nombre y Apellidos    | Correo Electrónico             | Subgrupo de Prácticas |
| --------------------- | ------------------------------ | --------------------- |
| [Nombre del Alumno 1] | [email1@um.es]                 | [P_XX]                |
| [Nombre del Alumno 2] | [email2@um.es]                 | [P_XX]                |
| [Alonso Daniel Calatrava Navas] | [ad.calatravanavas@um.es]                 | [P_3.4]                |

---

## ✨ Funcionalidades Principales

La aplicación incluye las siguientes características:

*   **Gestión de Gastos Personales**: Registro, edición y borrado de gastos, con asignación de cantidad, fecha y categorías (predefinidas y personalizadas).
*   **Visualización Avanzada de Datos**: Consulta de gastos en formato de tabla/lista y mediante gráficos (barras y circulares). También se incluye una vista de calendario para mostrar los gastos por día.
*   **Filtrado de Gastos**: Posibilidad de filtrar transacciones por rango de fechas, meses específicos, categorías o una combinación de estos.
*   **Cuentas de Gastos Compartidas**:
    *   Creación de cuentas grupales para gestionar deudas entre varias personas.
    *   Cálculo automático de saldos (quién debe a quién).
    *   Soporte para división de gastos **equitativa** y por **porcentajes personalizados**.
*   **Sistema de Alertas Configurables**: Los usuarios pueden definir límites de gasto (semanales o mensuales, totales o por categoría) para recibir notificaciones cuando se superen. Incluye un historial de notificaciones.
*   **Importación de Datos**: Capacidad para importar gastos desde ficheros de texto plano (simulando extractos bancarios) para evitar la introducción manual.
*   **Doble Interfaz**: Gestión completa de los gastos tanto desde la **interfaz gráfica (GUI)** como desde una **línea de comandos (CLI)**.

---

## 🛠️ Tecnologías y Librerías

*   **Lenguaje**: Java [Indicar versión, ej: 11]
*   **Interfaz Gráfica**: JavaFX
*   **Persistencia de Datos**: Jackson (para serialización a JSON)
*   **Gestión de Dependencias**: Apache Maven
*   **Control de Versiones**: Git y GitHub

---

## 📐 Patrones de Diseño Aplicados

Para garantizar una arquitectura robusta, modular y extensible, se han implementado los siguientes patrones de diseño:

*   **Repositorio**: Para desacoplar la lógica de negocio de la capa de persistencia de datos.
*   **Estrategia (Strategy)**: Utilizado para gestionar las diferentes lógicas de las alertas (semanal, mensual) y los métodos de división de gastos en cuentas compartidas (equitativa, por porcentaje).
*   **Adaptador (Adapter)**: Implementado en el sistema de importación para adaptar datos de ficheros externos al modelo de dominio de la aplicación.
*   **Método Factoría (Factory Method)**: Usado para crear las instancias adecuadas de los importadores de datos.
*   **Singleton**: Aplicado en clases que requieren una única instancia global para coordinar acciones en todo el sistema.
*   **[Opcional]** *Mencionar otros patrones usados (ej: Observer, Command, etc.).*

---

## 🚀 Cómo Ejecutar el Proyecto

### Prerrequisitos
- JDK (Java Development Kit) [Versión, ej: 11 o superior].
- Apache Maven.
- Git.

### Pasos para la Ejecución
1.  **Clonar el repositorio:**
    ```bash
    git clone [URL de tu repositorio de GitHub]
    ```
2.  **Navegar al directorio del proyecto:**
    ```bash
    cd [nombre-del-repositorio]
    ```
3.  **Compilar el proyecto y descargar dependencias con Maven:**
    ```bash
    mvn clean install
    ```
4.  **Ejecutar la aplicación:**
    *   **Desde la línea de comandos con Maven:**
        ```bash
        mvn javafx:run
        ```
    *   **Ejecutando el archivo JAR generado (después de compilar):**
        ```bash
        java -jar target/[nombre-del-jar-generado].jar
        ```
        *(Asegúrate de configurar el `maven-shade-plugin` o similar si optas por esta vía).*

---

## 📚 Documentación Detallada

Para una comprensión más profunda de la arquitectura, diseño y funcionalidades del proyecto, consulte la documentación completa ubicada en la carpeta `/docs`.

*   **[Diagrama de Clases del Dominio](./docs/1_DiagramaDeClases.md)**
*   **[Historias de Usuario](./docs/2_HistoriasDeUsuario.md)**
*   **[Diagrama de Interacción](./docs/3_DiagramaDeInteraccion.md)**
*   **[Explicación de Arquitectura y Diseño](./docs/4_Arquitectura.md)**
*   **[Explicación de Patrones de Diseño](./docs/5_Patrones.md)**
*   **[Manual de Usuario](./docs/6_ManualDeUsuario.md)**

---
