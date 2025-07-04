package com.lund.tastynotes.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import com.lund.tastynotes.viewmodels.EditRecipeViewModel
import com.lund.tastynotes.viewmodels.EditRecipeViewModelFactory
import java.io.InputStream

class EditRecipeActivity : AppCompatActivity() {
    private lateinit var recipeNameEditText: EditText
    private lateinit var ingredientsEditText: EditText
    private lateinit var stepsEditText: EditText
    private lateinit var recipeImageView: ImageView
    private lateinit var pickImageButton: Button
    private lateinit var recipeTypeSpinner: Spinner
    private lateinit var saveButton: Button
    private lateinit var backImageView: ImageView

    private var imageUri: Uri? = null
    private lateinit var recipeTypes: List<RecipeType>
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var viewModel: EditRecipeViewModel
    private var originalRecipe: Recipe? = null

    companion object {
        private const val EXTRA_RECIPE_ID = "recipe_id"
        
        fun newIntent(context: Context, recipeId: Long): Intent {
            return Intent(context, EditRecipeActivity::class.java).apply {
                putExtra(EXTRA_RECIPE_ID, recipeId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_recipe)

        this.setupViewModel()
        this.findAllViews()
        this.setupImagePickerLauncher()
        this.loadRecipeTypes()
        this.setupClickListeners()
        this.loadRecipeData()
    }

    private fun setupViewModel() {
        val database = AppDatabase.getDatabase(this)
        val repository = RecipeRepository(database.recipeDao())
        val factory = EditRecipeViewModelFactory(repository, this)
        viewModel = ViewModelProvider(this, factory)[EditRecipeViewModel::class.java]
    }

    private fun findAllViews() {
        this.recipeNameEditText = findViewById(R.id.edit_recipe_name_et)
        this.recipeImageView = findViewById(R.id.edit_recipe_image_iv)
        this.pickImageButton = findViewById(R.id.edit_recipe_pick_image_btn)
        this.recipeTypeSpinner = findViewById(R.id.edit_recipe_type_spinner)
        this.saveButton = findViewById(R.id.edit_recipe_save_btn)
        this.ingredientsEditText = findViewById(R.id.edit_recipe_ingredients_et)
        this.stepsEditText = findViewById(R.id.edit_recipe_steps_et)
        this.backImageView = findViewById(R.id.edit_recipe_back_iv)
    }

    private fun setupImagePickerLauncher() {
        this.imagePickerLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                // Take persistent permission so that it can still load the image after app restart
                val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                contentResolver.takePersistableUriPermission(uri, takeFlags)

                imageUri = uri
                Glide.with(this@EditRecipeActivity)
                    .load(uri)
                    .placeholder(R.drawable.ic_food_placeholder)
                    .error(R.drawable.ic_food_placeholder)
                    .into(this.recipeImageView)
            }
        }
    }

    private fun loadRecipeTypes() {
        try {
            val inputStream: InputStream = assets.open(Constants.RECIPE_TYPES_FILE_NAME)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            val json = String(buffer, Charsets.UTF_8)
            val gson = Gson()
            val type = object : TypeToken<List<RecipeType>>() {}.type
            recipeTypes = gson.fromJson(json, type)
            val typeNames = recipeTypes.map { it.name.replaceFirstChar { c -> c.uppercase() } }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, typeNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            this.recipeTypeSpinner.adapter = adapter
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.error_loading_recipe_types), Toast.LENGTH_LONG).show()
        }
    }

    private fun setupClickListeners() {
        this.pickImageButton.setOnClickListener {
            this.imagePickerLauncher.launch(arrayOf("image/*"))
        }

        this.saveButton.setOnClickListener {
            updateRecipe()
        }

        this.backImageView.setOnClickListener {
            finish()
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
            recipe?.let { 
                originalRecipe = it
                populateRecipeData(it)
            }
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                finish()
            }
        }

        viewModel.updateSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, getString(R.string.recipe_updated), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun populateRecipeData(recipe: Recipe) {
        // Populate recipe name
        this.recipeNameEditText.setText(recipe.name)

        // Load existing image if available
        if (!recipe.imageUri.isNullOrBlank()) {
            imageUri = Uri.parse(recipe.imageUri)
            Glide.with(this@EditRecipeActivity)
                .load(recipe.imageUri)
                .placeholder(R.drawable.ic_food_placeholder)
                .error(R.drawable.ic_food_placeholder)
                .into(this.recipeImageView)
        }

        // Set recipe type
        val typeIndex = recipeTypes.indexOfFirst { it.id == recipe.recipeTypeId }
        if (typeIndex != -1) {
            this.recipeTypeSpinner.setSelection(typeIndex)
        }

        // Populate ingredients
        this.ingredientsEditText.setText(recipe.ingredients)

        // Populate steps
        this.stepsEditText.setText(recipe.steps)
    }

    private fun updateRecipe() {
        val name = recipeNameEditText.text.toString()
        val ingredients = ingredientsEditText.text.toString()
        val steps = stepsEditText.text.toString()
        val selectedPosition = recipeTypeSpinner.selectedItemPosition
        
        if (selectedPosition == -1) {
            Toast.makeText(this, getString(R.string.validation_recipe_type_required), Toast.LENGTH_SHORT).show()
            return
        }
        
        val selectedRecipeType = recipeTypes[selectedPosition]
        val imageUriString = imageUri?.toString() ?: originalRecipe?.imageUri

        originalRecipe?.let { recipe ->
            viewModel.updateRecipe(
                recipeId = recipe.id,
                name = name,
                imageUri = imageUriString,
                ingredients = ingredients,
                steps = steps,
                recipeTypeId = selectedRecipeType.id
            )
        }
    }
} 