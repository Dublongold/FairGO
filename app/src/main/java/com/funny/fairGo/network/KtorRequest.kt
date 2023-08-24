package com.funny.fairGo.network

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class KtorRequest {
    suspend fun makeRequestAndGetString(url: String? = null): String? {
        val u = url ?: URL
        val client = HttpClient()
        val response = client.get(u)
        return if(response.status.isSuccess()) {
            response.bodyAsText()
        }
        else {
            null
        }
    }

    suspend fun makeRequestAndGetObject(url: String? = null): Receiver? {
        val u = url ?: URL
        val client = HttpClient()
        val response = client.get(u)
        return if (response.status.isSuccess()) {
            val body = response.bodyAsText()
            Json.decodeFromString(body)
        } else {
            null
        }
    }

    @Serializable
    data class Receiver(
        val allow: Boolean,
        val link: String?
    )

    companion object {
        const val URL = "https://gist.githubusercontent.com/Dublongold/76523dda8e34eeaa94416b43b95f1fc0/raw/forTest.txt"
    }
}