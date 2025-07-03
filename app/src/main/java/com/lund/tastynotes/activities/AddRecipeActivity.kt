package com.lund.tastynotes.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lund.tastynotes.R
import com.lund.tastynotes.database.AppDatabase
import com.lund.tastynotes.models.RecipeType
import com.lund.tastynotes.repository.RecipeRepository
import com.lund.tastynotes.utils.Constants
import com.lund.tastynotes.viewmodels.AddRecipeViewModel
import com.lund.tastynotes.viewmodels.AddRecipeViewModelFactory
import java.io.InputStream

class AddRecipeActivity : AppCompatActivity() {
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
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
    private lateinit var viewModel: AddRecipeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recipe)

        this.setupViewModel()
        this.findAllViews()
        this.setupImagePickerLauncher()
        this.loadRecipeTypes()
        this.setupClickListeners()
    }

    private fun setupViewModel() {
        val database = AppDatabase.getDatabase(this)
        val repository = RecipeRepository(database.recipeDao())
        val factory = AddRecipeViewModelFactory(repository, this)
        viewModel = ViewModelProvider(this, factory)[AddRecipeViewModel::class.java]
    }

    private fun findAllViews() {
        this.recipeNameEditText = findViewById(R.id.activity_add_recipe_recipe_name_et)
        this.recipeImageView = findViewById(R.id.activity_add_recipe_recipe_image_iv)
        this.pickImageButton = findViewById(R.id.activity_add_recipe_pick_image_btn)
        this.recipeTypeSpinner = findViewById(R.id.activity_add_recipe_type_spinner)
        this.saveButton = findViewById(R.id.activity_add_recipe_save_btn)
        this.ingredientsEditText = findViewById(R.id.activity_add_recipe_ingredients_et)
        this.stepsEditText = findViewById(R.id.activity_add_recipe_steps_et)
        this.backImageView = findViewById(R.id.activity_add_recipe_back_iv)
    }

    private fun setupImagePickerLauncher() {
        this.imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                imageUri = uri
                Glide.with(this@AddRecipeActivity)
                    .load(uri)
                    .placeholder(R.drawable.ic_food_placeholder)
                    .error(R.drawable.ic_food_placeholder)
                    .into(this.recipeImageView)
            }
        }
    }

    private fun loadRecipeTypes() {
        try {
            val inputStream: InputStream = assets.open(Constants.RECIPE_FILE_NAME)
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
            this.imagePickerLauncher.launch("image/*")
        }

        this.saveButton.setOnClickListener {
            saveRecipe()
        }

        this.backImageView.setOnClickListener {
            finish()
        }
    }

    // Save recipe into local DB
    private fun saveRecipe() {
        val name = recipeNameEditText.text.toString()
        val ingredients = ingredientsEditText.text.toString()
        val steps = stepsEditText.text.toString()
        val selectedPosition = recipeTypeSpinner.selectedItemPosition
        
        if (selectedPosition == -1) {
            Toast.makeText(this, getString(R.string.validation_recipe_type_required), Toast.LENGTH_SHORT).show()
            return
        }
        
        val selectedRecipeType = recipeTypes[selectedPosition]
        val imageUriString = imageUri?.toString()

        viewModel.saveRecipe(
            name = name,
            imageUri = imageUriString,
            ingredients = ingredients,
            steps = steps,
            recipeTypeId = selectedRecipeType.id,
            onSuccess = { recipeId ->
                Toast.makeText(this, getString(R.string.success_recipe_saved), Toast.LENGTH_SHORT).show()
                finish()
            },
            onError = { errorMessage ->
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }
        )
    }
} 