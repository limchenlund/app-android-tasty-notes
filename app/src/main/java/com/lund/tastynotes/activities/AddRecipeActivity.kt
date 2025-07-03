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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lund.tastynotes.R
import com.lund.tastynotes.models.RecipeType
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recipe)

        this.findAllViews()
        this.setupImagePickerLauncher()
        this.loadRecipeTypes()
        this.setupClickListeners()
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
                Glide.with(this@AddRecipeActivity).load(uri).into(this.recipeImageView)
            }
        }
    }

    private fun loadRecipeTypes() {
        try {
            val inputStream: InputStream = assets.open("recipetypes.json")
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
            // TODO save to DB here later
        }

        this.backImageView.setOnClickListener {
            finish()
        }
    }
} 