package com.tomerpacific.jetpackcomposetabs.ui.view

import android.graphics.Bitmap
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.WebView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.web.AccompanistWebChromeClient
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.WebViewNavigator
import com.google.accompanist.web.WebViewState
import com.google.accompanist.web.rememberWebViewNavigator
import com.google.accompanist.web.rememberWebViewState
import com.tomerpacific.jetpackcomposetabs.MainViewModel

data class WebState(
    val url:MutableState<String>,
    val state: WebViewState,
    val navigator: WebViewNavigator,
    val webClient: MyWebClient,
    val chromeClient: MyChromeClient
)

class MyChromeClient: AccompanistWebChromeClient() {
    var scrollX = 0
    var scrollY = 0
    var webView: WebView? = null
    var isScrollSet = false
    var isProgress100 = false

    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
        //Log.d("Accompanist WebView", "Progress changed to $newProgress")
        println("Progress $newProgress")
        if (newProgress == 100) {
            isProgress100 = true
        }
    }

    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
        Log.d("Accompanist WebView", "onConsoleMessage: ${consoleMessage?.message()}")
        return super.onConsoleMessage(consoleMessage)
    }

    fun onScrollChanged(
        webView: WebView?,
        scrollX: Int,
        scrollY: Int,
        oldScrollX: Int,
        oldScrollY: Int
    ) {
        webView ?: return
        this.webView = webView

        this.scrollX = scrollX
        this.scrollY = scrollY
        Log.d("Accompanist WebView", "Scroll changed to $scrollX, $scrollY")
    }

    fun scrollToSavedPosition() {
        Log.d("Accompanist WebView", "Scrolling to saved position $scrollX, $scrollY")
        webView?.scrollTo(scrollX, scrollY)
        isScrollSet = true
    }
}

class MyWebClient(
    val onPageCommitAvailable: () -> Unit = {}
): AccompanistWebViewClient() {
    var isAlreadyLoadedFromWeb = false
    var isPageFinished = false

    override fun onPageStarted(
        view: WebView?,
        url: String?,
        favicon: Bitmap?
    ) {
        if (isAlreadyLoadedFromWeb) {
            // We've already loaded the page, so this is a new page load
            // due to a navigation action
            Log.d("Accompanist WebView", "isAlreadyLoaded = true, for : $url")
            return
        }
        isAlreadyLoadedFromWeb = true
        isPageFinished = false

        super.onPageStarted(view, url, favicon)
        Log.d("Accompanist WebView", "Page started loading for $url")
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        Log.d("Accompanist WebView", "Page finished loading for $url")

        isPageFinished = true
    }

    override fun onPageCommitVisible(view: WebView?, url: String?) {
        super.onPageCommitVisible(view, url)
        Log.d("Accompanist WebView", "Page commit visible for $url")
        onPageCommitAvailable()
    }
}

@Composable
fun TabLayout(viewModel: MainViewModel) {
    val tabIndex = viewModel.tabIndex.observeAsState()

    val startUrl = "https://www.ssec.wisc.edu/data/us_comp/large"
    val url = remember { mutableStateOf(startUrl) }
    val state = rememberWebViewState(url = url.value)
    val navigator = rememberWebViewNavigator()
    val chromeClient = remember {
        MyChromeClient()
    }
    val webClient = remember {
        MyWebClient(
            onPageCommitAvailable =  {
                chromeClient.scrollToSavedPosition()
            }
        )
    }
    val webState = remember {
        mutableStateOf(
            WebState(
                url = url,
                state = state,
                navigator = navigator,
                webClient = webClient,
                chromeClient = chromeClient
            )
        )
    }

//    val url = remember { mutableStateOf("https://www.ssec.wisc.edu/data/us_comp/large") }
//    val state = rememberWebViewState(url = url.value)
//    val navigator = rememberWebViewNavigator()
//    var textFieldValue = remember(state.lastLoadedUrl) {
//        mutableStateOf(state.lastLoadedUrl ?: "")
//    }

    Column(modifier = Modifier.fillMaxWidth()) {
        TabRow(selectedTabIndex = tabIndex.value!!) {
            viewModel.tabs.forEachIndexed { index, title ->
                Tab(text = { Text(title) },
                    selected = tabIndex.value!! == index,
                    onClick = { viewModel.updateTabIndex(index) },
                    icon = {
                        when (index) {
                            0 -> Icon(imageVector = Icons.Default.Home, contentDescription = null)
                            1 -> Icon(imageVector = Icons.Default.Info, contentDescription = null)
                            2 -> Icon(imageVector = Icons.Default.Settings, contentDescription = null)
                        }
                    }
                )
            }
        }

        when (tabIndex.value) {
            0 -> HomeScreen(viewModel = viewModel, webState = webState)
            1 -> AboutScreen(viewModel = viewModel)
            2 -> SettingsScreen(viewModel = viewModel)
        }
    }
}

@Composable
@Preview(showBackground = true)
fun TabLayoutPreview() {
    TabLayout(viewModel = MainViewModel())
}