package dev.chara.news.ui

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.lifecycle.LifecycleOwner
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dev.chara.news.ui.component.ArticleListComponent
import dev.chara.news.ui.component.DefaultArticleListComponent
import javax.inject.Qualifier

@Module
@InstallIn(ActivityComponent::class)
abstract class ComponentModule {
    @Binds
    abstract fun bindArticleListComponent(
        articleListComponent: DefaultArticleListComponent
    ): ArticleListComponent
}

@Module
@InstallIn(ActivityComponent::class)
object ComponentContextModule {
    @ApplicationComponentContext
    @Provides
    fun provideApplicationComponentContext(activity: Activity): ComponentContext {
        activity as? ComponentActivity
            ?: throw IllegalStateException("Activity isn't a ComponentActivity")
        return DefaultComponentContext(
            lifecycle = (activity as LifecycleOwner).lifecycle,
            savedStateRegistry = activity.savedStateRegistry,
            viewModelStore = activity.viewModelStore,
            onBackPressedDispatcher = activity.onBackPressedDispatcher
        )
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationComponentContext