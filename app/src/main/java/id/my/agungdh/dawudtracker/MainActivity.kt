package id.my.agungdh.dawudtracker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import id.my.agungdh.dawudtracker.notification.FastingReminderReceiver
import id.my.agungdh.dawudtracker.ui.navigation.AppNavigation
import id.my.agungdh.dawudtracker.ui.theme.DawudTrackerTheme
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("dawud_prefs", Context.MODE_PRIVATE)
        val language = prefs.getString("app_language", "system") ?: "system"
        val locale = when (language) {
            "en" -> Locale.forLanguageTag("en")
            "id" -> Locale.forLanguageTag("id")
            else -> Locale.getDefault()
        }
        Locale.setDefault(locale)
        val config = newBase.resources.configuration
        config.setLocale(locale)
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FastingReminderReceiver.createNotificationChannel(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        enableEdgeToEdge()
        setContent {
            DawudTrackerTheme {
                AppNavigation()
            }
        }
    }
}
