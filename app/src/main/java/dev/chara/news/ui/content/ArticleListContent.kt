package dev.chara.news.ui.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import com.skydoves.landscapist.glide.GlideImage
import dev.chara.news.R
import dev.chara.news.model.Article
import dev.chara.news.model.Source
import dev.chara.news.ui.component.ArticleListComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.text.ParseException
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun ArticleListContent(component: ArticleListComponent) {
    val articles = component.articles
    val slot by component.articleSlot.subscribeAsState()

    val listState = rememberLazyListState()

    if (slot.child?.instance != null) {
        ArticleContent(slot.child?.instance!!)
    } else {
        ArticleList(listState, articles, component::onArticleClicked)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArticleList(
    listState: LazyListState,
    articles: Flow<PagingData<Article>>,
    onArticleClicked: (Article) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    val pagingItems = articles.collectAsLazyPagingItems()

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Box {}
            TopAppBar(
                title = {
                    Text(
                        "News Sample",
                        modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .clickable {
                        coroutineScope.launch {
                            listState.animateScrollToItem(0)
                        }
                    }
            )
        }
    ) { contentPadding ->
        LazyColumn(state = listState, contentPadding = contentPadding) {
            items(count = pagingItems.itemCount) { index ->
                val item = pagingItems[index]!!
                ArticleCard(article = item, onClick = onArticleClicked)
            }
            when {
                pagingItems.loadState.refresh is LoadState.Error -> {
                    val error = pagingItems.loadState.refresh as LoadState.Error
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            error.error.message ?: "An unknown error occurred",
                            withDismissAction = true,
                            duration = SnackbarDuration.Long
                        )
                    }
                }

                pagingItems.loadState.append is LoadState.Error -> {
                    val error = pagingItems.loadState.append as LoadState.Error
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            error.error.message ?: "An unknown error occurred",
                            withDismissAction = true,
                            duration = SnackbarDuration.Long
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview_ArticleList() {
    val fakeData = List(10) {
        Article(
            source = Source(id = "me", name = "News Source Name"),
            author = "John Smith",
            title = "This Is A News Story",
            description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
            url = "https://wavehealth.app",
            urlToImage = "https://images.unsplash.com/photo-1504711434969-e33886168f5c?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            publishedAt = "2023-11-26T16:34:00Z",
            content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Nulla aliquet porttitor lacus luctus accumsan tortor posuere ac. Montes nascetur ridiculus mus mauris vitae ultricies leo integer malesuada. Condimentum lacinia quis vel eros donec. Ultricies tristique nulla aliquet enim tortor at auctor urna nunc. Odio tempor orci dapibus ultrices in iaculis nunc sed. Morbi leo urna molestie at elementum eu. Nibh nisl condimentum id venenatis. Id aliquet risus feugiat in ante metus dictum at tempor. Nec ultrices dui sapien eget mi proin sed libero. Integer eget aliquet nibh praesent tristique magna. Quam id leo in vitae turpis massa sed. Vitae turpis massa sed elementum tempus. Libero id faucibus nisl tincidunt. Sed elementum tempus egestas sed sed risus pretium quam. In aliquam sem fringilla ut morbi."
        )
    }

    val pagingData = PagingData.from(fakeData)
    val fakeDataFlow = MutableStateFlow(pagingData)

    val listState = rememberLazyListState()

    ArticleList(listState, articles = fakeDataFlow) {}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArticleCard(article: Article, onClick: (Article) -> Unit) {
    Card(
        onClick = {
            onClick(article)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        article.apply {
            GlideImage(
                imageModel = { urlToImage },
                requestBuilder = {
                    Glide
                        .with(LocalContext.current)
                        .asBitmap()
                        .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .transition(withCrossFade())
                },
                loading = {
                    Box(modifier = Modifier.matchParentSize()) {
                        LinearProgressIndicator()
                    }
                },
                previewPlaceholder = R.drawable.ic_launcher_background,
                modifier = Modifier
                    .fillMaxWidth()
                    .sizeIn(minHeight = 8.dp, maxHeight = 212.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            )

            val formatter = DateTimeFormatter.ofLocalizedDateTime(
                FormatStyle.LONG,
                FormatStyle.MEDIUM
            ).withZone(ZoneId.systemDefault())

            val publishDate = try {
                Instant.parse(publishedAt)?.let {
                    formatter.format(it)
                } ?: "Could not format date"
            } catch (ex: ParseException) {
                "Could not parse date"
            }

            Text(
                text = publishDate,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Right,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 8.dp)
            )
        }
    }
}