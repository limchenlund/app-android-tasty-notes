package com.lund.tastynotes.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lund.tastynotes.repository.RecipeRepository

class RecipeDetailViewModelFactory(
    private val repository: RecipeRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecipeDetailViewModel(repository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 