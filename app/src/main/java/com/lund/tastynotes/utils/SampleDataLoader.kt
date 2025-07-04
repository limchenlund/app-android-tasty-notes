package com.lund.tastynotes.utils

import android.content.Context
import com.google.gson.Gson
import com.lund.tastynotes.models.Recipe
import com.lund.tastynotes.models.SampleRecipesResponse
import com.lund.tastynotes.repository.RecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class SampleDataLoader(private val context: Context, private val recipeRepository: RecipeRepository) {

    suspend fun loadSampleDataIfNeeded() {
        withContext(Dispatchers.IO) {
            try {
                // Check if we already have recipes in the database
                // Will only add the recipes when user first launch their app
                val existingRecipes = recipeRepository.getAllRecipes().first()
                if (existingRecipes.isNotEmpty()) {
                    return@withContext
                }
                
                // Load sample data from JSON file
                val sampleRecipes = loadSampleRecipesFromJson()
                
                // Convert and insert into database
                sampleRecipes.forEach { sampleRecipe ->
                    val recipe = Recipe(
                        name = sampleRecipe.name,
                        imageUri = convertAssetPathToUri(sampleRecipe.imageUri),
                        ingredients = sampleRecipe.ingredients.joinToString("\n"),
                        steps = sampleRecipe.steps.joinToString("\n"),
                        recipeTypeId = sampleRecipe.recipeTypeId
                    )
                    recipeRepository.insertRecipe(recipe)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    private fun convertAssetPathToUri(assetPath: String): String {
        return if (assetPath.isNotEmpty()) {
            Constants.ASSETS_IMAGE_PREFIX + assetPath
        } else {
            ""
        }
    }
    
    private fun loadSampleRecipesFromJson(): List<com.lund.tastynotes.models.SampleRecipe> {
        return try {
            val inputStream = context.assets.open(Constants.SAMPLE_RECIPES_FILE_NAME)
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val gson = Gson()
            val response = gson.fromJson(jsonString, SampleRecipesResponse::class.java)
            response.recipes
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
} 