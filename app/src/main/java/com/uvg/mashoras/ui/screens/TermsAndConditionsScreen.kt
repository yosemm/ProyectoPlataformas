package com.uvg.mashoras.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsAndConditionsScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Términos y Condiciones",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Última actualización
            Text(
                text = "Última actualización: Noviembre 2025",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Introducción
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Bienvenido a MasHoras",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "MasHoras es una aplicación desarrollada por estudiantes de la Universidad del Valle de Guatemala (UVG) para facilitar la gestión y seguimiento de actividades estudiantiles.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Justify
                    )
                }
            }

            // Sección 1: Aceptación de términos
            SectionTitle("1. Aceptación de los Términos")
            SectionContent(
                "Al acceder y utilizar MasHoras, aceptas estar sujeto a estos términos y condiciones. " +
                "Si no estás de acuerdo con alguna parte de estos términos, no debes utilizar la aplicación."
            )

            // Sección 2: Uso de la aplicación
            SectionTitle("2. Uso de la Aplicación")
            SectionContent(
                "MasHoras está diseñada exclusivamente para estudiantes y personal de la Universidad del Valle de Guatemala. " +
                "El acceso requiere un correo electrónico institucional válido (@uvg.edu.gt)."
            )
            BulletPoint("Debes proporcionar información precisa y actualizada durante el registro")
            BulletPoint("Eres responsable de mantener la confidencialidad de tu cuenta")
            BulletPoint("No debes compartir tu cuenta con terceros")
            BulletPoint("Debes notificar inmediatamente cualquier uso no autorizado de tu cuenta")

            // Sección 3: Actividades y horas
            SectionTitle("3. Gestión de Actividades y Horas")
            SectionContent(
                "La aplicación permite a los estudiantes inscribirse en actividades y realizar seguimiento de sus horas completadas:"
            )
            BulletPoint("Las actividades son creadas y gestionadas por maestros autorizados")
            BulletPoint("La inscripción en actividades está sujeta a disponibilidad de cupos")
            BulletPoint("Las horas completadas se registran automáticamente al finalizar actividades")
            BulletPoint("Los estudiantes son responsables de verificar su progreso regularmente")

            // Sección 4: Roles de usuario
            SectionTitle("4. Roles de Usuario")
            SectionContent("Existen dos tipos de usuarios en MasHoras:")
            
            Text(
                text = "Estudiantes:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            BulletPoint("Pueden inscribirse en actividades disponibles")
            BulletPoint("Pueden ver su progreso de horas")
            BulletPoint("Pueden establecer metas de horas personales")
            BulletPoint("Reciben notificaciones de nuevas actividades")
            
            Text(
                text = "Maestros:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            BulletPoint("Pueden crear y gestionar actividades")
            BulletPoint("Pueden editar y eliminar sus actividades")
            BulletPoint("Pueden finalizar actividades para registrar horas")
            BulletPoint("Pueden ver la lista de estudiantes inscritos")

            // Sección 5: Privacidad de datos
            SectionTitle("5. Privacidad y Protección de Datos")
            SectionContent(
                "Nos comprometemos a proteger tu información personal:"
            )
            BulletPoint("Recopilamos solo la información necesaria para el funcionamiento de la app")
            BulletPoint("Tu correo institucional y datos académicos se mantienen confidenciales")
            BulletPoint("No compartimos tu información con terceros sin tu consentimiento")
            BulletPoint("Utilizamos Firebase para el almacenamiento seguro de datos")

            // Sección 6: Notificaciones
            SectionTitle("6. Notificaciones")
            SectionContent(
                "La aplicación puede enviar notificaciones sobre:"
            )
            BulletPoint("Nuevas actividades disponibles para tu carrera")
            BulletPoint("Actividades finalizadas en las que estás inscrito")
            BulletPoint("Actualizaciones importantes de la aplicación")
            
            Text(
                text = "Puedes desactivar las notificaciones en cualquier momento desde la configuración de tu dispositivo.",
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 8.dp),
                textAlign = TextAlign.Justify
            )

            // Sección 7: Conducta del usuario
            SectionTitle("7. Conducta del Usuario")
            SectionContent("Al usar MasHoras, te comprometes a:")
            BulletPoint("No utilizar la aplicación para actividades ilegales o no autorizadas")
            BulletPoint("No intentar acceder a cuentas de otros usuarios")
            BulletPoint("No manipular o falsificar información de actividades u horas")
            BulletPoint("Respetar a otros usuarios y mantener una conducta profesional")

            // Sección 8: Limitación de responsabilidad
            SectionTitle("8. Limitación de Responsabilidad")
            SectionContent(
                "MasHoras es un proyecto estudiantil desarrollado con fines educativos y de apoyo a la comunidad UVG. " +
                "La aplicación se proporciona \"tal cual\" sin garantías de ningún tipo. No nos hacemos responsables de:"
            )
            BulletPoint("Errores o inexactitudes en el registro de horas")
            BulletPoint("Pérdida de datos debido a fallos técnicos")
            BulletPoint("Interrupciones en el servicio")
            BulletPoint("Decisiones académicas basadas en la información de la app")

            // Sección 9: Modificaciones
            SectionTitle("9. Modificaciones a los Términos")
            SectionContent(
                "Nos reservamos el derecho de modificar estos términos en cualquier momento. " +
                "Las modificaciones entrarán en vigor inmediatamente después de su publicación en la aplicación. " +
                "Tu uso continuado de MasHoras después de cualquier cambio constituye tu aceptación de los nuevos términos."
            )

            // Sección 10: Propiedad intelectual
            SectionTitle("10. Propiedad Intelectual")
            SectionContent(
                "MasHoras y todo su contenido, características y funcionalidad son propiedad de sus desarrolladores " +
                "y están protegidos por las leyes de propiedad intelectual aplicables."
            )

            // Sección 11: Terminación
            SectionTitle("11. Terminación de Cuenta")
            SectionContent(
                "Puedes eliminar tu cuenta en cualquier momento. Nos reservamos el derecho de suspender o " +
                "terminar tu acceso a la aplicación si violas estos términos o si tu correo institucional deja de ser válido."
            )

            // Sección 12: Contacto
            SectionTitle("12. Contacto")
            SectionContent(
                "Si tienes preguntas sobre estos términos y condiciones, puedes contactarnos a través de los " +
                "canales oficiales de la Universidad del Valle de Guatemala."
            )

            // Footer
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "De UVGenios para UVGenios",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Desarrollado por estudiantes de\nUniversidad del Valle de Guatemala",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón de aceptar
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(
                    text = "Entendido",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
}

@Composable
private fun SectionContent(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        modifier = Modifier.padding(bottom = 8.dp),
        textAlign = TextAlign.Justify
    )
}

@Composable
private fun BulletPoint(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, bottom = 4.dp)
    ) {
        Text(
            text = "• ",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = text,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Justify
        )
    }
}