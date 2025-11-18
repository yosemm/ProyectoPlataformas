# ProyectoPlataformas - Incluyendo implementación de Firebase.
Repositorio para el proyecto de Plataformas Moviles "Mas Horas".

### Link del Diseño en Figma:
https://www.figma.com/design/dxByWYuaOoyP7a5wVtXvwh/Proyecto-%22--Horas%22?node-id=0-1&t=RZd5GbYPi5JLUwW5-1

### Videos de funcionamiento (varios): 

#### Registrar cuenta:
[Registro.webm](https://github.com/user-attachments/assets/e4b1e486-b95e-43dd-b3a3-a9b7baf70a1f)

#### Iniciar sesión:
https://github.com/user-attachments/assets/edcf4e8b-998a-4c31-b84d-302a1acf1905

#### Editar una actividad como maestro y finalizarla. 
[Editar y finalizar actividad.webm](https://github.com/user-attachments/assets/c7b2e13b-d744-4524-ac1e-a1f5bbf7f1d2)

#### Inscribirse a una actividad como estudiante. 
[Inscribirse a actividad.webm](https://github.com/user-attachments/assets/a4d675d2-e68c-4849-a65a-9f63d0e527e6)

### Reflexión sobre la Arquitectura

#### Flujo de Datos
Para esta aplicación, se implementó un flujo de datos claro y robusto, basado en los principios de la arquitectura MVVM (Model-View-ViewModel). El flujo es unidireccional y reactivo, lo que hace que la app sea predecible y fácil de mantener.

Funciona así:
1.  **La Vista (UI)**, construida con Jetpack Compose, observa un `StateFlow` expuesto por el `ViewModel`. Esto significa que la pantalla reacciona automáticamente a cualquier cambio en el estado de los datos.
2.  Cuando el usuario interactúa con la UI (por ejemplo, al presionar un botón), se llama a una función en el **`ViewModel`**.
3.  El `ViewModel` procesa la lógica de negocio y solicita los datos necesarios al **`Repository`** correspondiente.
4.  El `Repository` es el único responsable de decidir de dónde obtener los datos, ya sea de una fuente remota (**Firebase**) o una fuente local (**DataStore** para preferencias). Esto abstrae por completo el origen de los datos del resto de la app.
5.  Los datos viajan de vuelta a través del `ViewModel`, que actualiza su `StateFlow`, provocando que la UI se recomponga y muestre la nueva información.

Este modelo asegura que la UI sea un reflejo directo del estado de los datos y permite tener un código más limpio y desacoplado.

#### Decisiones Arquitectónicas
Se estructuró el proyecto siguiendo los principios de **Clean Architecture**. La arquitectura se divide en tres capas principales:

*   **`data`**: Aquí reside todo lo relacionado con el acceso a datos. Contiene las implementaciones de los repositorios y los `DataSource`, que se comunican directamente con Firebase y el `DataStore` local.
*   **`domain`**: Es el corazón de la aplicación. Contiene la lógica de negocio y las reglas del dominio (por ejemplo, los modelos de datos y las interfaces de los repositorios). Esta capa no conoce los detalles de implementación de las otras capas, lo que la hace totalmente independiente.
*   **`ui`**: Esta capa se encarga de mostrar los datos en la pantalla. Incluye los `ViewModels` que preparan los datos para la UI y los Composables que definen la apariencia y el comportamiento de la interfaz.

Elegir esta arquitectura permite que cada parte de la app tenga una responsabilidad única, facilitando el desarrollo y futuras actualizaciones.

#### Mejoras a Futuro
Aunque la aplicación cumple con sus objetivos principales, dos áreas clave para futuras expansiones son:

*   **Gamificación e Incentivos:** Para fomentar una mayor interacción y motivación, se podría introducir un sistema de gamificación. Esto podría incluir la obtención de insignias por completar ciertas combinaciones de actividades (ej. "Semana Deportiva", "Voluntario del Mes") y la creación de un sistema de puntos que incentive la participación constante. Esto puede ir más allá de las horas becas, por ejemplo, podemos incluir charlas delvas u horas de extensión.
*   **Dashboard para Administradores y Docentes:** Se podría expandir la funcionalidad para los usuarios con rol de `MAESTRO` creando un panel de control dedicado. Este dashboard les permitiría visualizar estadísticas de inscripción en tiempo real y generar reportes de participación, añadiendo un gran valor para la organización de las horas extracurriculares.
