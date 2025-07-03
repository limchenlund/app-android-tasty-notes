package com.lund.tastynotes.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lund.tastynotes.R
import com.lund.tastynotes.models.Recipe
import com.lund.tastynotes.repository.RecipeRepository
import kotlinx.coroutines.launch

class AddRecipeViewModel(
    private val repository: RecipeRepository,
    private val context: Context
) : ViewModel() {
    
    fun saveRecipe(
        name: String,
        imageUri: String?,
        ingredients: String,
        steps: String,
        recipeTypeId: Int,
        onSuccess: (Long) -> Unit,
        onError: (String) -> Unit
    ) {
        if (name.isBlank()) {
            onError(context.getString(R.string.validation_recipe_name_required))
            return
        }
        
        if (ingredients.isBlank()) {
            onError(context.getString(R.string.validation_ingredients_required))
            return
        }
        
        if (steps.isBlank()) {
            onError(context.getString(R.string.validation_steps_required))
            return
        }
        
        val recipe = Recipe(
            name = name.trim(),
            imageUri = imageUri,
            ingredients = ingredients.trim(),
            steps = steps.trim(),
            recipeTypeId = recipeTypeId
        )
        
        viewModelScope.launch {
            try {
                val recipeId = repository.insertRecipe(recipe)
                onSuccess(recipeId)
            } catch (e: Exception) {
                onError(context.getString(R.string.error_failed_to_save_recipe, e.message))
            }
        }
    }
} 