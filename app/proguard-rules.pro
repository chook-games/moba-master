# MOBA Master ProGuard Rules
# Keep all game entities and engine classes (they use reflection-like serialization patterns)
-keep class com.moba.master.entity.** { *; }
-keep class com.moba.master.engine.** { *; }
-keep class com.moba.master.ai.** { *; }
-keep class com.moba.master.combat.** { *; }
-keep class com.moba.master.items.** { *; }
-keep class com.moba.master.map.** { *; }

# Keep Compose-related classes
-keep class androidx.compose.** { *; }

# Keep Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Keep enum classes (used extensively in FSM)
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep data classes with copy/destructors
-keepclassmembers class * {
    *** copy();
    *** copy$default(...);
}
