package com.lund.tastynotes.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.lund.tastynotes.R
import com.lund.tastynotes.views.ZoomableImageView

class ViewImageActivity : AppCompatActivity() {
    private lateinit var fullScreenImageView: ZoomableImageView
    private lateinit var backImageView: ImageView

    companion object {
        private const val EXTRA_IMAGE_URI = "image_uri"
        
        fun newIntent(context: Context, imageUri: String): Intent {
            return Intent(context, ViewImageActivity::class.java).apply {
                putExtra(EXTRA_IMAGE_URI, imageUri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_image)

        this.findAllViews()
        this.setupClickListeners()
        this.loadImage()
    }

    private fun findAllViews() {
        this.fullScreenImageView = findViewById(R.id.view_image_full_screen_iv)
        this.backImageView = findViewById(R.id.view_image_back_iv)
    }

    private fun setupClickListeners() {
        this.backImageView.setOnClickListener {
            finish()
        }

        // Allow tapping anywhere on the image to go back (only when not zoomed)
        this.fullScreenImageView.setOnClickListener {
            finish()
        }
    }

    private fun loadImage() {
        val imageUri = intent.getStringExtra(EXTRA_IMAGE_URI)
        if (!imageUri.isNullOrBlank()) {
            Glide.with(this@ViewImageActivity)
                .load(imageUri)
                .placeholder(R.drawable.ic_food_placeholder)
                .error(R.drawable.ic_food_placeholder)
                .fitCenter()
                .into(this.fullScreenImageView)
        } else {
            // If no image URI, show placeholder
            this.fullScreenImageView.setImageResource(R.drawable.ic_food_placeholder)
        }
    }
} 