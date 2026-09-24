package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.data.firebase.FirebaseManager
import com.example.ui.InnovateXApp
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.InnovateXViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  private val viewModel: InnovateXViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
    Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
      android.util.Log.e("MainActivity", "Uncaught exception in thread ${thread.name}: ${throwable.message}", throwable)
      defaultHandler?.uncaughtException(thread, throwable)
    }

    enableEdgeToEdge()
    
    setContent {
      MyApplicationTheme {
        InnovateXApp(viewModel = viewModel)
      }
    }

    lifecycleScope.launch(Dispatchers.IO) {
      try {
        FirebaseManager.initialize(applicationContext)
      } catch (t: Throwable) {
        android.util.Log.e("MainActivity", "Firebase init safe catch: ${t.message}", t)
      }
    }
  }
}
