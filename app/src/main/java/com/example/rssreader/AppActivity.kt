package com.example.rssreader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import com.example.rssreader.app.FeedSideEffect
import com.example.rssreader.app.FeedStore
import com.example.rssreader.composeui.MainScreen
import com.example.rssreader.ui.theme.RssReaderTheme
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import kotlinx.coroutines.flow.filterIsInstance
import org.koin.android.ext.android.inject

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RssReaderTheme {
                ProvideWindowInsets {
                    val store: FeedStore by inject()
                    val scaffoldState = rememberScaffoldState()
                    val error = store.observeSideEffect()
                        .filterIsInstance<FeedSideEffect.Error>()
                        .collectAsState(null)
                    LaunchedEffect(error.value) {
                        error.value?.let {
                            scaffoldState.snackbarHostState.showSnackbar(
                                it.error.message.toString()
                            )
                        }
                    }
                    Box(
                        Modifier.padding(
                            rememberInsetsPaddingValues(
                                insets = LocalWindowInsets.current.systemBars,
                                applyStart = true,
                                applyTop = false,
                                applyEnd = true,
                                applyBottom = false
                            )
                        )
                    ) {
                        Scaffold(
                            scaffoldState = scaffoldState,
                            snackbarHost = { hostState ->
                                SnackbarHost(
                                    hostState = hostState,
                                    modifier = Modifier.padding(
                                        rememberInsetsPaddingValues(
                                            insets = LocalWindowInsets.current.systemBars,
                                            applyBottom = true
                                        )
                                    )
                                )
                            }
                        ) {
                            Navigator(MainScreen())
                        }
                    }
                }
            }
        }
    }
}
