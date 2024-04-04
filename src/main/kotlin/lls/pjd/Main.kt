package lls.pjd

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

object Main {

    @JvmStatic
    fun main(args: Array<String>) {

        val logger = LoggerFactory.getLogger("PaperJarDownloader")

        if (args.size > 1) {
            logger.warn("Too many arguments")
            return
        }

        var version = if (args.isNotEmpty()) {
            args[0]
        } else {
            "latest"
        }

        val client = HttpClient(CIO)

        if (version == "latest") {
            val versions: List<String>
            runBlocking {
                versions = getVersions(client)
            }
            version = versions
                .filterNot { it.contains("pre") }
                .maxByOrNull {
                    val split = it.split(".")
                    split[0].toInt() * 100 + split[1].toInt() * 10 + runCatching { split[2].toInt() }.getOrElse { 0 }
                } ?: ""
        }

        val latestBuild: Int

        runBlocking {
            latestBuild = getBuilds(client, version).maxOf { it }
        }

        logger.info("Checking if current version is up to date")

        val serverDir = File("server")

        val currentVersion = checkForCurrentVersion(serverDir, logger)

        if (currentVersion != null) {
            if (currentVersion.second == version && currentVersion.first == latestBuild) {
                logger.info("Current version is up to date (MC ${currentVersion.second} - build ${currentVersion.first})")
                return
            } else {
                logger.info("Current version is not up to date (MC ${currentVersion.second} - build ${currentVersion.first})")
            }
        } else {
            logger.info("Could not check if current version is up to date")
        }

        logger.info("Downloading Paper version $version build $latestBuild")

        val latestBuildData: ByteArray

        runBlocking {
            latestBuildData = getSpecificBuild(client, version, latestBuild)
        }

        logger.info("Downloaded ${String.format("%.2f", latestBuildData.size / 1048576.0)} MB")
        logger.info("Writing to file")

        if (!serverDir.exists()) {
            logger.info("Creating server directory")
            serverDir.mkdir()
        }

        val serverFile = serverDir.resolve("server.jar")

        if (serverFile.exists()) {
            logger.info("Deleting old server.jar")
            serverFile.delete()
        }

        serverFile.writeBytes(latestBuildData)

        logger.info("Done")
    }

    private fun checkForCurrentVersion(serverDir: File, logger: Logger): Pair<Int, String>? {
        if (serverDir.exists()) {
            val versionHistory = serverDir.resolve("version_history.json")
            if (versionHistory.exists()) {
                var history = versionHistory.readText()
                if (history.indexOf("\"currentVersion\":") == -1) {
                    logger.warn("Could not find current version in version history")
                    return null
                }
                history = history.substring(history.indexOf("\"currentVersion\":") + 17)
                history = history
                    .removeSuffix("}")
                    .removeSurrounding("\"")
                    .removePrefix("git-Paper-")
                    .removeSuffix(")")
                if (!history.contains(" ")) {
                    logger.warn("Could not parse current version from version history")
                    return null
                }
                val currentBuild = history.substring(0, history.indexOf(" ")).toIntOrNull()
                val currentVersion = history.substring(history.lastIndexOf(" ") + 1)
                if (currentBuild == null) {
                    logger.warn("Could not parse current build from version history")
                    return null
                }
                return currentBuild to currentVersion
            } else {
                logger.info("Version history does not exist")
            }
        } else {
            logger.info("Server directory does not exist")
        }
        return null
    }

    private suspend fun getSpecificBuild(client: HttpClient, version: String, build: Int): ByteArray {
        return client.get(getSpecificBuildUrl(version, build)).readBytes()
    }

    private suspend fun getVersions(client: HttpClient): List<String> {
        return parseJSONArrayAttribute(client.get(getVersionsUrl()).bodyAsText(), "versions")
    }

    private suspend fun getBuilds(client: HttpClient, version: String): List<Int> {
        val response = client.get(getBuildsUrl(version))
        return parseJSONArrayAttribute(response.bodyAsText(), "builds").map { it.toInt() }
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