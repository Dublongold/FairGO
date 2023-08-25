package com.funny.fairGo.network

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.SerialName
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
        @SerialName("can_let_in")
        val canLetIn: Boolean,
        val link: String?
    )

    companion object {
        const val URL = "https://gist.githubusercontent.com/andreyKovalskji/4901253263c8e97137c437b7556434eb/raw/forFairGo"
    }
}