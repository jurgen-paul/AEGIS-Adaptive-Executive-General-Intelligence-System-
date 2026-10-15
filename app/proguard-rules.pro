# ============================================================================
# AEGIS — High-Performance ProGuard / R8 Production Optimization & Security Rules
# ============================================================================

# ----------------------------------------------------------------------------
# 1. General Optimization & Obfuscation Flags
# ----------------------------------------------------------------------------
-repackageclasses 'com.aistudio.aegis.internal'
-allowaccessmodification
-mergeinterfacesaggressively

# Preserve line numbers and source file attributes for actionable crash stacktraces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Preserve critical runtime annotations
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod,Exceptions

# ----------------------------------------------------------------------------
# 2. Release Security: Strip Debug and Verbose Logs
# ----------------------------------------------------------------------------
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
}

# ----------------------------------------------------------------------------
# 3. Android Room Database Persistence Rules
# ----------------------------------------------------------------------------
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
-keep class androidx.room.** { *; }

# Keep all Room Entities, DAOs, and Type Converters
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-keepclassmembers class * {
    @androidx.room.TypeConverter *;
}
-keep class * extends androidx.room.RoomDatabase$Callback { *; }

# ----------------------------------------------------------------------------
# 4. Android Biometrics & Cryptographic Hardware Providers
# ----------------------------------------------------------------------------
-keep class androidx.biometric.** { *; }
-keep class androidx.biometric.BiometricPrompt** { *; }
-keep class androidx.biometric.BiometricPrompt$PromptInfo** { *; }
-keep class androidx.biometric.BiometricPrompt$AuthenticationCallback { *; }

# Preserve AndroidKeyStore, Java Cryptography Architecture (JCA), and Cipher interfaces
-keepclassmembers class * implements java.security.Provider { *; }
-keep class javax.crypto.** { *; }
-keep class java.security.** { *; }

# ----------------------------------------------------------------------------
# 5. Moshi JSON Serialization & Reflection Rules
# ----------------------------------------------------------------------------
-dontwarn com.squareup.moshi.**
-keep class com.squareup.moshi.** { *; }
-keep @com.squareup.moshi.JsonClass class * { *; }
-keepclassmembers class * {
    @com.squareup.moshi.Json *;
}
-keep class * extends com.squareup.moshi.JsonAdapter { *; }
-keepclassmembers class * {
    @com.squareup.moshi.FromJson *;
    @com.squareup.moshi.ToJson *;
}

# ----------------------------------------------------------------------------
# 6. Retrofit, OkHttp & Okio Networking Rules
# ----------------------------------------------------------------------------
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepclasseswithmembers interface * {
    @retrofit2.http.* <methods>;
}

-dontwarn okhttp3.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okio.**
-keep class okio.** { *; }
-dontwarn javax.annotation.**

# ----------------------------------------------------------------------------
# 7. Kotlin Coroutines & Flow Runtime Rules
# ----------------------------------------------------------------------------
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# ----------------------------------------------------------------------------
# 8. Jetpack Compose & Material 3 Runtime Rules
# ----------------------------------------------------------------------------
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.material3.** { *; }
-dontwarn androidx.compose.**

# ----------------------------------------------------------------------------
# 9. AEGIS Domain & Security Data Models
# ----------------------------------------------------------------------------
-keep class com.example.data.** { *; }
-keep class com.example.service.BiometricAuthManager { *; }
-keep class com.example.service.GoogleSheetsIntegrationManager { *; }
-keep class com.example.service.GoogleSheetsSyncConfig { *; }
-keep class com.example.service.GoogleSheetsSyncResult { *; }
-keep class com.example.service.GoogleFormEntryMapping { *; }
