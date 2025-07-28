package com.example.memorygame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import android.app.Activity
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.memorygame.ui.theme.MemoryGameTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MemoryGameTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "menu") {
                    composable("menu") {
                        MenuScreen(navController = navController)
                    }
                    composable(
                        "game/{tileCount}",
                        arguments = listOf(navArgument("tileCount") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val tileCount = backStackEntry.arguments?.getInt("tileCount") ?: 4
                        GameScreen(
                            level = 1, // initial level
                            tileCount = tileCount,
                            onBackToMenu = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MenuScreen(navController: NavHostController) {
    var showDifficultyDialog by remember { mutableStateOf(false) }
    var showHighScoreDialog by remember { mutableStateOf(false) }
    var selectedDifficulty by remember { mutableStateOf("Easy") }
    val context = LocalContext.current
    val activity = context as? Activity
    var showExitDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🧠 Memory Game", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val tileCount = when (selectedDifficulty) {
                    "Intermediate" -> 6
                    "Hard" -> 8
                    else -> 4
                }
                navController.navigate("game/$tileCount")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
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

    if (showDifficultyDialog) {
        DifficultyDialog(
            onDismiss = { showDifficultyDialog = false },
            onSelect = {
                selectedDifficulty = it
                showDifficultyDialog = false
            }
        )
    }

    if (showHighScoreDialog) {
        HighScoreDialog(onDismiss = { showHighScoreDialog = false })
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Exit Game") },
            text = { Text("Are you sure you want to exit the game?") },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    activity?.finish()
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
    val coroutineScope = rememberCoroutineScope()
    var scores by remember { mutableStateOf<List<KtorTileHelper.LeaderboardEntry>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            isLoading = true
            scores = KtorTileHelper.fetchTopLeaderboard()
            isLoading = false
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Top 10 Leaderboard") },
        text = {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            } else {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    if (scores.isEmpty()) {
                        Text("No scores available.", style = MaterialTheme.typography.bodyMedium)
                    } else {
                        scores.take(10).forEachIndexed { index, entry ->
                            Text(
                                "${index + 1}. ${entry.name} — ${entry.score}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
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
    val dummyNav = rememberNavController()
    MemoryGameTheme {
        MenuScreen(navController = dummyNav)
    }
}
