package dev.chara.news.domain

import androidx.paging.PagingData
import dev.chara.news.data.NewsRepository
import dev.chara.news.model.Article
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetArticlesUseCase @Inject constructor(private val newsRepository: NewsRepository) {
    suspend operator fun invoke(): Flow<PagingData<Article>> =
        withContext(Dispatchers.Default) {
            return@withContext newsRepository.getArticles()
        }
}