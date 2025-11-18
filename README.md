# ProyectoPlataformas
Repositorio para el proyecto de Plataformas Moviles "Mas Horas".

### Link del Diseño en Figma:
https://www.figma.com/design/dxByWYuaOoyP7a5wVtXvwh/Proyecto-%22--Horas%22?node-id=0-1&t=RZd5GbYPi5JLUwW5-1

### Video funcionamiento: 
https://github.com/user-attachments/assets/ce8800d0-83c4-4fd2-92a7-f1117a1bdb97

### Reflexión sobre la Arquitectura

#### Flujo de Datos
Para esta aplicación, implementamos un flujo de datos claro y robusto, basado en los principios de la arquitectura MVVM (Model-View-ViewModel). El flujo es unidireccional y reactivo, lo que hace que la app sea predecible y fácil de mantener.

Funciona así:
1.  **La Vista (UI)**, construida con Jetpack Compose, observa un `StateFlow` expuesto por el `ViewModel`. Esto significa que la pantalla reacciona automáticamente a cualquier cambio en el estado de los datos.
2.  Cuando el usuario interactúa con la UI (por ejemplo, al presionar un botón), se llama a una función en el **`ViewModel`**.
3.  El `ViewModel` procesa la lógica de negocio y solicita los datos necesarios al **`Repository`** correspondiente.
4.  El `Repository` es el único responsable de decidir de dónde obtener los datos, ya sea de una fuente remota (**Firebase**) o una fuente local (**DataStore** para preferencias). Esto abstrae por completo el origen de los datos del resto de la app.
5.  Los datos viajan de vuelta a través del `ViewModel`, que actualiza su `StateFlow`, provocando que la UI se recomponga y muestre la nueva información.

Este modelo asegura que nuestra UI sea un reflejo directo del estado de los datos y nos permite tener un código más limpio y desacoplado.

#### Decisiones Arquitectónicas
Decidí estructurar el proyecto siguiendo los principios de **Clean Architecture**. Esta decisión fue clave para asegurar que la aplicación fuera modular, escalable y fácil de probar. La arquitectura se divide en tres capas principales:

*   **`data`**: Aquí reside todo lo relacionado con el acceso a datos. Contiene las implementaciones de los repositorios y los `DataSource`, que se comunican directamente con Firebase y el `DataStore` local.
*   **`domain`**: Es el corazón de la aplicación. Contiene la lógica de negocio y las reglas del dominio (por ejemplo, los modelos de datos y las interfaces de los repositorios). Esta capa no conoce los detalles de implementación de las otras capas, lo que la hace totalmente independiente.
*   **`ui`**: Esta capa se encarga de mostrar los datos en la pantalla. Incluye los `ViewModels` que preparan los datos para la UI y los Composables que definen la apariencia y el comportamiento de la interfaz.

Elegir esta arquitectura nos permite que cada parte de la app tenga una responsabilidad única, facilitando el desarrollo y futuras actualizaciones.

#### Mejoras a Futuro
Aunque la aplicación cumple con sus objetivos principales, diseñamos la arquitectura pensando en la escalabilidad. Dos áreas clave para futuras expansiones son:

*   **Gamificación e Incentivos:** Para fomentar una mayor interacción y motivación, podríamos introducir un sistema de gamificación. Esto podría incluir la obtención de insignias por completar ciertas combinaciones de actividades (ej. "Semana Deportiva", "Voluntario del Mes") y la creación de un sistema de puntos que incentive la participación constante.
*   **Dashboard para Administradores y Docentes:** Podríamos expandir la funcionalidad para los usuarios con rol de `MAESTRO` creando un panel de control dedicado. Este dashboard les permitiría gestionar actividades (crear, editar, archivar), visualizar estadísticas de inscripción en tiempo real y generar reportes de participación, añadiendo un gran valor para la organización de las horas extracurriculares.
