package dev.chsr.stonevault.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import dev.chsr.stonevault.screen.PasswordScreen
import dev.chsr.stonevault.screen.Screen
import dev.chsr.stonevault.screen.SettingsScreen
import dev.chsr.stonevault.ui.theme.StoneVaultTheme
import dev.chsr.stonevault.viewmodel.CredentialViewModel
import dev.chsr.stonevault.viewmodel.LocalizationViewModel
import javax.crypto.spec.SecretKeySpec


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database"
        ).build()

        val secretKeyBytes = intent.getByteArrayExtra("SECRET_KEY")
        val secretKey = SecretKeySpec(secretKeyBytes, "AES")

        val credentialViewModel: CredentialViewModel by viewModels {
            CredentialViewModel.CredentialViewModelFactory(db, secretKey)
        }
        val localizationViewModel: LocalizationViewModel by viewModels {
            LocalizationViewModel.LocalizationViewModelFactory(application)
        }

        setContent {
            StoneVaultTheme {
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
                    bottomBar = {
                        BottomNavigationBar(navController)
                    }) { innerPadding ->

                    NavHost(
                        navController = navController,
                        startDestination = Screen.PasswordList.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.PasswordList.route) {
                            PasswordListScreen(
                                credentialViewModel,
                                navController
                            )
                        }
                        composable(Screen.Settings.route) { SettingsScreen(localizationViewModel) }
                        composable(
                            route = "${Screen.Password.route}/{id}",
                            arguments = listOf(
                                navArgument("id") {
                                    type = NavType.IntType
                                    defaultValue = -1
                                }
                            )
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getInt("id") ?: 0
                            PasswordScreen(id, credentialViewModel, navController)
                        }
                    }
                }
            }
        }
    }
}