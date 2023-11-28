package dev.chara.news.data

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.michaelbull.result.mapBoth
import dev.chara.news.model.Article

class NewsPagingSource constructor(
    private val newsDataSource: NewsDataSource,
    private val pagingConfig: PagingConfig
) : PagingSource<Int, Article>() {

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val pageIndex = params.key ?: 1

        return newsDataSource.getPage(pageIndex)
            .mapBoth(
                success = {
                    LoadResult.Page(
                        it.articles.toList(),
                        prevKey = if (pageIndex == 1) null else pageIndex - 1,
                        nextKey = if (it.totalResults < pageIndex * pagingConfig.pageSize) null else pageIndex + 1
                    )
                },
                failure = {
                    LoadResult.Error(Exception(it.message))
                }
            )
    }
}