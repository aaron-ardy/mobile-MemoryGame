package com.example.memorygame

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GameScreen(level: Int, tileCount: Int, onBackToMenu: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()

    var currentLevel by remember { mutableStateOf(level) }
    var score by remember { mutableStateOf(0) }

    var sequence by remember { mutableStateOf(listOf<Int>()) }
    val highlightedTiles = remember { mutableStateListOf<Boolean>().apply { repeat(tileCount) { add(false) } } }
    val userInput = remember { mutableStateListOf<Int>() }

    var showStartButton by remember { mutableStateOf(true) }
    var feedbackMessage by remember { mutableStateOf("") }

    var showGameOverDialog by remember { mutableStateOf(false) }
    var playerName by remember { mutableStateOf("") }

    fun resetGame() {
        currentLevel = level
        score = 0
        sequence = listOf()
        userInput.clear()
        feedbackMessage = ""
        showStartButton = true
    }

    val columns = when (tileCount) {
        6 -> 3
        8 -> 4
        else -> 2
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text("Level $currentLevel", style = MaterialTheme.typography.headlineMedium)
        Text("Score: $score", style = MaterialTheme.typography.bodyLarge)

        if (feedbackMessage.isNotEmpty()) {
            Text(
                text = feedbackMessage,
                color = if (feedbackMessage == "Correct!") Color(0xFF4CAF50) else Color.Red,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Column {
            for (row in 0 until (tileCount + columns - 1) / columns) {
                Row {
                    for (col in 0 until columns) {
                        val index = row * columns + col
                        if (index < tileCount) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .padding(6.dp)
                                    .background(
                                        color = when {
                                            highlightedTiles[index] -> Color.Yellow
                                            userInput.contains(index) -> Color.Cyan
                                            else -> Color.LightGray
                                        },
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable(
                                        enabled = !showStartButton &&
                                                sequence.isNotEmpty() &&
                                                userInput.size < sequence.size
                                    ) {
                                        if (userInput.size >= sequence.size) return@clickable

                                        userInput.add(index)
                                        highlightedTiles[index] = true

                                        coroutineScope.launch {
                                            delay(300)
                                            highlightedTiles[index] = false
                                        }

                                        val currentIndex = userInput.lastIndex
                                        if (sequence[currentIndex] != index) {
                                            coroutineScope.launch {
                                                feedbackMessage = "Wrong! Game Over"
                                                delay(1000)
                                                showGameOverDialog = true
                                            }
                                        } else if (userInput.size == sequence.size) {
                                            coroutineScope.launch {
                                                feedbackMessage = "Correct!"
                                                delay(800)
                                                try {
                                                    val earnedPoints = KtorTileHelper.getPointsForLevel(tileCount, true)
                                                    score += earnedPoints
                                                } catch (e: Exception) {
                                                    Log.e("PointsFetch", "Failed: ${e.message}")
                                                }
                                                currentLevel++
                                                userInput.clear()
                                                feedbackMessage = ""
                                                showStartButton = true
                                            }
                                        }
                                    }
                            )
                        } else {
                            Spacer(modifier = Modifier.size(80.dp).padding(6.dp))
                        }
                    }
                }
            }
        }

        if (showStartButton) {
            Button(onClick = {
                showStartButton = false
                userInput.clear()
                feedbackMessage = ""

                coroutineScope.launch {
                    try {
                        val result = KtorTileHelper.getTileSequence(currentLevel, tileCount)
                        sequence = result.map { it.toInt() }
                        Log.d("TileSequence", "Sequence: $sequence")

                        for (index in sequence) {
                            highlightedTiles[index] = true
                            delay(500)
                            highlightedTiles[index] = false
                            delay(300)
                        }
                    } catch (e: Exception) {
                        Log.e("TileFetch", "Failed: ${e.message}")
                        feedbackMessage = "Failed to fetch pattern"
                        showStartButton = true
                    }
                }
            }) {
                Text("Start")
            }
        }

        Button(onClick = onBackToMenu) {
            Text("Back to Menu")
        }
    }

    if (showGameOverDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Game Over") },
            text = {
                Column {
                    Text("Your score: $score")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = playerName,
                        onValueChange = { playerName = it },
                        label = { Text("Enter your name") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    coroutineScope.launch {
                        try {
                            if (playerName.isNotBlank()) {
                                KtorTileHelper.submitScore(playerName, score, currentLevel)
                            }
                        } catch (e: Exception) {
                            Log.e("SubmitScore", "Error: ${e.message}")
                        }
                        showGameOverDialog = false
                        resetGame()
                        onBackToMenu()
                    }
                }) {
                    Text("Submit")
                }
            }
        )
    }
}
