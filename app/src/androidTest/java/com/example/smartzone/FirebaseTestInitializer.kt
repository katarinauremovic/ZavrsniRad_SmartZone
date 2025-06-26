package com.example.smartzone
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions


object FirebaseTestInitializer {

    private var initialized = false

    fun initIfNeeded() {
        if (initialized) return

        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val options = FirebaseOptions.Builder()
            .setProjectId("smartzone-test-45961")
            .setApplicationId("1:770906986009:android:f662e9e9658a1eddbcfc88")
            .setApiKey("AIzaSyCVntPOubaBaMA0suvMalxl7E26za2GDHA")
            .setDatabaseUrl("https://smartzone-test.firebaseio.com")
            .build()

        FirebaseApp.initializeApp(context, options, "test")
        initialized = true
    }
}