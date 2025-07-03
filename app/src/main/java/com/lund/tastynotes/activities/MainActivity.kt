package com.lund.tastynotes.activities

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.lund.tastynotes.R
import com.lund.tastynotes.adapters.RecipeViewPagerAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var addImageView: ImageView
    private lateinit var searchImageView: ImageView
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    private lateinit var viewPagerAdapter: RecipeViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.findAllViews()
        this.setupViews()
    }

    private fun setupViews() {
        this.viewPagerAdapter = RecipeViewPagerAdapter(this@MainActivity)
        this.viewPager.adapter = viewPagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.all_recipes)
                1 -> getString(R.string.types)
                else -> getString(R.string.all_recipes)
            }
        }.attach()
    }

    private fun findAllViews() {
        this.addImageView = findViewById(R.id.activity_main_add_iv)
        this.searchImageView = findViewById(R.id.activity_main_search_iv)
        this.tabLayout = findViewById(R.id.activity_main_tl)
        this.viewPager = findViewById(R.id.activity_main_vp2)
    }
}