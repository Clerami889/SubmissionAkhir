package com.clerami.intermediate.ui.story

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.clerami.intermediate.DataDummy
import com.clerami.intermediate.MainDispatcherRule
import com.clerami.intermediate.data.remote.response.ListStoryItem
import com.clerami.intermediate.data.remote.retrofit.ApiConfig
import com.clerami.intermediate.data.remote.retrofit.ApiService
import com.clerami.intermediate.getOrAwaitValue
import com.clerami.intermediate.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var apiService: ApiService
    private lateinit var sessionManager: SessionManager

    private lateinit var storyViewModel: StoryViewModel


    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {

        val dummyStories = DataDummy.generateDummyStoryList()
        val pagingData = PagingData.from(dummyStories)

        Mockito.`when`(storyRepository.getStories("Bearer some_token")).thenReturn(flowOf(pagingData))


        Mockito.`when`(sessionManager.getToken(any())).thenReturn("valid_token")


        storyViewModel = StoryViewModel(storyRepository, mock(Context::class.java), mock(ApiService::class.java))


        val actualStories: PagingData<ListStoryItem> = storyViewModel.stories.getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStories)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStories.size, differ.snapshot().size)
        Assert.assertEquals(dummyStories[0], differ.snapshot()[0])
    }



    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {

        val emptyList: PagingData<ListStoryItem> = PagingData.from(emptyList())


        Mockito.`when`(storyRepository.getStories("Bearer valid_token")).thenReturn(flowOf(emptyList))


        Mockito.`when`(sessionManager.getToken(any())).thenReturn("valid_token")


        val storyViewModel = StoryViewModel(storyRepository, mock(Context::class.java), mock(ApiService::class.java))


        val actualStory: PagingData<ListStoryItem> = storyViewModel.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        differ.submitData(actualStory)


        Assert.assertEquals(0, differ.snapshot().size)
    }



}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}
