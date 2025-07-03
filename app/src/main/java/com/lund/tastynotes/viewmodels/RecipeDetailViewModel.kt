package com.lund.tastynotes.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lund.tastynotes.R
import com.lund.tastynotes.models.Recipe
import com.lund.tastynotes.repository.RecipeRepository
import kotlinx.coroutines.launch

class RecipeDetailViewModel(
    private val repository: RecipeRepository,
    private val context: Context
) : ViewModel() {
    
    private val _recipe = MutableLiveData<Recipe>()
    val recipe: LiveData<Recipe> = _recipe
    
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    private val _deleteSuccess = MutableLiveData<Boolean>()
    val deleteSuccess: LiveData<Boolean> = _deleteSuccess
    
    fun loadRecipe(recipeId: Long) {
        viewModelScope.launch {
            try {
                val recipe = repository.getRecipeById(recipeId)
                if (recipe != null) {
                    _recipe.value = recipe
                } else {
                    _error.value = context.getString(R.string.error_recipe_not_found)
                }
            } catch (e: Exception) {
                _error.value = context.getString(R.string.error_failed_to_load_recipe, e.message)
            }
        }
    }
    
    fun deleteRecipe(recipe: Recipe) {
        viewModelScope.launch {
            try {
                repository.deleteRecipe(recipe)
                _deleteSuccess.value = true
            } catch (e: Exception) {
                _error.value = context.getString(R.string.error_failed_to_delete_recipe, e.message)
            }
        }
    }
} 