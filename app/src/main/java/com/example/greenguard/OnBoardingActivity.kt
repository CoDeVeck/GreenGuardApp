package com.example.greenguard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.greenguard.databinding.ActivityOnBoardingBinding

class OnBoardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnBoardingBinding

    private val layouts = listOf(
        R.layout.on_boarding_step1,
        R.layout.on_boarding_step2,
        R.layout.on_boarding_step3
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isOnboardingCompleted()) {
            navigateToMain()
            return
        }

        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        setupDots()
        setupListeners()
        updateUI(0)
    }


    private fun setupViewPager() {
        val adapter = OnboardingAdapter(layouts)
        binding.viewPager.adapter = adapter

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateUI(position)
                updateDots(position)
            }
        })
    }

    private fun setupDots() {
        val dots = arrayOfNulls<ImageView>(layouts.size)

        binding.dotsIndicator.removeAllViews()

        for (i in dots.indices) {
            dots[i] = ImageView(this)
            dots[i]?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dot_inactive))

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 0, 8, 0)

            binding.dotsIndicator.addView(dots[i], params)
        }

        updateDots(0)
    }

    private fun updateDots(position: Int) {
        for (i in 0 until binding.dotsIndicator.childCount) {
            val dot = binding.dotsIndicator.getChildAt(i) as ImageView
            if (i == position) {
                dot.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dot_active))
            } else {
                dot.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dot_inactive))
            }
        }
    }

    private fun updateUI(position: Int) {
        binding.tvStepIndicator.text = "Paso ${position + 1}"

        when (position) {
            layouts.size - 1 -> {
                // Última página
                binding.btnNext.text = "Comenzar"
                binding.tvSkip.visibility = View.GONE
            }
            else -> {
                // Otras páginas
                binding.btnNext.text = "Siguiente"
                binding.tvSkip.visibility = View.VISIBLE
            }
        }
    }

    private fun setupListeners() {
        binding.btnNext.setOnClickListener {
            val currentPosition = binding.viewPager.currentItem

            if (currentPosition < layouts.size - 1) {
                binding.viewPager.currentItem = currentPosition + 1
            } else {
                finishOnboarding()
            }
        }

        binding.tvSkip.setOnClickListener {
            finishOnboarding()
        }
    }

    private fun finishOnboarding() {
        val prefs = getSharedPreferences("GreenGuardian", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("onboarding_completed", true).apply()

        navigateToMain()
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun isOnboardingCompleted(): Boolean {
        /*val prefs = getSharedPreferences("GreenGuardian", Context.MODE_PRIVATE)
        return prefs.getBoolean("onboarding_completed", false)*/
        return false
    }

    class OnboardingAdapter(private val layouts: List<Int>) :
        RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

        class OnboardingViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(layouts[viewType], parent, false)
            return OnboardingViewHolder(view)
        }

        override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        }

        override fun getItemCount(): Int = layouts.size

        override fun getItemViewType(position: Int): Int = position
    }
}