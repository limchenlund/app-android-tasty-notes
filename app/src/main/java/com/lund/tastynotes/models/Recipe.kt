package com.lund.tastynotes.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val imageUri: String?, // Store image URI as string
    val ingredients: String, // Store as JSON string
    val steps: String, // Store as JSON string
    val recipeTypeId: Int,
    val createdAt: Long = System.currentTimeMillis()
) 