package com.clerami.intermediate.ui.story

import androidx.paging.PagingSource
import androidx.paging.PagingState

import com.clerami.intermediate.data.remote.response.ListStoryItem
import com.clerami.intermediate.data.remote.retrofit.ApiService
import retrofit2.HttpException

class StoryPagingSource(
    private val apiService: ApiService,
    private val token: String
) : PagingSource<Int, ListStoryItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        val page = params.key ?: 1

        return try {

            val response = apiService.getAllStories(token, page, params.loadSize)


            val stories = response.listStory ?: emptyList()


            val nextPage = if (stories.isEmpty()) null else page + 1


            LoadResult.Page(
                data = stories,
                prevKey = if (page == 1) null else page - 1,
                nextKey = nextPage
            )
        } catch (exception: Exception) {

            if (exception is HttpException) {

                LoadResult.Error(exception)
            } else {
                LoadResult.Error(Exception("Unknown error occurred"))
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {

        return state.anchorPosition?.let { state.closestPageToPosition(it)?.prevKey }
    }
}
