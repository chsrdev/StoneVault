package dev.chsr.stonevault.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import dev.chsr.stonevault.viewmodel.AppViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SplashActivity : ComponentActivity() {
    val viewModel: AppViewModel by viewModels {
        AppViewModel.AppViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (viewModel.getVector().isEmpty()) {
            startActivity(Intent(this@SplashActivity, CreateMasterPasswordActivity::class.java))
        } else {
            startActivity(Intent(this@SplashActivity, EnterMasterPasswordActivity::class.java))
        }

        finish()
    }
}