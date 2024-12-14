package com.clerami.intermediate

import com.clerami.intermediate.data.remote.response.ListStoryItem

object DataDummy {

    fun generateDummyStoryList(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val quote = ListStoryItem(
                i.toString(),
                "created at + $i",
                "name $i",
                "Desc $i"
            )
            items.add(quote)
        }
        return items
    }
}