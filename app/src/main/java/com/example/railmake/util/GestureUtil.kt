package com.example.railmake.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlin.math.hypot

fun Offset.distanceTo(other: Offset): Float =
    hypot(x - other.x, y - other.y)

fun Offset.nearestEdge(size: Size): Offset {
    val left = Offset(0f, y)
    val right = Offset(size.width, y)
    val top = Offset(x, 0f)
    val bottom = Offset(x, size.height)

    return listOf(left, right, top, bottom).minBy { it.distanceTo(this) }
}
