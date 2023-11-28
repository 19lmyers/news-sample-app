package dev.chara.news.ui.component

import com.arkivanov.decompose.ComponentContext
import dev.chara.news.model.Article
import dev.chara.news.ui.ApplicationComponentContext

interface ArticleComponent {
    val article: Article

    fun onDismiss()
}

class DefaultArticleComponent(
    @ApplicationComponentContext componentContext: ComponentContext,
    displayedArticle: Article,
    private val onBack: () -> Unit
) : ArticleComponent, ComponentContext by componentContext {

    override val article: Article = displayedArticle

    override fun onDismiss() = onBack()
}