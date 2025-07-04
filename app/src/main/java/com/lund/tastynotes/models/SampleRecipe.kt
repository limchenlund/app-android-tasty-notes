package com.lund.tastynotes.models

data class SampleRecipe(
    val name: String,
    val imageUri: String,
    val ingredients: List<String>,
    val steps: List<String>,
    val recipeTypeId: Int
)

data class SampleRecipesResponse(
    val recipes: List<SampleRecipe>
) 