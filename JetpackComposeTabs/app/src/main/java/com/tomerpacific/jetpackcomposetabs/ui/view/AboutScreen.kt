package com.tomerpacific.jetpackcomposetabs.ui.view

import android.util.Log
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.tomerpacific.jetpackcomposetabs.MainViewModel

@Composable
fun AboutScreen(viewModel: MainViewModel) {

    var isDragToTheLeft by remember { mutableStateOf(false) }
    val dragState = rememberDraggableState(onDelta = { delta ->
        isDragToTheLeft = delta > 0
    })

    Column(modifier = Modifier.fillMaxSize().draggable(
        state = dragState,
        orientation = Orientation.Horizontal,
        onDragStarted = {  },
        onDragStopped = {
            viewModel.updateTabIndexBasedOnSwipe(isSwipeToTheLeft = isDragToTheLeft)
        }),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text(
                text = "About",
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}