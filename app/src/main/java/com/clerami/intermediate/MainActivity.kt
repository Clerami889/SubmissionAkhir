package com.clerami.intermediate

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.clerami.intermediate.databinding.ActivityMainBinding
import com.clerami.intermediate.ui.addstory.AddStoryActivity
import com.clerami.intermediate.ui.login.LoginActivity
import com.clerami.intermediate.ui.map.MapsActivity
import com.clerami.intermediate.ui.story.StoryActivity

import com.clerami.intermediate.utils.SessionManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setSupportActionBar(binding.toolbar)

        if (!SessionManager.isLoggedIn(this)) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.keluar.setOnClickListener {
            signOut()
        }

        binding.keStory.setOnClickListener{
            val intent = Intent(this,StoryActivity::class.java)
            startActivity(intent)
        }


        binding.AddStory.setOnClickListener{
            val intent = Intent(this,AddStoryActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()

        animateViews()
    }


    private fun animateViews() {

        binding.logo.translationY = -500f
        binding.keStory.translationX = 500f
        binding.keluar.translationX = -500f
        binding.AddStory.translationX = 500f

        val logoAnimator = ObjectAnimator.ofFloat(binding.logo, "translationY", -500f, 0f)
        logoAnimator.duration = 1000
        logoAnimator.interpolator = AccelerateDecelerateInterpolator()

        val keStoryAnimator = ObjectAnimator.ofFloat(binding.keStory, "translationX", 500f, 0f)
        keStoryAnimator.duration = 1000
        keStoryAnimator.interpolator = AccelerateDecelerateInterpolator()

        val keluarAnimator = ObjectAnimator.ofFloat(binding.keluar, "translationX", -500f, 0f)
        keluarAnimator.duration = 1000
        keluarAnimator.interpolator = AccelerateDecelerateInterpolator()

        val addStoryAnimator = ObjectAnimator.ofFloat(binding.AddStory, "translationX", 500f, 0f)
        addStoryAnimator.duration = 1000
        addStoryAnimator.interpolator = AccelerateDecelerateInterpolator()

        val animatorSet = android.animation.AnimatorSet()
        animatorSet.playTogether(logoAnimator, keStoryAnimator, keluarAnimator, addStoryAnimator)
        animatorSet.start()
    }

    private fun signOut() {
        SessionManager.clearSession(this)
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }


    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            R.id.action_map ->{
                intent = Intent(this,MapsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
