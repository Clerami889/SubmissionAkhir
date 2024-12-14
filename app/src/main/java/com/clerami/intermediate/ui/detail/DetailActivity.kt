package com.clerami.intermediate.ui.detail

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.clerami.intermediate.databinding.ActivityDetailBinding
import androidx.lifecycle.Observer

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val detailViewModel: DetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityDetailBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }

        val storyId = intent.getStringExtra("storyId") ?: return


        detailViewModel.storyDetailLiveData.observe(this, Observer { detailStory ->
            detailStory?.story?.let { story ->
                binding.apply {
                    tvDetailName.text = story.name
                    tvDetailDescription.text = story.description
                    tvDetailCreated.text = story.createdAt
                    Glide.with(this@DetailActivity)
                        .load(story.photoUrl)
                        .into(ivDetailPhoto)
                }
            }
        })


        detailViewModel.errorLiveData.observe(this, Observer { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        })

        detailViewModel.fetchStoryDetail(storyId)
    }
}
