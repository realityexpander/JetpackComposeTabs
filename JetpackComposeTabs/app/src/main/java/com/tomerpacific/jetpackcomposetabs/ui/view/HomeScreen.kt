package com.tomerpacific.jetpackcomposetabs.ui.view

import android.graphics.Bitmap
import android.util.Log
import android.webkit.WebView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.LoadingState
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewNavigator
import com.google.accompanist.web.rememberWebViewState
import com.tomerpacific.jetpackcomposetabs.MainViewModel

@Composable
fun HomeScreen(viewModel: MainViewModel) {

    WebBrowser()

//    Column(modifier = Modifier.fillMaxSize().draggable(
//        state = viewModel.dragState.value!!,
//        orientation = Orientation.Horizontal,
//        onDragStarted = {  },
//        onDragStopped = {
//            viewModel.updateTabIndexBasedOnSwipe()
//        }),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center) {
//        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
//            Text(
//                text = "Welcome Home!",
//                textAlign = TextAlign.Center,
//                fontSize = 20.sp,
//                fontWeight = FontWeight.Bold
//            )
//        }
//    }
}

@Composable
fun WebBrowser() {
    var url by remember { mutableStateOf("https://www.ssec.wisc.edu/data/us_comp/large") }
    val state = rememberWebViewState(url = url)
    val navigator = rememberWebViewNavigator()
    var textFieldValue by remember(state.lastLoadedUrl) {
        mutableStateOf(state.lastLoadedUrl ?: "")
    }

    Column {
        TopAppBar {
            IconButton(onClick = { navigator.navigateBack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
            IconButton(onClick = { navigator.navigateForward() }) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Forward"
                )
            }
//            Text(
//                text = "Web Browser", style = TextStyle(
//                    color = Color.White,
//                    fontSize = MaterialTheme.typography.h6.fontSize,
//                    fontWeight = MaterialTheme.typography.h6.fontWeight
//                )
//            )
        }

        Row(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
                .height(IntrinsicSize.Min)
        ) {
            Row(
                modifier = Modifier
                    .weight(2f),
            ) {
                BasicTextField(
                    modifier = Modifier
                        .weight(8f)
                        .align(Alignment.CenterVertically),
                    value = textFieldValue,
                    onValueChange = { textFieldValue = it },
                    maxLines = 1,
                )

                if (state.errorsForCurrentRequest.isNotEmpty()) {
                    Icon(
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically),
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Error",
                        tint = Color.Red
                    )
                }
                IconButton(onClick = { navigator.reload() }) {
                    Icon(
                        modifier = Modifier
                            .align(Alignment.CenterVertically),
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh"
                    )
                }
                IconButton(onClick = { url = textFieldValue }) {
                    Icon(
                        modifier = Modifier
                            .align(Alignment.CenterVertically),
                        imageVector = Icons.Default.Check,
                        contentDescription = "Go"
                    )
                }
            }
        }

        val loadingState = state.loadingState
        if (loadingState is LoadingState.Loading) {
            LinearProgressIndicator(
                progress = loadingState.progress,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // A custom WebViewClient and WebChromeClient can be provided via subclassing
        val webClient = remember {
            object : AccompanistWebViewClient() {
                override fun onPageStarted(
                    view: WebView?,
                    url: String?,
                    favicon: Bitmap?
                ) {
                    super.onPageStarted(view, url, favicon)
                    Log.d("Accompanist WebView", "Page started loading for $url")
                }
            }
        }

        WebView(
            state = state,
            modifier = Modifier.weight(1f),
            navigator = navigator,
            onCreated = { webView ->
                webView.settings.javaScriptEnabled = true
            },
            client = webClient
        )
    }
}