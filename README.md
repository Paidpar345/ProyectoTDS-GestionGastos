# Proyecto: Aplicación de Gestión de Gastos

**Facultad de Informática - Universidad de Murcia**
**Asignatura:** Tecnologías de Desarrollo de Software
**Curso:** 2025/2026

---

## 📋 Descripción del proyecto

Este proyecto consiste en el desarrollo de una **aplicación de escritorio completa para la gestión inteligente de gastos personales y compartidos**, diseñada para ayudar a usuarios individuales, familias y grupos de amigos a tomar el control total de sus finanzas de manera sencilla y efectiva.

### 💡 ¿Para qué sirve?

La aplicación permite **registrar todos los gastos diarios** (compra en el supermercado, gasolina, cena con amigos, suscripciones) y **organizarlos automáticamente** por categorías como Alimentación, Transporte, Ocio, Salud o cualquier otra que el usuario defina.

**En tres clics puedes:**

1. Registrar un gasto: "50€ en Mercadona el 4/1/2026 - Comida"
2. Ver cuánto has gastado esta semana en comida
3. Recibir una alerta si te acercas al límite mensual

### 👥 Gestión de gastos compartidos

Perfecta para **convivientes, familias o grupos de amigos** que comparten gastos:

---

## 👥 Integrantes del Grupo

| Nombre y Apellidos              | Correo Electrónico        | Subgrupo de Prácticas |
| ------------------------------- | ------------------------- | --------------------- |
| [Alonso Daniel Calatrava Navas] | [ad.calatravanavas@um.es] | [P_3.4]               |
| [xx]                            | [ejemplo1@um.es]          | [xx]                  |
| [xx]                            | [ejemplo2@um.es]          | [xx]                  |

---

## ✨ Funcionalidades Principales

La aplicación incluye las siguientes características:

- **Gestión de Gastos Personales**: Registro, edición y borrado de gastos, con asignación de cantidad, fecha y categorías (predefinidas y personalizadas).
- **Visualización Avanzada de Datos**: Consulta de gastos en formato de tabla/lista y mediante gráficos (barras y circulares). También se incluye una vista de calendario para mostrar los gastos por día.
- **Filtrado de Gastos**: Posibilidad de filtrar transacciones por rango de fechas, meses específicos, categorías o una combinación de estos.
- **Cuentas de Gastos Compartidas**:
  - Creación de cuentas grupales para gestionar deudas entre varias personas.
  - Cálculo automático de saldos (quién debe a quién).
  - Soporte para división de gastos **equitativa** y por **porcentajes personalizados**.
- **Sistema de Alertas Configurables**: Los usuarios pueden definir límites de gasto (semanales o mensuales, totales o por categoría) para recibir notificaciones cuando se superen. Incluye un historial de notificaciones.
- **Importación de Datos**: Capacidad para importar gastos desde ficheros de texto plano (simulando extractos bancarios) para evitar la introducción manual.
- **Doble Interfaz**: Gestión completa de los gastos tanto desde la **interfaz gráfica (GUI)** como desde una **línea de comandos (CLI)**.

---

## 🛠️ Tecnologías y Librerías

- **Lenguaje**: Java 17 o superior
- **Interfaz Gráfica**: JavaFX
- **Persistencia de Datos**: Jackson (para serialización a JSON)
- **Gestión de Dependencias**: Apache Maven
- **Control de Versiones**: Git y GitHub

## 🚀 Cómo Ejecutar el Proyecto

### Prerrequisitos

- JDK (Java Development Kit) (Versión 17 o superior)
- Apache Maven.
- Git.

### Pasos para la Ejecución

1. **Clonar el repositorio:**

   ```bash
   git clone https://github.com/Paidpar345/ProyectoTDS-GestionGastos.git
   ```

2. **Navegar al directorio del proyecto:**

   ```bash
   cd [nombre-del-repositorio]
   ```

3. **Compilar el proyecto y descargar dependencias con Maven:**

   ```bash
   mvn clean install
   ```

4. **Ejecutar la aplicación:**

   - **Desde la línea de comandos con Maven:**

     ```bash
     mvn javafx:run
     ```

   - **Ejecutando el archivo JAR generado (después de compilar):**

     ```bash
     java -jar target/[nombre-del-jar-generado].jar
     ```

     _(Asegúrate de configurar el `maven-shade-plugin` o similar si optas por esta vía)._

---

## 📚 Documentación Detallada

Para una comprensión más profunda de la arquitectura, diseño y funcionalidades del proyecto, consulte la documentación completa ubicada en la carpeta `/docs`.

- **[Diagrama de Clases del Dominio](./docs/imagenes/Modelo_De_Dominio.png)**
- **[Historias de Usuario](./docs/Proyecto.md)**
- **[Diagrama de Interacción](./docs/Proyecto.md)**
- **[Explicación de Arquitectura y Diseño](./docs/Proyecto.md)**
- **[Explicación de Patrones de Diseño](./docs/Proyecto.md)**
- **[Manual de Usuario](./docs/Manual_De_Usuario.md)**

---
