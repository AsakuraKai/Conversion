package com.example.conversion

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.initialize
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for the Conversion app
 *
 * Handles:
 * - Hilt dependency injection setup
 * - Firebase initialization
 * - Global app configuration
 */
@HiltAndroidApp
class ConversionApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        Firebase.initialize(this)
    }
}
