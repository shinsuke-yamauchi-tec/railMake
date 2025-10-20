package com.example.railmake.ui.draw

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.railmake.R
import com.example.railmake.ui.theme.RailMakeTheme
import com.example.railmake.util.nearestEdge
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawScreen(modifier: Modifier = Modifier) {
    // 描画履歴：色とストロークのペアを保持
    val strokes = remember { mutableStateListOf<Pair<Color, List<Offset>>>() }

    // 現在選択中の色
    var selectedColor by remember { mutableStateOf(Color.Black) }

    // ボトムシートの状態を管理（Material3の標準API）
    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.Hidden,
        confirmValueChange = { true },
        skipHiddenState = false
    )

    // コルーチンで非同期制御（ボトムシートの表示/非表示など）
    val coroutineScope = rememberCoroutineScope()

    // メインUI：描画領域 + ツールバー
    Column(modifier = modifier.fillMaxSize()) {
        DrawCanvasArea(
            modifier = Modifier.weight(1f),
            strokes = strokes,
            strokeColor = selectedColor,
            onStrokeFinished = { stroke ->
                // 描画完了時に履歴へ追加
                strokes.add(selectedColor to stroke)
            }
        )

        DrawToolbar(
            onPaletteClick = {
                // パレットアイコン押下でボトムシート表示
                coroutineScope.launch { sheetState.show() }
            }
        )
    }

    // ボトムシート：色選択UI
    if (sheetState.currentValue != SheetValue.Hidden) {
        ModalBottomSheet(
            onDismissRequest = {
                // 外側タップでボトムシートを閉じる
                coroutineScope.launch { sheetState.hide() }
            },
            sheetState = sheetState
        ) {
            ColorPickerSheet(
                selectedColor = selectedColor,
                onColorSelected = { color ->
                    // 色選択時に状態更新＋ボトムシートを閉じる
                    selectedColor = color
                    coroutineScope.launch { sheetState.hide() }
                }
            )
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun DrawCanvasArea(
    modifier: Modifier = Modifier,
    strokes: SnapshotStateList<Pair<Color, List<Offset>>>,
    strokeColor: Color,
    onStrokeFinished: (List<Offset>) -> Unit
) {
    // 一時的なストローク（描画中の線）
    var tempStroke by remember { mutableStateOf<List<Offset>>(emptyList()) }

    // 親サイズを取得するためのスコープ
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val canvasSize = Size(
            constraints.maxWidth.toFloat(),
            constraints.maxHeight.toFloat()
        )

        // ジェスチャー入力領域
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(canvasSize) {
                    awaitEachGesture {
                        val down = awaitFirstDown()
                        val stroke = mutableListOf<Offset>()

                        // 線の始点を端にスナップ
                        val startEdge = down.position.nearestEdge(canvasSize)
                        stroke.add(startEdge)
                        stroke.add(down.position)
                        tempStroke = stroke.toList()

                        var previous: Offset
                        do {
                            val event = awaitPointerEvent()
                            val current = event.changes.first().position
                            stroke.add(current)
                            tempStroke = stroke.toList()
                            previous = current
                        } while (event.changes.any { it.pressed })

                        // 線の終点も端にスナップ
                        val endEdge = previous.nearestEdge(canvasSize)
                        stroke.add(endEdge)

                        // 完了したストロークを通知
                        onStrokeFinished(stroke.toList())
                        tempStroke = emptyList()
                    }
                }
        ) {
            // 描画処理：履歴と現在のストロークを描画
            Canvas(modifier = Modifier.fillMaxSize()) {
                for ((color, stroke) in strokes) {
                    for (i in 0 until stroke.size - 1) {
                        drawLine(
                            color = color,
                            start = stroke[i],
                            end = stroke[i + 1],
                            strokeWidth = 4f
                        )
                    }
                }

                for (i in 0 until tempStroke.size - 1) {
                    drawLine(
                        color = strokeColor,
                        start = tempStroke[i],
                        end = tempStroke[i + 1],
                        strokeWidth = 4f
                    )
                }
            }
        }
    }
}

@Composable
fun DrawToolbar(
    modifier: Modifier = Modifier,
    onPaletteClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(Color(0xFFE0E0E0)),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // パレットアイコン：色選択ボトムシートを開く
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Palette",
            modifier = Modifier.clickable { onPaletteClick() }
        )

        // 削除アイコン（未実装）
        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")

        // 保存アイコン（未実装）
        Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Save")
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColorPickerSheet(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit
) {
    val colors = listOf(
        Color.Black, Color.Red, Color.Green, Color.Blue,
        Color.Yellow, Color.Magenta, Color.Cyan, Color.Gray
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Text(stringResource(id = R.string.draw_label_color_select), style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        // 折り返し可能な色選択レイアウト
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            for (color in colors) {
                Box(modifier = Modifier.padding(vertical = 4.dp)) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(color, shape = CircleShape)
                            .border(
                                width = if (color == selectedColor) 3.dp else 0.dp,
                                color = Color.DarkGray,
                                shape = CircleShape
                            )
                            .clickable { onColorSelected(color) }
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