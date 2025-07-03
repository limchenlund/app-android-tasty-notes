package com.lund.tastynotes.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lund.tastynotes.R
import com.lund.tastynotes.adapters.RecipeAdapter
import com.lund.tastynotes.database.AppDatabase
import com.lund.tastynotes.models.Recipe
import com.lund.tastynotes.repository.RecipeRepository
import com.lund.tastynotes.viewmodels.AllRecipesViewModel
import com.lund.tastynotes.viewmodels.AllRecipesViewModelFactory
import kotlinx.coroutines.launch

class AllRecipesFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var viewModel: AllRecipesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_all_recipes, container, false)
        
        setupViewModel()
        setupRecyclerView(view)
        observeRecipes()
        
        return view
    }

    private fun setupViewModel() {
        val database = AppDatabase.getDatabase(requireContext())
        val repository = RecipeRepository(database.recipeDao())
        val factory = AllRecipesViewModelFactory(repository, requireContext())
        viewModel = ViewModelProvider(this, factory)[AllRecipesViewModel::class.java]
    }

    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.all_recipes_recycler_view)
        
        // To show 2 item per row
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        
        // Initialize adapter with empty list while waiting for data to load
        recipeAdapter = RecipeAdapter(emptyList()) { recipe ->
            onRecipeClick(recipe)
        }
        
        recyclerView.adapter = recipeAdapter
    }

    private fun observeRecipes() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.recipes.collect { recipes ->
                recipeAdapter = RecipeAdapter(recipes) { recipe ->
                    onRecipeClick(recipe)
                }
                recyclerView.adapter = recipeAdapter
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error.collect { error ->
                error?.let {
                    Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun onRecipeClick(recipe: Recipe) {
        // TODO show recipe detail activity
    }

    override fun onResume() {
        super.onResume()
        // Refresh recipes when fragment becomes visible
        viewModel.refreshRecipes()
    }
}