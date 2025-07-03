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

class EditRecipeViewModel(
    private val repository: RecipeRepository,
    private val context: Context
) : ViewModel() {
    
    private val _recipe = MutableLiveData<Recipe>()
    val recipe: LiveData<Recipe> = _recipe
    
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    private val _updateSuccess = MutableLiveData<Boolean>()
    val updateSuccess: LiveData<Boolean> = _updateSuccess
    
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
    
    fun updateRecipe(
        recipeId: Long,
        name: String,
        imageUri: String?,
        ingredients: String,
        steps: String,
        recipeTypeId: Int
    ) {
        // Validation
        if (name.isBlank()) {
            _error.value = context.getString(R.string.validation_recipe_name_required)
            return
        }
        
        if (ingredients.isBlank()) {
            _error.value = context.getString(R.string.validation_ingredients_required)
            return
        }
        
        if (steps.isBlank()) {
            _error.value = context.getString(R.string.validation_steps_required)
            return
        }
        
        val updatedRecipe = Recipe(
            id = recipeId,
            name = name.trim(),
            imageUri = imageUri,
            ingredients = ingredients.trim(),
            steps = steps.trim(),
            recipeTypeId = recipeTypeId
        )
        
        viewModelScope.launch {
            try {
                repository.updateRecipe(updatedRecipe)
                _updateSuccess.value = true
            } catch (e: Exception) {
                _error.value = context.getString(R.string.error_failed_to_update_recipe, e.message)
            }
        }
    }
} 