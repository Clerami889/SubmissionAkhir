package com.clerami.intermediate.ui.detail

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.clerami.intermediate.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val detailViewModel: DetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("DetailActivity", "DetailActivity initialized")

        val storyId = intent.getStringExtra("story_id")
        if (storyId == null) {
            Log.e("DetailActivity", "No storyId passed to DetailActivity")
            finish()
        } else {
            Log.d("DetailActivity", "Received storyId: $storyId")
            detailViewModel.fetchStoryDetail(storyId)
        }

        detailViewModel.storyDetailLiveData.observe(this, Observer { detailStory ->
            if (detailStory != null) {
                Log.d("DetailActivity", "Story details received: $detailStory")
                detailStory.story.let { story ->
                    binding.apply {
                        if (story != null) {
                            tvDetailName.text = story.name
                        }
                        if (story != null) {
                            tvDetailDescription.text = story.description
                        }
                        if (story != null) {
                            tvDetailCreated.text = story.createdAt
                        }
                        if (story != null) {
                            Glide.with(this@DetailActivity)
                                .load(story.photoUrl)
                                .into(ivDetailPhoto)
                        }

                        if (story != null) {
                            lat.text = story.lat.toString()
                        }


                        if (story != null) {
                            lon.text = story.lon.toString()
                        }
                    }
                }
            } else {
                Log.e("DetailActivity", "Story details are null")
            }
        })

        detailViewModel.errorLiveData.observe(this, Observer { errorMessage ->
            Log.e("DetailActivity", "Error received: $errorMessage")
        })
    }


    override fun onStart() {
        super.onStart()
        Log.d("DetailActivity", "onStart called")
    }

    override fun onResume() {
        super.onResume()
        Log.d("DetailActivity", "onResume called")
    }

}


