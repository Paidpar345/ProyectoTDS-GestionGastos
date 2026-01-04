# Gestión de Gastos — Documento General del Proyecto

## 1. Diagrama de clases del dominio del proyecto

El modelo de dominio se centra en la gestión de **gastos** personales y compartidos, organizados alrededor de las entidades `Gasto`, `Categoria`, `Persona`, `CuentaCompartida`, `Alerta` y `Notificacion`, que encapsulan la información de los movimientos, su clasificación, los participantes y los avisos generados por superar límites de gasto.
Los catálogos `CatalogoGastos`, `CatalogoCategorias`, `CatalogoAlertas` y `CatalogoCuentasCompartidas` actúan como agregados que mantienen las colecciones en memoria y proporcionan operaciones de alta, baja, búsqueda, agrupación y cálculo de totales sobre estas entidades.

Para consultas y análisis, se aplican filtros de gasto (`FiltroCategorias`, `FiltroFechas`, `FiltroMeses` y `FiltroCompuesto`) que permiten construir criterios de filtrado simples y compuestos.
El sistema de alertas se apoya en el patrón _Strategy_ mediante `EstrategiaAlerta` y sus implementaciones (`AlertaSemanal` y `AlertaMensual`), encargadas de calcular el gasto acumulado en un periodo (y opcionalmente por categoría) y generar `Notificacion` cuando se supera el umbral configurado.

La persistencia se abstrae a través de la interfaz `Repositorio` y su implementación `RepositorioJSON`, que guarda y recupera gastos, categorías, alertas y cuentas compartidas en formato JSON.
Por encima del dominio, los controladores (`ControladorGastos`, `ControladorCategorias`, `ControladorAlertas`, `ControladorCuentasCompartidas` y `ControladorImportador`) coordinan el acceso al repositorio y a los catálogos para ejecutar los casos de uso.

Finalmente, `FachadaAplicacion` implementa un _Singleton_ que inicializa el repositorio, carga los datos persistidos y expone un punto de acceso unificado a los controladores del sistema, aplicando el patrón _Facade_ para desacoplar la capa de presentación de la lógica de negocio.

![Diagrama de Clases](imagenes/Modelo_De_Dominio.png)

## 2. Especificación de las historias de usuario del proyecto

### Historia de Usuario 1: Registrar gasto

Como usuario, quiero registrar un gasto indicando cantidad, fecha y categoría, para mantener un control actualizado de mis finanzas personales.
Criterios de verificación:

- El usuario debe poder introducir una cantidad positiva, seleccionar una fecha y elegir una categoría existente o crear una nueva antes de guardar.
- El sistema debe validar que la cantidad sea numérica positiva y que la fecha tenga formato válido, mostrando mensajes de error en caso contrario.
- Al guardar con éxito, el nuevo gasto debe aparecer en la lista/tabla y reflejarse en las visualizaciones del periodo activo.

### Historia de Usuario 2: Editar gasto

Como usuario , quiero editar un gasto existente, para corregir errores o actualizar la información registrada.
Criterios de verificación:

- El usuario debe poder abrir un formulario de edición con los datos actuales del gasto pre-rellenados.
- El sistema debe validar los cambios con las mismas reglas que en el alta de gastos y bloquear el guardado si hay errores.
- Tras guardar con éxito, el gasto actualizado debe verse reflejado en la lista y en las visualizaciones del periodo filtrado.

### Historia de Usuario 3: Eliminar gasto

Como usuario , quiero eliminar un gasto, para limpiar registros duplicados o incorrectos.
Criterios de verificación:

- El sistema debe solicitar confirmación antes de eliminar definitivamente el gasto.
- Al confirmar, el gasto debe desaparecer de la lista y recalcularse totales, filtros y gráficos afectados.
- Si se cancela la confirmación, el gasto debe permanecer sin cambios.

### Historia de Usuario 4: Gestionar categorías

Como usuario, quiero crear, renombrar y eliminar categorías, para organizar mis gastos más allá de las predefinidas.
Criterios de verificación:

- El sistema debe permitir crear categorías con nombre no vacío y no duplicado.
- **El sistema debe bloquear la eliminación de una categoría si esta tiene gastos asociados, mostrando un mensaje de error para mantener la integridad de los datos.**
- Las nuevas categorías deben estar disponibles de inmediato en el formulario de registro de gastos.

### Historia de Usuario 5: Listar y filtrar gastos

Como usuario , quiero listar y filtrar gastos por meses, intervalos de fechas y categorías, para analizar subconjuntos específicos.
Criterios de verificación:

