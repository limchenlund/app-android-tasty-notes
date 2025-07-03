package com.lund.tastynotes.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lund.tastynotes.R
import com.lund.tastynotes.models.Recipe

class RecipeAdapter(
    private val recipes: List<Recipe>,
    private val onItemClick: (Recipe) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.item_recipe_name_tv)
        private val imageView: ImageView = itemView.findViewById(R.id.item_recipe_image_iv)

        fun bind(recipe: Recipe) {
            nameTextView.text = recipe.name
            
            // Load image if available
            if (!recipe.imageUri.isNullOrBlank()) {
                Glide.with(itemView.context)
                    .load(recipe.imageUri)
                    .placeholder(R.drawable.ic_food_placeholder)
                    .error(R.drawable.ic_food_placeholder)
                    .centerCrop()
                    .into(imageView)
            } else {
                // Set placeholder if no image
                imageView.setImageResource(R.drawable.ic_food_placeholder)
            }
            
            itemView.setOnClickListener { onItemClick(recipe) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(recipes[position])
    }

    override fun getItemCount(): Int = recipes.size
} 