package com.tomerpacific.jetpackcomposetabs.ui.view

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tomerpacific.jetpackcomposetabs.MainViewModel

@Composable
fun AboutScreen(viewModel: MainViewModel) {

    Column(modifier = Modifier
        .fillMaxSize()
        .draggable(
            state = viewModel.dragState.value!!,
            orientation = Orientation.Horizontal,
            onDragStarted = { },
            onDragStopped = {
                viewModel.updateTabIndexBasedOnSwipe()
            }),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState())
        ) {

            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text(
                    text = "About",
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = {
                    viewModel.addElement()
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Add element")
            }

            /////////////////////////////////
            // Using regular mutableState and list.
            // This works, but code is verbose.
            if(false) {
                viewModel.elements0.value.forEach {
                    Text(text = it)
                }
                Divider()
            }

            /////////////////////////////////
            // Using regular `mutableState` and `mutableListOf`
            // Note: this will not update the UI!!!
            if(false) {
                val elements by viewModel.elements1
                elements.forEach {
                    Text(text = it)
                }
            }
            Divider()

            /////////////////////////////////
            // Using `mutableStateListOf` (combo of mutableState and mutableList)
            // Cleanest and simplest code for mutable lists.
            // Updates UI when list is changed, as expected.
            if(true) {
                viewModel.elements2.forEach {
                    Text(text = it)
                }
            }

        }

    }
}

@Composable
fun Divider() {
    Spacer(modifier = Modifier.height(10.dp))
    Divider()
    Spacer(modifier = Modifier.height(10.dp))
}