- La lista/tabla debe mostrar fecha, categoría, descripción opcional e importe, con ordenación por columna.
- Al aplicar filtros de mes, rango de fechas y/o categorías, la lista y las métricas deben actualizarse coherentemente.
- Debe mantenerse un rendimiento adecuado con desplazamiento o paginación cuando el volumen aumente.

### Historia de Usuario 6: Visualizaciones por categoría

Como usuario , quiero ver gráficos de barras o circulares por categoría, para comprender visualmente la distribución de mis gastos.
Criterios de verificación:

- Debe mostrarse la distribución por categoría y los totales del periodo filtrado, coherente con la lista visible.
- Al cambiar filtros de fecha o categoría, los gráficos deben recalcularse y renderizarse de nuevo.
- Si no hay datos para el filtro, debe mostrarse un estado vacío informativo sin errores.

### Historia de Usuario 7: Vista calendario (opcional)

Como usuario , quiero visualizar gastos en un calendario de día completo, para entender su distribución temporal.
Criterios de verificación:

- El calendario debe mostrar los gastos como eventos en su fecha, con detalles básicos.
- Al navegar entre días o intervalos, los eventos visibles deben actualizarse sin inconsistencias.
- En días sin gastos, el calendario debe indicarlo sin lanzar errores.

### Historia de Usuario 8: Línea de comandos (CLI)

Como usuario , quiero registrar, modificar y borrar gastos desde una línea de comandos básica, para operar sin interfaz gráfica.
Criterios de verificación:

- Deben existir comandos “add”, “update” y “delete” con parámetros para cantidad, fecha, categoría e identificador según corresponda.
- El sistema debe validar parámetros e informar con mensajes claros de éxito o error en consola.
- Las operaciones realizadas en CLI deben persistir y reflejarse al abrir la aplicación gráfica.

### Historia de Usuario 9: Configurar alertas de gasto

Como usuario , quiero configurar límites semanales, mensuales y por categoría, para recibir notificaciones al superar topes.
Criterios de verificación:

- Debe permitirse crear alertas con periodo, umbral y, opcionalmente, categoría vinculada.
- Al superar el umbral en el periodo o categoría, debe generarse una notificación legible y accesible.
- Al desactivar o ajustar una alerta, el cambio debe aplicarse de inmediato a futuros cálculos.

### Historia de Usuario 10: Historial de notificaciones

Como usuario , quiero consultar el historial de notificaciones, para revisar avisos pasados y su contexto temporal.
Criterios de verificación:

- Debe listarse fecha, tipo de alerta y mensaje descriptivo de cada notificación.
- Debe poder marcar notificaciones como leídas sin eliminarlas del historial.
- El historial debe poder filtrarse por periodo y tipo de alerta.

### Historia de Usuario 11: Cuenta compartida (reparto equitativo)

Como miembro de una cuenta compartida, quiero crear una cuenta con reparto equitativo y añadir gastos, para dividir costes de forma sencilla.
Criterios de verificación:

- Al crear la cuenta, el reparto debe ser igual para todos los participantes.
- **Al registrar un gasto en el grupo, se debe especificar qué participante lo ha pagado.**
- Los saldos individuales deben recalcularse automáticamente (quién debe a quién) tras cada nuevo gasto.
- Deben evitarse participantes duplicados y nombres vacíos en la creación.

### Historia de Usuario 12: Cuenta compartida (porcentajes)

Como miembro de una cuenta compartida, quiero definir porcentajes personalizados de participación, para reflejar acuerdos de contribución distintos.
Criterios de verificación:

- El sistema debe validar que la suma de los porcentajes de todos los participantes sea exactamente 100% antes de crear la cuenta.
- **Al registrar un gasto, el sistema debe imputar la deuda a cada miembro según su porcentaje asignado, independientemente de quién haya pagado.**
- Los porcentajes definidos deben ser inmutables tras la creación de la cuenta para evitar inconsistencias históricas.

### Historia de Usuario 13: Importar gastos externos

Como usuario , quiero importar gastos desde ficheros de texto de plataformas bancarias en diferentes formatos, para cargar datos externos rápidamente.
Criterios de verificación:

- Debe aceptarse un fichero válido, adaptarlo al modelo y crear los gastos correspondientes.
- Debe evitar duplicados aplicando una regla de identificación o conciliación de registros.
- La arquitectura debe permitir añadir nuevos adaptadores de formato sin modificar la lógica de dominio.

## 3. Diagrama de interacción para una historia de usuario

**Estado:** Diagrama de secuencia definido (HU10).  
**Historia seleccionada:** Historial de notificaciones.

### Objetivo

