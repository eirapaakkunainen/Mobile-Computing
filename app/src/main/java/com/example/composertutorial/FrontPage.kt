package com.example.composertutorial

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.composertutorial.ui.theme.ComposerTutorialTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp


@Composable
fun FrontPage(onNavigateToMessages: () -> Unit) {
    Column (modifier = Modifier.padding(64.dp)) {
        Text(
            modifier = Modifier.padding((45.dp)),
            style = MaterialTheme.typography.titleMedium,
            text = "WELCOME TO MY APP :D!"
        )

        Image(
            painter = painterResource(R.drawable.kissa),
            contentDescription = "Front page picture // App logo",
            modifier = Modifier
                .size(300.dp)
                .border(3.dp, MaterialTheme.colorScheme.secondary)
                .padding(top = 24.dp)
                .padding(bottom = 24.dp)
        )

        Button(
            onClick = onNavigateToMessages,
            modifier = Modifier.padding(start=82.dp))
        {
            Text("Messages")
        }
    }
}


@Preview
@Composable
fun PreviewFrontPge() {
    ComposerTutorialTheme {
        FrontPage(onNavigateToMessages = {})
    }
}
