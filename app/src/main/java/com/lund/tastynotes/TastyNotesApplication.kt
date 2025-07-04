package com.lund.tastynotes

import android.app.Application
import com.lund.tastynotes.database.AppDatabase
import com.lund.tastynotes.repository.RecipeRepository
import com.lund.tastynotes.utils.SampleDataLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TastyNotesApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Initialize database
        val database = AppDatabase.getDatabase(this)
        val recipeRepository = RecipeRepository(database.recipeDao())
        
        // Load sample recipe in background
        CoroutineScope(Dispatchers.IO).launch {
            val sampleDataLoader = SampleDataLoader(this@TastyNotesApplication, recipeRepository)
            sampleDataLoader.loadSampleDataIfNeeded()
        }
    }
} 