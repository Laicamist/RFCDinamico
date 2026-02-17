package com.laicamist.rfcdinamico

import androidx.compose.material.MaterialTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.runtime.*
fun main() = application {
    val windowState = rememberWindowState(
        size = DpSize(1920.dp, 1080.dp)
    )

    Window(
        onCloseRequest = ::exitApplication,
        title = "RFC Dinámico",
        state = windowState,
    ) {
        MaterialTheme {
            App()  // Tu Composable RFC
        }
    }
}