Permitir al usuario consultar el historial de notificaciones mostrando fecha, tipo de alerta y mensaje, con opción de marcado como leídas (sin borrado) y filtrado por periodo y tipo.

### Participantes (según el diagrama)

- **Usuario**: inicia la consulta, filtra y marca como leídas.
- **UI (HistorialNotificacionesView)**: solicita el historial y presenta/filtra resultados.
- **FachadaAplicacion (Singleton)**: punto de acceso a los controladores.
- **ControladorAlertas**: construye el historial a partir de alertas y sus notificaciones.
- **CatalogoAlertas / Alerta / Notificacion**: contienen la estructura de alertas y su historial de notificaciones.
- **Repositorio**: persiste el estado “leída/no leída” tras el marcado.

### Flujo principal (consulta del historial)

- El usuario abre la pantalla de historial en la UI.
- La UI obtiene la instancia de `FachadaAplicacion` y solicita el `ControladorAlertas`.
- La UI invoca `obtenerTodasLasNotificaciones()` para recuperar el histórico completo.
- `ControladorAlertas` consulta `CatalogoAlertas.obtenerTodas()`, recorre cada `Alerta` y agrega las listas de `Notificacion` asociadas.
- La UI muestra cada entrada con **fecha**, **tipo de alerta** (derivado de la alerta/periodo) y **mensaje**.

### Flujos alternativos

- **Filtrado:** el usuario selecciona periodo y/o tipo de alerta; la UI aplica el filtrado sobre la lista ya recuperada y refresca la vista.
- **Marcar como leídas:** el usuario pulsa “Marcar como leídas”; la UI invoca `marcarTodasLasNotificacionesComoLeidas()`, el controlador marca cada `Notificacion` como leída y persiste el cambio mediante el repositorio, manteniendo el historial intacto.

![Diagrama de interacción (HU10 - Historial de notificaciones)](imagenes/DiagramaInteraccion.png)

## 4. Arquitectura de la aplicación y decisiones de diseño

La aplicación se organiza en una arquitectura por capas para separar responsabilidades y facilitar el mantenimiento y la evolución del sistema.  
La interfaz (JavaFX y CLI) se limita a capturar la entrada del usuario y a representar resultados, delegando la lógica en una capa intermedia.

### Separación por capas

- **Presentación (JavaFX/CLI):** Contiene las pantallas JavaFX y la interfaz de consola, que invocan operaciones de alto nivel sin acceder directamente a la persistencia ni a estructuras internas del dominio.
- **Aplicación (coordinación de casos de uso):** Incluye la fachada (`FachadaAplicacion`) y los controladores (`ControladorGastos`, `ControladorCategorias`, `ControladorAlertas`, `ControladorCuentasCompartidas`, `ControladorImportador`), que coordinan el flujo de cada caso de uso (validaciones, creación/actualización de entidades, persistencia y operaciones derivadas).
- **Dominio (modelo y reglas):** Agrupa las entidades principales (`Gasto`, `Categoria`, `CuentaCompartida`, `Persona`, `Alerta`, `Notificacion`) y catálogos que mantienen colecciones y operaciones en memoria (por ejemplo, altas/bajas, búsquedas, totales, agrupaciones y verificaciones).
- **Persistencia (Repositorios JSON):** Encapsula el almacenamiento en la interfaz `Repositorio` y su implementación `RepositorioJSON`, que serializa y recupera gastos, categorías, alertas y cuentas compartidas desde un fichero JSON.

### Persistencia desacoplada

La persistencia se abstrae mediante `Repositorio`, de forma que el resto del sistema trabaja contra un contrato estable y no depende de detalles concretos del formato JSON.  
Esto permite sustituir la implementación (por ejemplo, migrar a base de datos) minimizando cambios en la lógica de aplicación y en el dominio.

### Coherencia tras escrituras

Las operaciones que modifican datos (p. ej., registrar/modificar gastos) se gestionan desde los controladores, que actualizan catálogos, persisten cambios y desencadenan la verificación de alertas cuando procede.  
Con esta decisión, el sistema mantiene consistencia (datos guardados + alertas recalculadas) sin que la UI tenga que conocer el orden interno de pasos.

### Reutilización entre GUI y CLI

La GUI y la CLI consumen la misma capa de aplicación a través de `FachadaAplicacion` y los controladores, evitando duplicación de lógica de negocio entre interfaces.  
Esto reduce el esfuerzo de mantenimiento y asegura que ambos modos de uso se comporten de forma equivalente.

## 5. Explicación de los patrones de diseño usados

