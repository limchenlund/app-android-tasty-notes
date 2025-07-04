package com.lund.tastynotes.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lund.tastynotes.R
import com.lund.tastynotes.database.AppDatabase
import com.lund.tastynotes.models.Recipe
import com.lund.tastynotes.models.RecipeType
import com.lund.tastynotes.repository.RecipeRepository
import com.lund.tastynotes.utils.Constants
import com.lund.tastynotes.viewmodels.RecipeDetailViewModel
import com.lund.tastynotes.viewmodels.RecipeDetailViewModelFactory
import java.io.InputStream

class RecipeDetailActivity : AppCompatActivity() {
    private lateinit var recipeImageView: ImageView
    private lateinit var recipeNameTextView: TextView
    private lateinit var recipeTypeTextView: TextView
    private lateinit var ingredientsTextView: TextView
    private lateinit var stepsTextView: TextView
    private lateinit var backImageView: ImageView
    private lateinit var deleteImageView: ImageView
    private lateinit var editImageView: ImageView
    private lateinit var viewModel: RecipeDetailViewModel
    private var currentRecipe: Recipe? = null

    companion object {
        private const val EXTRA_RECIPE_ID = "recipe_id"
        
        fun newIntent(context: Context, recipeId: Long): Intent {
            return Intent(context, RecipeDetailActivity::class.java).apply {
                putExtra(EXTRA_RECIPE_ID, recipeId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

        this.setupViewModel()
        this.findAllViews()
        this.setupClickListeners()
        this.loadRecipeData()
    }

    override fun onResume() {
        super.onResume()
        // Reload recipe data when returning from edit activity
        val recipeId = intent.getLongExtra(EXTRA_RECIPE_ID, -1)
        if (recipeId != -1L) {
            viewModel.loadRecipe(recipeId)
        }
    }

    private fun setupViewModel() {
        val database = AppDatabase.getDatabase(this)
        val repository = RecipeRepository(database.recipeDao())
        val factory = RecipeDetailViewModelFactory(repository, this)
        viewModel = ViewModelProvider(this, factory)[RecipeDetailViewModel::class.java]
    }

    private fun findAllViews() {
        this.recipeImageView = findViewById(R.id.recipe_detail_image_iv)
        this.recipeNameTextView = findViewById(R.id.recipe_detail_name_tv)
        this.recipeTypeTextView = findViewById(R.id.recipe_detail_type_tv)
        this.ingredientsTextView = findViewById(R.id.recipe_detail_ingredients_tv)
        this.stepsTextView = findViewById(R.id.recipe_detail_steps_tv)
        this.backImageView = findViewById(R.id.recipe_detail_back_iv)
        this.deleteImageView = findViewById(R.id.recipe_detail_delete_iv)
        this.editImageView = findViewById(R.id.recipe_detail_edit_iv)
    }

    private fun setupClickListeners() {
        this.backImageView.setOnClickListener {
            finish()
        }

        this.deleteImageView.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        this.editImageView.setOnClickListener {
            currentRecipe?.let { recipe ->
                val intent = EditRecipeActivity.newIntent(this, recipe.id)
                startActivity(intent)
            }
        }
    }

    private fun loadRecipeData() {
        val recipeId = intent.getLongExtra(EXTRA_RECIPE_ID, -1)
        if (recipeId == -1L) {
            Toast.makeText(this, getString(R.string.error_invalid_recipe), Toast.LENGTH_LONG).show()
            finish()
            return
        }

        viewModel.loadRecipe(recipeId)
        observeRecipeData()
    }

    private fun observeRecipeData() {
        viewModel.recipe.observe(this) { recipe ->
            recipe?.let { displayRecipe(it) }
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                finish()
            }
        }

        viewModel.deleteSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, getString(R.string.recipe_deleted), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun displayRecipe(recipe: Recipe) {
        // Store the current recipe for deletion
        this.currentRecipe = recipe
        
        this.recipeNameTextView.text = recipe.name
        
        // Load image if available else hide it
        if (!recipe.imageUri.isNullOrBlank()) {
            this.recipeImageView.visibility = View.VISIBLE
            Glide.with(this@RecipeDetailActivity)
                .load(recipe.imageUri)
                .placeholder(R.drawable.ic_food_placeholder)
                .error(R.drawable.ic_food_placeholder)
                .centerCrop()
                .into(this.recipeImageView)
            
            // Add click listener to open full screen image
            this.recipeImageView.setOnClickListener {
                val intent = ViewImageActivity.newIntent(this, recipe.imageUri)
                startActivity(intent)
            }
        } else {
            this.recipeImageView.visibility = View.GONE
        }

        // Load recipe type name
        loadRecipeTypeName(recipe.recipeTypeId)

        // Display ingredients with bullet points
        val ingredients = recipe.ingredients.split("\n").filter { it.isNotBlank() }
        val ingredientsText = ingredients.joinToString("\n") { "• $it" }
        this.ingredientsTextView.text = ingredientsText

        // Display steps with numbers
        val steps = recipe.steps.split("\n").filter { it.isNotBlank() }
        val stepsText = steps.mapIndexed { index, step -> "${index + 1}. $step" }.joinToString("\n")
        this.stepsTextView.text = stepsText
    }

    private fun loadRecipeTypeName(recipeTypeId: Int) {
        try {
            val inputStream: InputStream = assets.open(Constants.RECIPE_TYPES_FILE_NAME)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            
            val json = String(buffer, Charsets.UTF_8)
            val gson = Gson()
            val type = object : TypeToken<List<RecipeType>>() {}.type
            val recipeTypes: List<RecipeType> = gson.fromJson(json, type)
            
            val recipeType = recipeTypes.find { it.id == recipeTypeId }
            this.recipeTypeTextView.text = recipeType?.name?.replaceFirstChar { it.uppercase() } ?: getString(R.string.unknown_type)
            
        } catch (e: Exception) {
            this.recipeTypeTextView.text = getString(R.string.unknown_type)
        }
    }

    private fun showDeleteConfirmationDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.delete_recipe))
            .setMessage(getString(R.string.confirm_delete_recipe))
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                deleteRecipe()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteRecipe() {
        currentRecipe?.let { recipe ->
            viewModel.deleteRecipe(recipe)
        }
    }
} 