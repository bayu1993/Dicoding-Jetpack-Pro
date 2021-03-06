package com.bayuspace.academy.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bayuspace.academy.R
import com.bayuspace.academy.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        val pagerAdapter = SectionPagerAdapter(this, supportFragmentManager)
        _binding.viewPager.adapter = pagerAdapter
        _binding.homeTab.setupWithViewPager(_binding.viewPager)

        supportActionBar?.elevation = 0F
    }
}