package com.lund.tastynotes.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lund.tastynotes.fragments.AllRecipesFragment
import com.lund.tastynotes.fragments.AllTypesFragment

class RecipeViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AllRecipesFragment()
            1 -> AllTypesFragment()
            else -> AllRecipesFragment()
        }
    }
}