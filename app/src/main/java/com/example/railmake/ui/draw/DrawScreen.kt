package com.example.railmake.ui.draw

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import com.example.railmake.ui.theme.RailMakeTheme
import com.example.railmake.util.nearestEdge

@Composable
fun DrawScreen(modifier: Modifier = Modifier) {
    val strokes = remember { mutableStateListOf<List<Offset>>() }
    var currentStroke by remember { mutableStateOf<List<Offset>>(emptyList()) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val canvasSize = Size(
            constraints.maxWidth.toFloat(),
            constraints.maxHeight.toFloat()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(canvasSize) {
                    awaitEachGesture {
                        val down = awaitFirstDown()
                        val stroke = mutableListOf<Offset>()

                        // 始点 → 最も近い端
                        val startEdge = down.position.nearestEdge(canvasSize)
                        stroke.add(startEdge)
                        stroke.add(down.position)
                        currentStroke = stroke.toList()

                        var previous: Offset
                        do {
                            val event = awaitPointerEvent()
                            val current = event.changes.first().position
                            stroke.add(current)
                            currentStroke = stroke.toList()
                            previous = current
                        } while (event.changes.any { it.pressed })

                        // 終点 → 最も近い端
                        val endEdge = previous.nearestEdge(canvasSize)
                        stroke.add(endEdge)

                        strokes.add(stroke.toList())
                        currentStroke = emptyList()
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // 描画済みのストローク
                for (stroke in strokes) {
                    for (i in 0 until stroke.size - 1) {
                        drawLine(
                            color = Color.Black,
                            start = stroke[i],
                            end = stroke[i + 1],
                            strokeWidth = 4f
                        )
                    }
                }

                // 現在描画中のストローク
                for (i in 0 until currentStroke.size - 1) {
                    drawLine(
                        color = Color.Black,
                        start = currentStroke[i],
                        end = currentStroke[i + 1],
                        strokeWidth = 4f
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DrawScreenPreview() {
    RailMakeTheme {
        DrawScreen()
    }
}