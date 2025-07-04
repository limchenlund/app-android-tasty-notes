package com.lund.tastynotes.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lund.tastynotes.R
import com.lund.tastynotes.models.Recipe
import com.lund.tastynotes.repository.RecipeRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RecipeListViewModel(
    private val repository: RecipeRepository,
    private val context: Context
) : ViewModel() {
    
    private val _recipes = MutableLiveData<List<Recipe>>()
    val recipes: LiveData<List<Recipe>> = _recipes
    
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    fun loadRecipesByType(typeId: Int) {
        viewModelScope.launch {
            try {
                repository.getRecipesByType(typeId).collect { recipes ->
                    _recipes.value = recipes
                }
            } catch (e: Exception) {
                _error.value = context.getString(R.string.error_failed_to_load_recipes, e.message)
            }
        }
    }
} 