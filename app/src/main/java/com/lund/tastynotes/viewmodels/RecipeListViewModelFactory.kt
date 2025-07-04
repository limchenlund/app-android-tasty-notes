package com.lund.tastynotes.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lund.tastynotes.repository.RecipeRepository

class RecipeListViewModelFactory(
    private val repository: RecipeRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecipeListViewModel(repository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 