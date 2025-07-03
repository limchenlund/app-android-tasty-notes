package com.lund.tastynotes

import android.app.Application
import com.lund.tastynotes.database.AppDatabase

class TastyNotesApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Initialize database
        AppDatabase.getDatabase(this)
    }
} 