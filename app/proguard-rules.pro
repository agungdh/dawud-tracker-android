# Room
-keep class id.my.agungdh.dawudtracker.data.entity.** { *; }
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Compose
-keep class androidx.compose.** { *; }

# Navigation
-keep class * extends androidx.navigation.NavType

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Enum classes
-keepclassmembers enum * { *; }

# Serializable
-keepclassmembers class * implements java.io.Serializable { *; }
-keepclassmembers class * implements android.os.Parcelable { *; }

# ViewModels
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keep class * extends androidx.lifecycle.AndroidViewModel { *; }

# KSP generated
-keep class id.my.agungdh.dawudtracker.**_Impl { *; }

# Keep generated classes
-keep class ** extends ** { *; }
