package dev.chara.news.ui.component

import android.os.Parcelable
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.value.Value
import dev.chara.news.domain.GetArticlesUseCase
import dev.chara.news.model.Article
import dev.chara.news.ui.ApplicationComponentContext
import dev.chara.news.ui.component.util.coroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

interface ArticleListComponent {
    val articles: StateFlow<PagingData<Article>>

    val articleSlot: Value<ChildSlot<*, ArticleComponent>>

    fun onArticleClicked(article: Article)
}

class DefaultArticleListComponent @Inject constructor(
    @ApplicationComponentContext componentContext: ComponentContext,
    private val getArticlesUseCase: GetArticlesUseCase
) : ArticleListComponent, ComponentContext by componentContext {

    private val coroutineScope = coroutineScope(Dispatchers.Default + SupervisorJob())

    private val _articles: MutableStateFlow<PagingData<Article>> =
        MutableStateFlow(value = PagingData.empty())
    override val articles = _articles.asStateFlow()

    init {
        coroutineScope.launch {
            getArticlesUseCase()
                .distinctUntilChanged()
                .cachedIn(coroutineScope)
                .collect {
                    _articles.value = it
                }
        }
    }

    private val articleNavigation = SlotNavigation<ArticleConfig>()

    override fun onArticleClicked(article: Article) {
        articleNavigation.activate(ArticleConfig(article))
    }

    override val articleSlot: Value<ChildSlot<*, ArticleComponent>> =
        childSlot(
            source = articleNavigation,
            handleBackButton = true
        ) { config, childComponentContext ->
            DefaultArticleComponent(
                componentContext = childComponentContext,
                displayedArticle = config.article,
                onBack = articleNavigation::dismiss,
            )
        }

    @Parcelize
    private data class ArticleConfig(
        val article: Article,
    ) : Parcelable
}