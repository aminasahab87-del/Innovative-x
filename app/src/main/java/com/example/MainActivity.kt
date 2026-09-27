package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.platform.TextToolbarStatus
import androidx.lifecycle.lifecycleScope
import com.example.data.firebase.FirebaseManager
import com.example.ui.InnovateXApp
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.InnovateXViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  private val viewModel: InnovateXViewModel by viewModels()

  private val emptyTextToolbar = object : TextToolbar {
    override val status: TextToolbarStatus
      get() = TextToolbarStatus.Hidden
    override fun hide() {}
    override fun showMenu(
      rect: Rect,
      onCopyRequested: (() -> Unit)?,
      onPasteRequested: (() -> Unit)?,
      onCutRequested: (() -> Unit)?,
      onSelectAllRequested: (() -> Unit)?
    ) {}
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
    Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
      android.util.Log.e("MainActivity", "Uncaught exception in thread ${thread.name}: ${throwable.message}", throwable)
      val msg = throwable.message?.lowercase() ?: ""
      val isMigrationError = msg.contains("migration") || msg.contains("room") || msg.contains("sqlite")
      if (isMigrationError) {
        android.util.Log.w("MainActivity", "Recoverable database/migration exception intercepted, preventing crash: ${throwable.message}")
      } else {
        defaultHandler?.uncaughtException(thread, throwable)
      }
    }

    enableEdgeToEdge()
    
    setContent {
      CompositionLocalProvider(LocalTextToolbar provides emptyTextToolbar) {
        MyApplicationTheme {
          InnovateXApp(viewModel = viewModel)
        }
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
