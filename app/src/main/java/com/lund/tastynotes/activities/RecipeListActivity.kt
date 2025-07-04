package com.lund.tastynotes.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lund.tastynotes.R
import com.lund.tastynotes.adapters.RecipeAdapter
import com.lund.tastynotes.database.AppDatabase
import com.lund.tastynotes.models.RecipeType
import com.lund.tastynotes.repository.RecipeRepository
import com.lund.tastynotes.utils.Constants
import com.lund.tastynotes.viewmodels.RecipeListViewModel
import com.lund.tastynotes.viewmodels.RecipeListViewModelFactory
import java.io.InputStream

class RecipeListActivity : AppCompatActivity() {
    private lateinit var backImageView: ImageView
    private lateinit var titleTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var noRecipesTextView: TextView
    private lateinit var viewModel: RecipeListViewModel
    private lateinit var recipeAdapter: RecipeAdapter
    private var selectedRecipeType: RecipeType? = null

    companion object {
        private const val EXTRA_RECIPE_TYPE_ID = "recipe_type_id"
        
        fun newIntent(context: Context, recipeTypeId: Int): Intent {
            return Intent(context, RecipeListActivity::class.java).apply {
                putExtra(EXTRA_RECIPE_TYPE_ID, recipeTypeId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_list)

        this.setupViewModel()
        this.findAllViews()
        this.setupRecyclerView()
        this.setupClickListeners()
        this.loadRecipeType()
        this.loadRecipes()
    }

    private fun setupViewModel() {
        val database = AppDatabase.getDatabase(this)
        val repository = RecipeRepository(database.recipeDao())
        val factory = RecipeListViewModelFactory(repository, this)
        viewModel = ViewModelProvider(this, factory)[RecipeListViewModel::class.java]
    }

    private fun findAllViews() {
        this.backImageView = findViewById(R.id.recipe_list_back_iv)
        this.titleTextView = findViewById(R.id.recipe_list_title_tv)
        this.recyclerView = findViewById(R.id.recipe_list_recycler_view)
        this.noRecipesTextView = findViewById(R.id.recipe_list_no_recipes_tv)
    }

    private fun setupRecyclerView() {
        // Show 2 items per row
        this.recyclerView.layoutManager = GridLayoutManager(this, 2)
        
        // Initialize adapter with empty list while waiting for data to load
        this.recipeAdapter = RecipeAdapter(emptyList()) { recipe ->
            onRecipeClick(recipe)
        }
        
        this.recyclerView.adapter = this.recipeAdapter
    }

    private fun setupClickListeners() {
        this.backImageView.setOnClickListener {
            finish()
        }
    }

    private fun loadRecipeType() {
        val recipeTypeId = intent.getIntExtra(EXTRA_RECIPE_TYPE_ID, -1)
        if (recipeTypeId == -1) {
            Toast.makeText(this, getString(R.string.error_invalid_recipe_type), Toast.LENGTH_LONG).show()
            finish()
            return
        }

        try {
            val inputStream: InputStream = assets.open(Constants.RECIPE_FILE_NAME)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            val json = String(buffer, Charsets.UTF_8)
            val gson = Gson()
            val type = object : TypeToken<List<RecipeType>>() {}.type
            val recipeTypes: List<RecipeType> = gson.fromJson(json, type)
            
            this.selectedRecipeType = recipeTypes.find { it.id == recipeTypeId }
            if (this.selectedRecipeType != null) {
                this.titleTextView.text = this.selectedRecipeType!!.name.replaceFirstChar { it.uppercase() }
            } else {
                Toast.makeText(this, getString(R.string.error_recipe_type_not_found), Toast.LENGTH_LONG).show()
                finish()
                return
            }
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.error_loading_recipe_types), Toast.LENGTH_LONG).show()
            finish()
            return
        }
    }

    private fun loadRecipes() {
        this.selectedRecipeType?.let { recipeType ->
            viewModel.loadRecipesByType(recipeType.id)
            observeRecipes()
        }
    }

    private fun observeRecipes() {
        viewModel.recipes.observe(this) { recipes ->
            if (recipes.isEmpty()) {
                this.recyclerView.visibility = View.GONE
                this.noRecipesTextView.visibility = View.VISIBLE
            } else {
                this.recyclerView.visibility = View.VISIBLE
                this.noRecipesTextView.visibility = View.GONE
                
                this.recipeAdapter = RecipeAdapter(recipes) { recipe ->
                    onRecipeClick(recipe)
                }
                this.recyclerView.adapter = this.recipeAdapter
            }
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun onRecipeClick(recipe: com.lund.tastynotes.models.Recipe) {
        val intent = RecipeDetailActivity.newIntent(this, recipe.id)
        startActivity(intent)
    }
} 