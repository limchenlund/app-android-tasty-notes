package com.lund.tastynotes.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lund.tastynotes.R
import com.lund.tastynotes.activities.RecipeListActivity
import com.lund.tastynotes.adapters.RecipeTypeAdapter
import com.lund.tastynotes.models.RecipeType
import com.lund.tastynotes.utils.Constants

class AllTypesFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeTypeAdapter: RecipeTypeAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_all_types, container, false)

        this.findAllViews(view)
        this.setupViews()
        this.setupAdapter()

        return view
    }

    private fun setupViews() {
        this.recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun setupAdapter() {
        try {
            val inputStream = requireContext().assets.open(Constants.RECIPE_FILE_NAME)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            
            val json = String(buffer, Charsets.UTF_8)
            val gson = Gson()
            val type = object : TypeToken<List<RecipeType>>() {}.type
            val recipeTypes: List<RecipeType> = gson.fromJson(json, type)
            
            this.recipeTypeAdapter = RecipeTypeAdapter(recipeTypes) { recipeType ->
                val intent = RecipeListActivity.newIntent(requireContext(), recipeType.id)
                startActivity(intent)
            }
            this.recyclerView.adapter = recipeTypeAdapter
            
        } catch (e: Exception) {
            Toast.makeText(context, getString(R.string.error_loading_recipe_types), Toast.LENGTH_LONG).show()
        }
    }

    private fun findAllViews(view: View) {
        this.recyclerView = view.findViewById(R.id.recipe_types_recycler_view)
    }
}