
# Historias de Usuario [Gestión de Gastos]

## 1. Registrar gasto

Como usuario registrado, quiero registrar un gasto indicando cantidad, fecha y categoría, para mantener un control actualizado de mis finanzas personales.
Criterios de verificación:

- El usuario debe poder introducir una cantidad positiva, seleccionar una fecha y elegir una categoría existente o crear una nueva antes de guardar.
- El sistema debe validar que la cantidad sea numérica positiva y que la fecha tenga formato válido, mostrando mensajes de error en caso contrario.
- Al guardar con éxito, el nuevo gasto debe aparecer en la lista/tabla y reflejarse en las visualizaciones del periodo activo.


## 2. Editar gasto

Como usuario registrado, quiero editar un gasto existente, para corregir errores o actualizar la información registrada.
Criterios de verificación:

- El usuario debe poder abrir un formulario de edición con los datos actuales del gasto pre-rellenados.
- El sistema debe validar los cambios con las mismas reglas que en el alta de gastos y bloquear el guardado si hay errores.
- Tras guardar con éxito, el gasto actualizado debe verse reflejado en la lista y en las visualizaciones del periodo filtrado.


## 3. Eliminar gasto

Como usuario registrado, quiero eliminar un gasto, para limpiar registros duplicados o incorrectos.
Criterios de verificación:

- El sistema debe solicitar confirmación antes de eliminar definitivamente el gasto.
- Al confirmar, el gasto debe desaparecer de la lista y recalcularse totales, filtros y gráficos afectados.
- Si se cancela la confirmación, el gasto debe permanecer sin cambios.


## 4. Gestionar categorías

Como usuario registrado, quiero crear, renombrar y eliminar categorías, para organizar mis gastos más allá de las predefinidas.
Criterios de verificación:

- El sistema debe permitir crear categorías con nombre no vacío y no duplicado.
- Al renombrar o eliminar, el sistema debe garantizar la consistencia de los gastos asociados mediante reglas de reasignación o bloqueo según el diseño establecido.
- Las nuevas categorías deben estar disponibles de inmediato en el formulario de registro de gastos.


## 5. Listar y filtrar gastos

Como usuario registrado, quiero listar y filtrar gastos por meses, intervalos de fechas y categorías, para analizar subconjuntos específicos.
Criterios de verificación:

- La lista/tabla debe mostrar fecha, categoría, descripción opcional e importe, con ordenación por columna.
- Al aplicar filtros de mes, rango de fechas y/o categorías, la lista y las métricas deben actualizarse coherentemente.
- Debe mantenerse un rendimiento adecuado con desplazamiento o paginación cuando el volumen aumente.


## 6. Visualizaciones por categoría

Como usuario registrado, quiero ver gráficos de barras o circulares por categoría, para comprender visualmente la distribución de mis gastos.
Criterios de verificación:

- Debe mostrarse la distribución por categoría y los totales del periodo filtrado, coherente con la lista visible.
- Al cambiar filtros de fecha o categoría, los gráficos deben recalcularse y renderizarse de nuevo.
- Si no hay datos para el filtro, debe mostrarse un estado vacío informativo sin errores.


## 7. Vista calendario (opcional)

Como usuario registrado, quiero visualizar gastos en un calendario de día completo, para entender su distribución temporal.
Criterios de verificación:

- El calendario debe mostrar los gastos como eventos en su fecha, con detalles básicos.
- Al navegar entre días o intervalos, los eventos visibles deben actualizarse sin inconsistencias.
- En días sin gastos, el calendario debe indicarlo sin lanzar errores.


## 8. Línea de comandos (CLI)

Como usuario registrado, quiero registrar, modificar y borrar gastos desde una línea de comandos básica, para operar sin interfaz gráfica.
Criterios de verificación:

- Deben existir comandos “add”, “update” y “delete” con parámetros para cantidad, fecha, categoría e identificador según corresponda.
- El sistema debe validar parámetros e informar con mensajes claros de éxito o error en consola.
- Las operaciones realizadas en CLI deben persistir y reflejarse al abrir la aplicación gráfica.


## 9. Configurar alertas de gasto

Como usuario registrado, quiero configurar límites semanales, mensuales y por categoría, para recibir notificaciones al superar topes.
Criterios de verificación:

- Debe permitirse crear alertas con periodo, umbral y, opcionalmente, categoría vinculada.
- Al superar el umbral en el periodo o categoría, debe generarse una notificación legible y accesible.
- Al desactivar o ajustar una alerta, el cambio debe aplicarse de inmediato a futuros cálculos.


## 10. Historial de notificaciones

Como usuario registrado, quiero consultar el historial de notificaciones, para revisar avisos pasados y su contexto temporal.
Criterios de verificación:

- Debe listarse fecha, tipo de alerta y mensaje descriptivo de cada notificación.
- Debe poder marcar notificaciones como leídas sin eliminarlas del historial.
- El historial debe poder filtrarse por periodo y tipo de alerta.


## 11. Cuenta compartida (reparto equitativo)

Como miembro de una cuenta compartida, quiero crear una cuenta con participantes y reparto equitativo por defecto, para dividir gastos de forma sencilla.
Criterios de verificación:

- Al crear la cuenta, el reparto debe ser igual para todos y la lista de personas no podrá modificarse posteriormente.
- Al registrar un gasto a nombre de una persona, los saldos individuales deben recalcularse mostrando quién debe y cuánto.
- Deben evitarse participantes duplicados y nombres vacíos en la creación.


## 12. Cuenta compartida (porcentajes)

Como miembro de una cuenta compartida, quiero definir porcentajes personalizados que sumen 100%, para reflejar acuerdos de contribución distintos.
Criterios de verificación:

- El sistema debe validar que la suma de porcentajes sea exactamente 100% antes de crear la cuenta.
- Los saldos de cada gasto deben calcularse aplicando los porcentajes configurados.
- Los porcentajes definidos deben ser inmutables tras la creación de la cuenta.


## 13. Importar gastos externos

Como usuario registrado, quiero importar gastos desde ficheros de texto de plataformas bancarias en diferentes formatos, para cargar datos externos rápidamente.
Criterios de verificación:

- Debe aceptarse un fichero válido, adaptarlo al modelo y crear los gastos correspondientes.
- Debe evitar duplicados aplicando una regla de identificación o conciliación de registros.
- La arquitectura debe permitir añadir nuevos adaptadores de formato sin modificar la lógica de dominio.



