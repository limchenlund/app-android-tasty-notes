package com.lund.tastynotes.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lund.tastynotes.R
import com.lund.tastynotes.models.RecipeType

class RecipeTypeAdapter(
    private val recipeTypes: List<RecipeType>,
    private val onItemClick: (RecipeType) -> Unit
) : RecyclerView.Adapter<RecipeTypeAdapter.RecipeTypeViewHolder>() {

    inner class RecipeTypeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.item_recipe_type_name_tv)

        fun bind(recipeType: RecipeType) {
            nameTextView.text = recipeType.name.replaceFirstChar { it.uppercase() }
            itemView.setOnClickListener { onItemClick(recipeType) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeTypeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe_type, parent, false)
        return RecipeTypeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeTypeViewHolder, position: Int) {
        holder.bind(recipeTypes[position])
    }

    override fun getItemCount(): Int = recipeTypes.size
} 