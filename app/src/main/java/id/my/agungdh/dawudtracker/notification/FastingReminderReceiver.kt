package id.my.agungdh.dawudtracker.notification

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import java.util.Calendar

class FastingReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            rescheduleAfterBoot(context)
        } else {
            showNotification(context)
        }
    }

    private fun rescheduleAfterBoot(context: Context) {
        val db = id.my.agungdh.dawudtracker.data.database.AppDatabase.getInstance(context)
        val repo = id.my.agungdh.dawudtracker.data.repository.SettingsRepository(db.appSettingDao())
        kotlinx.coroutines.runBlocking {
            val hour = repo.getSettingOnce(
                id.my.agungdh.dawudtracker.data.repository.SettingsRepository.Companion.KEY_NOTIFICATION_HOUR
            )?.value?.toIntOrNull() ?: id.my.agungdh.dawudtracker.data.repository.SettingsRepository.Companion.DEFAULT_NOTIFICATION_HOUR
            val minute = repo.getSettingOnce(
                id.my.agungdh.dawudtracker.data.repository.SettingsRepository.Companion.KEY_NOTIFICATION_MINUTE
            )?.value?.toIntOrNull() ?: id.my.agungdh.dawudtracker.data.repository.SettingsRepository.Companion.DEFAULT_NOTIFICATION_MINUTE
            scheduleReminder(context, hour, minute)
        }
    }

    companion object {
        private const val CHANNEL_ID = "fasting_reminder"
        private const val NOTIFICATION_ID = 1
        const val ACTION_SCHEDULE = "id.my.agungdh.dawudtracker.SCHEDULE_REMINDER"

        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = android.app.NotificationChannel(
                    CHANNEL_ID,
                    "Pengingat Puasa",
                    android.app.NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Pengingat harian untuk puasa Daud"
                }
                val manager = context.getSystemService(android.app.NotificationManager::class.java)
                manager.createNotificationChannel(channel)
            }
        }

        fun scheduleReminder(context: Context, hour: Int, minute: Int) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, FastingReminderReceiver::class.java).apply {
                action = ACTION_SCHEDULE
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (before(Calendar.getInstance())) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }

        fun cancelReminder(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, FastingReminderReceiver::class.java).apply {
                action = ACTION_SCHEDULE
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }

        private fun showNotification(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        context, Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) return
            }

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Pengingat Puasa Daud")
                .setContentText("Sudahkah kamu mencatat puasa hari ini?")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build()

            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        }
    }
}
