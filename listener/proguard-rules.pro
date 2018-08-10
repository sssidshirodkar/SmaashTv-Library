# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile



-keep class org.eclipse.paho.client.mqttv3.logging.JSR47Logger { *; }
-keep class org.eclipse.paho.client.mqttv3.** { *; }

-dontwarn okio.**
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *; }
-keep interface com.squareup.okhttp3.** { *; }
-dontwarn javax.annotation.Nullable
-dontwarn javax.annotation.ParametersAreNonnullByDefault
-dontwarn javax.annotation.**

-dontwarn okhttp3.internal.platform.ConscryptPlatform.**
-dontwarn org.conscrypt.**

-dontwarn retrofit2.Platform$Java8

# Proguard configuration for Jackson 2.x (fasterxml package instead of codehaus package)
-keep class com.fasterxml.jackson.databind.ObjectMapper {
    public <methods>;
    protected <methods>;
}
-keep class com.fasterxml.jackson.databind.ObjectWriter {
    public ** writeValueAsString(**);
}
-keepnames class com.fasterxml.jackson.** { *; }
-dontwarn com.fasterxml.jackson.databind.**
#############################################################

-keep class com.smaaash.tv.utils.SmashTv {*;}
-keep interface com.smaaash.tv.utils.SmashTv$SmaaashTvListener {*;}

-keep @**annotation** class * {*;}



##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

##---------------End: proguard configuration for Gson  ----------










#-injars      bin/classes
#-injars      libs
#-outjars     bin/classes-processed.jar
#-libraryjars /usr/local/java/android-sdk/platforms/android-9/android.jar

#-android
#-dontpreverify
#-repackageclasses ''
#-allowaccessmodification
#-optimizations !code/simplification/arithmetic
#-keepattributes *Annotation*
#
#-keep public class * extends android.app.Activity
#-keep public class * extends android.app.Application
#-keep public class * extends android.app.Service
#-keep public class * extends android.content.BroadcastReceiver
#-keep public class * extends android.content.ContentProvider
#
#-keep public class * extends android.view.View {
#      public <init>(android.content.Context);
#      public <init>(android.content.Context, android.util.AttributeSet);
#      public <init>(android.content.Context, android.util.AttributeSet, int);
#      public void set*(...);
#}
#
#-keepclasseswithmembers class * {
#    public <init>(android.content.Context, android.util.AttributeSet);
#}
#
#-keepclasseswithmembers class * {
#    public <init>(android.content.Context, android.util.AttributeSet, int);
#}
#
#-keepclassmembers class * extends android.content.Context {
#    public void *(android.view.View);
#    public void *(android.view.MenuItem);
#}
#
#-keepclassmembers class * implements android.os.Parcelable {
#    static ** CREATOR;
#}
#
#-keepclassmembers class **.R$* {
#    public static <fields>;
#}
#
#-keepclassmembers class * {
#    @android.webkit.JavascriptInterface <methods>;
#}

