package dev.chara.news.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
data class Page(
    val status: String,
    val totalResults: Int,
    val articles: Array<Article>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Page

        if (status != other.status) return false
        if (totalResults != other.totalResults) return false
        return articles.contentEquals(other.articles)
    }

    override fun hashCode(): Int {
        var result = status.hashCode()
        result = 31 * result + totalResults
        result = 31 * result + articles.contentHashCode()
        return result
    }
}

@Serializable
@Parcelize
data class Article(
    val source: Source,
    val author: String?,
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    val content: String?
) : Parcelable

@Serializable
@Parcelize
data class Source(
    val id: String?,
    val name: String
) : Parcelable
