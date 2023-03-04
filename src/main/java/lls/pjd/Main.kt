package lls.pjd

import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

object Main {

    @JvmStatic
    fun main(args: Array<String>) {

        if (args.size > 1) {
            println("Usage: java -jar PaperJarDownloader.jar <version>")
            return
        }

        var version = if (args.isNotEmpty()) {
            args[0]
        } else {
            "latest"
        }

        val client = HttpClient.newHttpClient()

        if (version == "latest") {
            val versions = getVersions(client)
            version = versions
                .filterNot { it.contains("pre") }
                .maxByOrNull {
                    val split = it.split(".")
                    split[0].toInt() * 100 + split[1].toInt() * 10 + runCatching { split[2].toInt() }.getOrElse { 0 }
                } ?: ""
        }

        val latestBuild = getBuilds(client, version).maxOf { it }

        println("Downloading Paper version $version build $latestBuild")

        val latestBuildData = getSpecificBuild(client, version, latestBuild)

        println("Downloaded ${latestBuildData.size} bytes")
        println("Writing to file")

        val serverDir = File("server")

        if (!serverDir.exists()) {
            serverDir.mkdir()
        }

        val serverFile = serverDir.resolve("server.jar")

        if (serverFile.exists()) {
            serverFile.delete()
        }

        serverFile.writeBytes(latestBuildData)

        println("Done")
    }

    private fun getSpecificBuild(client: HttpClient, version: String, build: Int): ByteArray {
        val response = client.send(
            HttpRequest.newBuilder(URI.create(getSpecificBuildUrl(version, build))).build(),
            HttpResponse.BodyHandlers.ofInputStream()
        )
        try {
            return response.body().readBytes()
        } finally {
            response.body().close()
        }
    }

    private fun getVersions(client: HttpClient): List<String> {
        val response = client.send(
            HttpRequest.newBuilder(URI.create(getVersionsUrl())).build(),
            HttpResponse.BodyHandlers.ofString()
        )
        return parseJSONArrayAttribute(response.body(), "versions")
    }

    private fun getBuilds(client: HttpClient, version: String): List<Int> {
        val response = client.send(
            HttpRequest.newBuilder(URI.create(getBuildsUrl(version))).build(),
            HttpResponse.BodyHandlers.ofString()
        )
        return parseJSONArrayAttribute(response.body(), "builds").map { it.toInt() }
    }

    private fun parseJSONArrayAttribute(json: String, attribute: String): List<String> {
        var index = json.indexOf("\"$attribute\":")
        index += attribute.length + 3
        return json
            .substring(index)
            .replace(Regex("[\\[\\]{}\"]|(\"$attribute\":)"), "")
            .split(",")
    }

    private fun getVersionsUrl(): String {
        return "https://papermc.io/api/v2/projects/paper"
    }

    private fun getBuildsUrl(version: String): String {
        return "https://papermc.io/api/v2/projects/paper/versions/$version"
    }

    private fun getSpecificBuildUrl(version: String, build: Int): String {
        return "https://papermc.io/api/v2/projects/paper/versions/$version/builds/$build/downloads/paper-$version-$build.jar"
    }

}