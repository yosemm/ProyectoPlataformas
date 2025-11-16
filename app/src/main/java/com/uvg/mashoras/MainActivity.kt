package com.uvg.mashoras

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uvg.mashoras.data.models.UserRole
import com.uvg.mashoras.navigation.AppNavigation
import com.uvg.mashoras.navigation.AppScreens
import com.uvg.mashoras.presentation.activities.ActivityNotificationsObserver
import com.uvg.mashoras.ui.components.BottomNavBar
import com.uvg.mashoras.ui.theme.MasHorasTheme
import com.uvg.mashoras.utils.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : ComponentActivity() {

    // Observer para escuchar cambios en Firestore y disparar notificaciones
    private lateinit var notificationsObserver: ActivityNotificationsObserver

    // Launcher para pedir permiso de notificaciones en Android 13+
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { _ ->
            // Si quieres hacer algo cuando lo acepten / rechacen, lo haces aquí
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        android.util.Log.d("DEBUG_MAIN", "onCreate de MainActivity")
        enableEdgeToEdge()

        // Crear el canal de notificaciones
        NotificationHelper.createChannel(this)
        askNotificationPermissionIfNeeded()

        // Instancias de Firebase
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        val notificationHelper = NotificationHelper(this)

        notificationsObserver = ActivityNotificationsObserver(
            context = this,
            firestore = firestore,
            auth = auth,
            notificationHelper = notificationHelper
        )

        // Solo tiene sentido observar si el usuario está logueado
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Obtener los datos del usuario desde Firestore
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val userDoc = firestore.collection("users")
                        .document(currentUser.uid)
                        .get()
                        .await()
                    
                    if (userDoc.exists()) {
                        val carrera = userDoc.getString("carrera") ?: "all"
                        val rolString = userDoc.getString("rol") ?: "ESTUDIANTE"
                        val isTeacher = rolString == UserRole.MAESTRO.name
                        
                        android.util.Log.d(
                            "DEBUG_MAIN", 
                            "Usuario cargado: carrera=$carrera, rol=$rolString, isTeacher=$isTeacher"
                        )
                        
                        notificationsObserver.startObserving(
                            userCareer = carrera,
                            isTeacher = isTeacher
                        )
                    } else {
                        android.util.Log.w("DEBUG_MAIN", "No se encontró documento de usuario en Firestore")
                    }
                } catch (e: Exception) {
                    android.util.Log.e("DEBUG_MAIN", "Error al cargar datos de usuario", e)
                }
            }
        }

        setContent {
            android.util.Log.d("DEBUG_MAIN", "Entrando a setContent")
            MasHorasTheme {
                MainScaffold()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::notificationsObserver.isInitialized) {
            notificationsObserver.stopObserving()
        }
    }

    private fun askNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionCheck = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            )
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@Composable
fun MainScaffold() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Solo mostramos el bottom nav en estas pantallas
            if (currentRoute in listOf(
                    AppScreens.AvailableActivitiesScreen.route,
                    AppScreens.HistoryScreen.route,
                    AppScreens.ProfileScreen.route
                )
            ) {
                BottomNavBar(navController)
            }
        }
    ) { innerPadding ->
        AppNavigation(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}