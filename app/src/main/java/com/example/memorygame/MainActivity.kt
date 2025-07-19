package com.example.memorygame
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import android.app.Activity
import androidx.compose.ui.platform.LocalContext


import com.example.memorygame.ui.theme.MemoryGameTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MemoryGameTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MenuScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MenuScreen(modifier: Modifier = Modifier) {
    var showDifficultyDialog by remember { mutableStateOf(false) }
    var showHighScoreDialog by remember { mutableStateOf(false) }
    var selectedDifficulty by remember { mutableStateOf("Easy") }
    val context = LocalContext.current
    val activity = context as? Activity
    var showExitDialog by remember { mutableStateOf(false) }


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🧠 Memory Game", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { /* TODO: Start game with selectedDifficulty */ }, modifier = Modifier.fillMaxWidth()) {
            Text("Play")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("Difficulty: $selectedDifficulty", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { showDifficultyDialog = true }, modifier = Modifier.fillMaxWidth()) {
            Text("Choose Difficulty")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { showHighScoreDialog = true }, modifier = Modifier.fillMaxWidth()) {
            Text("Leaderboard")
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { showExitDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Exit", color = MaterialTheme.colorScheme.onError)
        }
    }

    // modal after clicking diff button
    if (showDifficultyDialog) {
        DifficultyDialog(
            onDismiss = { showDifficultyDialog = false },
            onSelect = {
                selectedDifficulty = it
                showDifficultyDialog = false
                // TODO: Send difficulty to backend via REST (XAMPP + PHP + phpMyAdmin)
            }
        )
    }

    // hs modal
    if (showHighScoreDialog) {
        HighScoreDialog(onDismiss = { showHighScoreDialog = false })
    }

    // exit modal
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Exit Game") },
            text = { Text("Are you sure you want to exit the game?") },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    activity?.finish() // ✅ This closes the app
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("No")
                }
            }
        )
    }
}

@Composable
fun HighScoreDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("High Scores") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                // TODO: Replace with actual scores loaded from REST API
                val dummyScores = listOf(
                    "1. Balbas — 1200",
                    "2. JamieOliver — 950",
                    "3. Rat — 880",
                    "4. Kupal — 750",
                    "5. Hotdog — 620",
                    "6. Cakedup — 590",
                    "7. Epstein — 12"
                )
                dummyScores.forEach { score ->
                    Text(score, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun DifficultyDialog(onDismiss: () -> Unit, onSelect: (String) -> Unit) {
    val options = listOf("Easy", "Intermediate", "Hard")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Difficulty") },
        text = {
            Column {
                options.forEach { option ->
                    TextButton(onClick = { onSelect(option) }) {
                        Text(option)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}

@Preview(showBackground = true)
@Composable
fun MenuPreview() {
    MemoryGameTheme {
        MenuScreen()
    }
}
