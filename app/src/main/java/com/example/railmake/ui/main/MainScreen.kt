package com.example.railmake.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.railmake.R
import com.example.railmake.ui.theme.RailMakeTheme

@Composable
fun MainMenuScreen(
    onDrawClick: () -> Unit,
    onTestClick: () -> Unit,
    onOptionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Companion.CenterHorizontally
    ) {
        Button(
            onClick = onDrawClick,
            modifier = Modifier.Companion.fillMaxWidth()
        ) {
            Text(stringResource(id = R.string.main_button_draw))
        }
        Spacer(modifier = Modifier.Companion.height(16.dp))
        Button(
            onClick = onTestClick,
            modifier = Modifier.Companion.fillMaxWidth()
        ) {
            Text(stringResource(id = R.string.main_button_test))
        }
        Spacer(modifier = Modifier.Companion.height(16.dp))
        Button(
            onClick = onOptionClick,
            modifier = Modifier.Companion.fillMaxWidth()
        ) {
            Text(stringResource(id = R.string.main_button_option))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainMenuScreenPreview() {
    RailMakeTheme {
        MainMenuScreen(
            onDrawClick = {},
            onTestClick = {},
            onOptionClick = {}
        )
    }
}