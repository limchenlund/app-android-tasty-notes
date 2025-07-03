package com.lund.tastynotes.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lund.tastynotes.R
import com.lund.tastynotes.models.Recipe
import com.lund.tastynotes.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AllRecipesViewModel(
    private val repository: RecipeRepository,
    private val context: Context
) : ViewModel() {
    
    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    init {
        loadRecipes()
    }
    
    fun loadRecipes() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            repository.getAllRecipes()
                .catch { exception ->
                    _error.value = context.getString(R.string.error_failed_to_load_recipes, exception.message)
                    _isLoading.value = false
                }
                .collect { recipes ->
                    _recipes.value = recipes
                    _isLoading.value = false
                }
        }
    }
    
    fun refreshRecipes() {
        loadRecipes()
    }
} 