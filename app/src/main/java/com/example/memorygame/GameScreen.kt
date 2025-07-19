package com.example.memorygame

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GameScreen(
    onBackToMenu: () -> Unit = {} // Hook this into navController when ready
) {
    var currentLevel by remember { mutableStateOf(1) }
    var hintUsesRemaining by remember { mutableStateOf(3) } // Set limit as needed

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Level: $currentLevel",
            style = MaterialTheme.typography.headlineSmall,
            fontSize = 22.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 🧠 Tile Grid
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            repeat(2) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    repeat(2) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Gray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("💡") // Placeholder for pulse animation
                        }
                    }
                }
            }

            // TODO: Animate pulse sequence from backend data
            // TODO: Store user tap order for validation
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ✅ Submit Answer Button
        Button(
            onClick = {
                // TODO: Validate user’s input against pulse sequence
                // TODO: Send result via REST (submitscore.php)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit")
        }

        // 🔄 Bottom Button Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onBackToMenu) {
                Text("Back")
            }

            Button(
                onClick = {
                    // TODO: Replay pulse animation
                    // TODO: Decrement hintUsesRemaining
                },
                enabled = hintUsesRemaining > 0
            ) {
                Text("Hint ($hintUsesRemaining left)")
            }
        }
    }
}

