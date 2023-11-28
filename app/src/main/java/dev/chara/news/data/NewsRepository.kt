package dev.chara.news.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.chara.news.model.Article
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NewsRepository @Inject constructor(
    private val newsDataSource: NewsDataSource,
    private val pagingConfig: PagingConfig
) {
    fun getArticles(): Flow<PagingData<Article>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = {
                NewsPagingSource(newsDataSource, pagingConfig)
            }
        ).flow
    }
}