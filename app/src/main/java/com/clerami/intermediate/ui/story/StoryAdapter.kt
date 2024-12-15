package com.clerami.intermediate.ui.story

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.clerami.intermediate.data.remote.response.ListStoryItem
import com.clerami.intermediate.databinding.ItemStoryBinding


class StoryAdapter(
    private val onItemClick: (String) -> Unit
) : PagingDataAdapter<ListStoryItem, StoryAdapter.StoryViewHolder>(StoryDiffCallback()) {

    inner class StoryViewHolder(val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val story = getItem(position)
                    if (story != null && story.id != null) {
                        Log.d("StoryAdapter", "Clicked storyId: ${story.id}")
                        onItemClick(story.id)
                    } else {
                        Log.e("StoryAdapter", "Story or storyId is null at position $position")
                    }
                } else {
                    Log.e("StoryAdapter", "Invalid adapter position: $position")
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        if (story == null) {
            Log.w("StoryAdapter", "Story at position $position is null (loading state)")
            return
        }

        holder.binding.apply {
            tvItemName.text = story.name ?: "No Name"
            tvItemDescription.text = story.description ?: "No Description"
            Glide.with(ivItemPhoto.context)
                .load(story.photoUrl)
                .into(ivItemPhoto)
        }
    }

    class StoryDiffCallback : DiffUtil.ItemCallback<ListStoryItem>() {
        override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
            return oldItem == newItem
        }
    }


    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}

