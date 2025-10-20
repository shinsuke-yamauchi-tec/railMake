package com.example.railmake.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.railmake.ui.draw.DrawActivity
import com.example.railmake.ui.theme.RailMakeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RailMakeTheme {
                Scaffold(modifier = Modifier.Companion.fillMaxSize()) { innerPadding ->
                    MainMenuScreen(
                        onDrawClick = { openDraw() },
                        onTestClick = { /* テスト画面へ遷移 */ },
                        onOptionClick = { /* オプション画面へ遷移 */ },
                        modifier = Modifier.Companion.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun openDraw() {
        val intent = Intent(this, DrawActivity::class.java)
        startActivity(intent)

    }
}