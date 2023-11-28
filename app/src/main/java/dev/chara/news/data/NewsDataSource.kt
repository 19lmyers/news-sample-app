package dev.chara.news.data

import androidx.paging.PagingConfig
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dev.chara.news.BuildConfig
import dev.chara.news.model.Error
import dev.chara.news.model.Page
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import javax.inject.Inject

class NewsDataSource @Inject constructor(
    private val client: HttpClient,
    private val pagingConfig: PagingConfig
) {
    suspend fun getPage(pageIndex: Int): Result<Page, Error> {
        //Logger.getLogger("NewsDataSource").warning("Fetching page $pageIndex")

        try {
            val response = client.get("https://newsapi.org/v2/top-headlines") {
                header("X-Api-Key", BuildConfig.API_KEY)
                parameter("country", "us")
                parameter("pageSize", pagingConfig.pageSize)
                parameter("page", pageIndex)
            }

            return when (response.status) {
                HttpStatusCode.OK -> {
                    val page: Page = response.body()
                    //Logger.getLogger("NewsDataSource").warning(page.toString())
                    Ok(page)
                }

                HttpStatusCode.TooManyRequests -> {
                    val error: Error = response.body()
                    //Logger.getLogger("NewsDataSource").severe(error.toString())
                    Err(error)
                }

                else -> {
                    val error: Error = response.body()
                    //Logger.getLogger("NewsDataSource").severe(error.toString())
                    Err(error)
                }
            }
        } catch (ex: Exception) {
            //Logger.getLogger("NewsDataSource").severe(ex.message ?: "An unknown error occurred")
            return Err(
                Error(
                    "error",
                    "communicationError",
                    ex.message
                        ?: "An unknown error occurred while communicating with the server. Please try again in a little while."
                )
            )
        }
    }
}