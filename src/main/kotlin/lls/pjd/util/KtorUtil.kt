package lls.pjd.util

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

object KtorUtil {

    private val client = HttpClient(CIO)
    val userAgent = "User-Agent" to "PaperJarDownloader/3.0 (https://github.com/Lukas-LLS/PaperJarDownloader)"

    suspend fun getBytes(url: String) = client
        .get(url) {
            header(userAgent.first, userAgent.second)
        }
        .readRawBytes()

    suspend fun getBodyAsText(url: String) = client
        .get(url) {
            header(userAgent.first, userAgent.second)
        }
        .bodyAsText()

    fun close() = client.close()

}