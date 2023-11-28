package dev.chara.news.ui.content

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.glide.GlideImage
import dev.chara.news.R
import dev.chara.news.model.Article
import dev.chara.news.model.Source
import dev.chara.news.ui.component.ArticleComponent
import java.text.ParseException
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun ArticleContent(component: ArticleComponent) {
    Article(component.article, component::onDismiss)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Article(article: Article, onUp: () -> Unit) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = onUp) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Go back")
                        }
                    },
                    title = { },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                )
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .padding(8.dp)
                )
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(article.url)))
                },
                icon = {
                    Icon(Icons.Filled.OpenInBrowser, contentDescription = "Read article")
                },
                text = {
                    Text("Read article")
                }
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .verticalScroll(scrollState)
        ) {
            article.apply {
                GlideImage(
                    imageModel = { urlToImage },
                    loading = {
                        Box(modifier = Modifier.matchParentSize()) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    },
                    previewPlaceholder = R.drawable.ic_launcher_background,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 212.dp)
                )
                Text(
                    text = source.name,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                )
                Row {
                    author?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                        )
                    }
                }

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
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .padding(bottom = 8.dp)
                )

                Text(
                    text = description ?: "",
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview_Article() {
    val article = Article(
        source = Source(id = "me", name = "News Source Name"),
        author = "John Smith",
        title = "This Is A News Story",
        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
        url = "https://wavehealth.app",
        urlToImage = "https://images.unsplash.com/photo-1504711434969-e33886168f5c?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
        publishedAt = "2023-11-26T16:34:00Z",
        content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Nulla aliquet porttitor lacus luctus accumsan tortor posuere ac. Montes nascetur ridiculus mus mauris vitae ultricies leo integer malesuada. Condimentum lacinia quis vel eros donec. Ultricies tristique nulla aliquet enim tortor at auctor urna nunc. Odio tempor orci dapibus ultrices in iaculis nunc sed. Morbi leo urna molestie at elementum eu. Nibh nisl condimentum id venenatis. Id aliquet risus feugiat in ante metus dictum at tempor. Nec ultrices dui sapien eget mi proin sed libero. Integer eget aliquet nibh praesent tristique magna. Quam id leo in vitae turpis massa sed. Vitae turpis massa sed elementum tempus. Libero id faucibus nisl tincidunt. Sed elementum tempus egestas sed sed risus pretium quam. In aliquam sem fringilla ut morbi."
    )

    Article(article) {}
}