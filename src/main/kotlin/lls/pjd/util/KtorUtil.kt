package lls.pjd.util

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

object KtorUtil {

    private val client = HttpClient(CIO)

    suspend fun getBytes(url: String) = client.get(url).readRawBytes()

    suspend fun getBodyAsText(url: String) = client.get(url).bodyAsText()

    fun close() = client.close()

}