En el proyecto se han aplicado varios patrones para estructurar el código, reducir acoplamiento entre módulos y permitir ampliar funcionalidades sin reescribir partes centrales. Estos patrones aparecen reflejados tanto en las clases como en los propios comentarios/javadoc del proyecto, donde se explica su intención dentro del sistema.

### Singleton

El patrón _Singleton_ se usa para garantizar un único punto de acceso a componentes “globales” del sistema y evitar instancias duplicadas que desincronicen el estado. En `FachadaAplicacion` se implementa una instancia única mediante `getInstancia()`, lo que permite que cualquier interfaz (GUI o CLI) acceda a los mismos controladores ya inicializados y a la misma configuración de arranque.

También se aplica en `RepositorioJSON`, centralizando la lectura/escritura del fichero JSON y evitando que dos repositorios distintos mantengan copias diferentes de gastos, categorías o alertas. Por último, `FabricaImportadores` es Singleton para que el registro de adaptadores sea único y consistente durante toda la ejecución (si se registra un adaptador, queda disponible para cualquier importación posterior).

### Facade

La _Facade_ se materializa en `FachadaAplicacion`, que ofrece una interfaz simple de acceso al subsistema de aplicación: crea y mantiene instancias de repositorio, catálogos y controladores, y expone métodos para obtenerlos. Esta decisión reduce la complejidad de la capa de presentación, ya que la UI no necesita conocer cómo se conectan `RepositorioJSON`, `CatalogoGastos`, `CatalogoCategorias`, etc., ni el orden correcto de inicialización.

Además, la fachada incluye la carga inicial de datos desde el repositorio a los catálogos y la configuración de componentes como el subsistema de importación. Con esto se consigue que el “arranque” de la aplicación esté encapsulado en un solo lugar, facilitando pruebas, depuración y evolución.

### Repository

El patrón _Repository_ se aplica definiendo una interfaz `Repositorio` con operaciones de persistencia para los distintos agregados: gastos, categorías, alertas y cuentas compartidas. Gracias a este contrato, el resto del sistema trabaja contra métodos como `guardarGastos()` u `obtenerTodosLosGastos()` sin depender de detalles concretos del almacenamiento.

La clase `RepositorioJSON` implementa el contrato y encapsula el uso de Jackson y del fichero `datosgastos.json`, manteniendo el resto del código independiente del formato. Esto permite sustituir el almacenamiento por una base de datos u otro mecanismo creando otra implementación del repositorio, sin tener que reescribir controladores ni entidades del dominio.

### Strategy (Alertas)

La lógica de cálculo del gasto relevante para una alerta se desacopla mediante `EstrategiaAlerta`, una interfaz que define cómo computar el gasto dentro de un periodo (y opcionalmente por categoría). Las implementaciones concretas (como `AlertaSemanal` y `AlertaMensual`) encapsulan reglas distintas sin modificar la clase `Alerta`, que solo delega el cálculo en la estrategia configurada.

`ControladorAlertas` decide qué estrategia corresponde en función del `PeriodoTemporal` al crear una alerta, y `Alerta` la utiliza cuando se verifica el límite y se generan `Notificacion` si procede. Este enfoque permite añadir nuevos periodos o criterios (por ejemplo, trimestral o anual) creando una nueva estrategia, sin tocar la lógica existente de verificación.

### Composite (Filtros de gastos)

Los filtros de gastos se estructuran con una interfaz `Filtro` que define una operación uniforme para aplicar un criterio sobre una lista. Existen filtros simples como `FiltroCategorias`, `FiltroFechas` o `FiltroMeses`, cada uno responsable de un único tipo de condición.

Para combinar condiciones, `FiltroCompuesto` mantiene una colección de filtros y los aplica en cadena, produciendo la intersección de resultados. Esta composición permite construir consultas complejas reutilizando piezas pequeñas, y evita crear clases “monolíticas” para cada combinación posible de criterios.

### Adapter + Factory (Importación)

El subsistema de importación desacopla el formato externo de los datos del modelo interno mediante la interfaz `AdaptadorFormato`, que transforma el contenido de un fichero en objetos `Gasto`. `AdaptadorBancario` implementa ese contrato para un CSV bancario concreto, encapsulando reglas de parseo (fechas, cantidades, columnas) sin contaminar la lógica del dominio.

La selección del adaptador se centraliza en `FabricaImportadores`, que mantiene un registro de adaptadores y elige uno compatible con el contenido de entrada. `ImportadorDatos` actúa como coordinador: lee el archivo, solicita el adaptador a la fábrica y ejecuta el parseo, de forma que añadir nuevos formatos se reduce a crear un nuevo adaptador y registrarlo.
