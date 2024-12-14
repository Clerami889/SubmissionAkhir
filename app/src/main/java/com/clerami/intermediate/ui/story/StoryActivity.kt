package com.clerami.intermediate.ui.story

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clerami.intermediate.R
import com.clerami.intermediate.databinding.ActivityStoryBinding
import com.clerami.intermediate.data.remote.retrofit.ApiConfig
import com.clerami.intermediate.ui.detail.DetailActivity
import com.clerami.intermediate.utils.SessionManager

class StoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryBinding
    private lateinit var storyViewModel: StoryViewModel
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val apiService = ApiConfig.getApiService()
        val repository = StoryRepository(apiService)

        val factory = StoryViewModelFactory(repository, this, apiService)
        storyViewModel = ViewModelProvider(this, factory).get(StoryViewModel::class.java)



        storyAdapter = StoryAdapter { storyId ->

            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("story_id", storyId)
            startActivity(intent)
        }


        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = storyAdapter


        storyViewModel.stories.observe(this, Observer { pagingData ->

            storyAdapter.submitData(lifecycle, pagingData)
        })


        storyViewModel.isLoading.observe(this, { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })


        val token = SessionManager.getToken(this)
        if (token != null) {
            storyViewModel.getStories(token)
        }
    }
}
