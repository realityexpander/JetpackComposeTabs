package com.tomerpacific.jetpackcomposetabs.ui.view

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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.web.LoadingState
import com.google.accompanist.web.WebView
import com.google.accompanist.web.WebViewNavigator
import com.google.accompanist.web.WebViewState
import com.tomerpacific.jetpackcomposetabs.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    webState: MutableState<WebState>
) {

    WebBrowser(
        url = webState.value.url,
        state = webState.value.state,
        navigator = webState.value.navigator,
        webClient = webState.value.webClient,
        chromeClient = webState.value.chromeClient
    )

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
fun WebBrowser(
    url: MutableState<String>, // = remember { mutableStateOf("https://www.ssec.wisc.edu/data/us_comp/large") },
    state: WebViewState, // = rememberWebViewState(url = url.value),
    navigator: WebViewNavigator, // = rememberWebViewNavigator(),
    webClient: MyWebViewClient,
    chromeClient: MyChromeClient
) {
//    var url = remember { mutableStateOf("https://www.ssec.wisc.edu/data/us_comp/large") }
//    val state = rememberWebViewState(url = url)
//    val navigator = rememberWebViewNavigator()
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
                    onValueChange = {
                        textFieldValue = it
                        webClient.isAlreadyLoadedFromWeb = false
                    },
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
                IconButton(onClick = {
                    webClient.isAlreadyLoadedFromWeb = false
                    navigator.reload()
                }) {
                    Icon(
                        modifier = Modifier
                            .align(Alignment.CenterVertically),
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh"
                    )
                }
                IconButton(onClick = { url.value = textFieldValue }) {
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

//        // A custom WebViewClient and WebChromeClient can be provided via subclassing
//        val webClient = remember {
//            object : AccompanistWebViewClient() {
//                override fun onPageStarted(
//                    view: WebView?,
//                    url: String?,
//                    favicon: Bitmap?
//                ) {
//                    super.onPageStarted(view, url, favicon)
//                    Log.d("Accompanist WebView", "Page started loading for $url")
//                }
//            }
//        }

//        val chromeClient = remember {
//            object : AccompanistWebChromeClient() {
//                override fun onProgressChanged(view: WebView?, newProgress: Int) {
//                    super.onProgressChanged(view, newProgress)
//                    Log.d("Accompanist WebView", "Progress changed to $newProgress")
//                }
//
//                override fun onConsoleMessage(
//                    message: String?,
//                    lineNumber: Int,
//                    sourceID: String?
//                ) {
//                    super.onConsoleMessage(message, lineNumber, sourceID)
//                }
//            }
//        }

        val coroutineScope = rememberCoroutineScope()

        WebView(
            state = state,
            modifier = Modifier.weight(1f),
            navigator = navigator,
            onCreated = { webView ->
                webView.settings.javaScriptEnabled = true
//                webView.scrollTo(chromeClient.scrollX, chromeClient.scrollY)
                chromeClient.isScrollSet = false
                webClient.isPageFinished = false
                chromeClient.isProgress100 = false

                webView.evaluateJavascript(
                    """
//                       document.body.addEventListener('load',(event) => {
//                           console.log('Page loaded...');
//                       });
                       
//                       let stateCheck = setInterval(() => {
//                          let state = document.readyState;
//                          console.log('Checking document ready state...' + state);
//                          if (document.readyState === 'complete') { 
//                             clearInterval(stateCheck); // document ready 
//                          } 
//                       }, 100)
                       
                    """.trimIndent()
                ) {}
                //{ value -> Log.d("WebView", "Value received: $value") }

                coroutineScope.launch {
                    var oldScrollX = -1
                    var oldScrollY = -1

                    println("PRE isPageFinished: ${webClient.isPageFinished}, isScrollSet: ${chromeClient.isScrollSet}")
                    while(
                        !webClient.isPageFinished
                        || !chromeClient.isScrollSet
                        || !chromeClient.isProgress100
                    ) {
                        println("IN Waiting for page to finish loading... isPageFinished: ${webClient.isPageFinished}, isScrollSet: ${chromeClient.isScrollSet}")
                        delay(150)
                    }
                    println("POST isPageFinished: ${webClient.isPageFinished}, isScrollSet: ${chromeClient.isScrollSet}")

                    webView.scrollTo(chromeClient.scrollX, chromeClient.scrollY)
                    delay(100)

                    // Track scroll position by polling
                    while (true) {
                        delay(100)
                        val newScrollX = webView.scrollX
                        val newScrollY = webView.scrollY

                        if(newScrollX != oldScrollX || newScrollY != oldScrollY) {
                            chromeClient.onScrollChanged(webView, newScrollX, newScrollY, oldScrollX, oldScrollY)
                            oldScrollX = newScrollX
                            oldScrollY = newScrollY
                            //Log.d("WebView", "Scrolling... scrollX: $oldScrollX, scrollY: $oldScrollY")
                        }
                    }
//                    webView.evaluateJavascript(
////                    "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();",
//                        """
////                        setInterval(function() {
////                            console.log("scrollX: " + window.scrollX + ", scrollY: " + window.scrollY);
////                        }, 500);
//
////                        document.body.addEventListener('scroll',(event) => {
////                            console.log('Scrolling...');
////                        });
//
////                        document.body.onscroll = function(event) {
////                            console.log("scrollX: " + window.scrollX + ", scrollY: " + window.scrollY);
////                        };
//                    """.trimIndent()
//                    ) { value -> Log.d("WebView", "Value received: $value") }

                }
            },
            client = webClient,
            chromeClient = chromeClient
        )
    }
}