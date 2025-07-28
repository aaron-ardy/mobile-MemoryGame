package com.example.memorygame

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.json.JSONObject

object KtorTileHelper {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    private const val BASE_URL = "http://192.168.254.100:8080/MemoryGame"

    suspend fun getTileSequence(level: Int, tileCount: Int): List<Int> {
        val response: String = client.get("$BASE_URL/getTileSequence.php") {
            parameter("level", level)
            parameter("tileCount", tileCount)
        }.body()
        return response.trim()
            .removePrefix("[")
            .removeSuffix("]")
            .split(",")
            .mapNotNull { it.trim().toIntOrNull() }
    }

    suspend fun submitScore(name: String, score: Int, level: Int): Boolean {
        return try {
            val response: HttpResponse = client.submitForm(
                url = "$BASE_URL/submitScore.php",
                formParameters = Parameters.build {
                    append("name", name)
                    append("score", score.toString())
                    append("level", level.toString())
                }
            )
            val responseBody = response.body<String>()
            responseBody.contains("\"success\":true")
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    @Serializable
    data class LeaderboardEntry(
        val name: String,
        val score: Int
    )

    suspend fun fetchTopLeaderboard(): List<LeaderboardEntry> {
        return try {
            val response: HttpResponse = client.get("$BASE_URL/getLeaderboard.php")
            val responseBody = response.body<String>()
            val jsonElement = Json.parseToJsonElement(responseBody)
            val root = jsonElement.jsonObject
            val success = root["success"]?.jsonPrimitive?.content == "true"

            if (success) {
                val data = root["data"] ?: return emptyList()
                Json.decodeFromJsonElement(ListSerializer(LeaderboardEntry.serializer()), data)
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    suspend fun getPointsForLevel(tileCount: Int, completed: Boolean): Int {
        val response = client.get("http://192.168.254.100:8080/MemoryGame/getPointsForLevel.php") {
            parameter("tileCount", tileCount)
            parameter("completed", completed)
        }

        val json = response.bodyAsText()
        val jsonObj = JSONObject(json)
        return if (jsonObj.getBoolean("success")) {
            jsonObj.getInt("points")
        } else {
            0
        }
    }

    }

