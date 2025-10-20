package com.example.railmake.ui.draw

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.railmake.ui.theme.RailMakeTheme

class DrawActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RailMakeTheme {
                DrawScreen()
            }
        }
    }
}
