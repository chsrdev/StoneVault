package dev.chsr.stonevault.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import dev.chsr.stonevault.activity.component.BottomNavigationBar
import dev.chsr.stonevault.database.AppDatabase
import dev.chsr.stonevault.screen.PasswordListScreen
import dev.chsr.stonevault.screen.Screen
import dev.chsr.stonevault.screen.SettingsScreen
import dev.chsr.stonevault.security.SessionManager
import dev.chsr.stonevault.ui.theme.StoneVaultTheme
import dev.chsr.stonevault.viewmodel.AppViewModel
import dev.chsr.stonevault.viewmodel.CredentialViewModel
import dev.chsr.stonevault.viewmodel.LocalizationViewModel
import dev.chsr.stonevault.viewmodel.theme.ThemeMode
import dev.chsr.stonevault.viewmodel.theme.ThemeViewModel
import javax.crypto.spec.SecretKeySpec


class MainActivity : AppCompatActivity() {
    private val sessionHandler = Handler(Looper.getMainLooper())

    private val clearSessionRunnable = Runnable {
        SessionManager.clear()
        startActivity(
            Intent(this, EnterMasterPasswordActivity::class.java)
        )
        finish()
    }

    override fun onStop() {
        super.onStop()

        sessionHandler.postDelayed(
            clearSessionRunnable,
            30_000
        )
    }

    override fun onStart() {
        super.onStart()
        sessionHandler.removeCallbacks(clearSessionRunnable)
        if (!SessionManager.isInitialized()) {
            startActivity(
                Intent(this, EnterMasterPasswordActivity::class.java)
            )
            finish()
            return
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database"
        ).build()

//        val secretKeyBytes = intent.getByteArrayExtra("SECRET_KEY")
//        if (secretKeyBytes != null) {
//            SessionManager.setKey(
//                SecretKeySpec(secretKeyBytes, "AES")
//            )
//        }

        val credentialViewModel: CredentialViewModel by viewModels {
            CredentialViewModel.CredentialViewModelFactory(db)
        }
        val localizationViewModel: LocalizationViewModel by viewModels {
            LocalizationViewModel.LocalizationViewModelFactory(application)
        }
        val themeViewModel: ThemeViewModel by viewModels {
            ThemeViewModel.ThemeViewModelFactory(application)
        }
        val appViewModel: AppViewModel by viewModels {
            AppViewModel.AppViewModelFactory(application)
        }

        setContent {
            val currentTheme by themeViewModel.currentTheme.collectAsState()
            val isDarkTheme = when (currentTheme) {
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }
            StoneVaultTheme(darkTheme = isDarkTheme) {
                key(isDarkTheme) {
                    AppContent(
                        credentialViewModel = credentialViewModel,
                        localizationViewModel = localizationViewModel,
                        themeViewModel = themeViewModel,
                        appViewModel = appViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun AppContent(
    credentialViewModel: CredentialViewModel,
    localizationViewModel: LocalizationViewModel,
    themeViewModel: ThemeViewModel,
    appViewModel: AppViewModel
) {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.PasswordList.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.PasswordList.route) {
                PasswordListScreen(
                    credentialViewModel,
                    localizationViewModel,
                    navController,
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(localizationViewModel, themeViewModel, appViewModel, credentialViewModel)
            }
        }
    }
}