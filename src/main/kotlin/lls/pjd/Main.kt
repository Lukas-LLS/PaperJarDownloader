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

        if (args.isNotEmpty()) {
            logger.warn("This application does not take any arguments")
            logger.warn("If you wish to specify any program behavior please set the environment variables MC_VERSION and FOLIA accordingly")
        }

        var version = System.getenv("MC_VERSION") ?: "latest"

        val folia = System.getenv("FOLIA") != null

        val client = HttpClient(CIO)

        if (version == "latest") {
            val versions: List<String>
            runBlocking {
                versions = getVersions(client, folia)
            }
            version = versions
                .filterNot { it.contains("pre") }
                .maxByOrNull {
                    val split = it.split(".")
                    split[0].toInt() * 100 + split[1].toInt() * 10 + runCatching { split[2].toInt() }.getOrElse { 0 }
                } ?: ""
        }

        val latestBuild: Int?

        runBlocking {
            latestBuild = getBuilds(folia, client, version).maxOfOrNull { it }
        }

        latestBuild ?: run {
            logger.error("Could not get latest build for version $version (${getProject(folia, true)})")
            return
        }

        logger.info("Checking if current version is up to date")

        val serverDir = File("server")

        val currentVersion = checkForCurrentVersion(serverDir, logger)

        if (currentVersion != null) {
            if (currentVersion.second == version && currentVersion.first == latestBuild) {
                logger.info(
                    "Current version is up to date (${
                        getProject(
                            folia,
                            true
                        )
                    } - MC ${currentVersion.second} - build ${currentVersion.first})"
                )
                return
            } else {
                logger.info(
                    "Current version is not up to date (${
                        getProject(
                            folia,
                            true
                        )
                    } - MC ${currentVersion.second} - build ${currentVersion.first})"
                )
            }
        } else {
            logger.info("Could not check if current version is up to date")
        }

        logger.info("Downloading ${getProject(folia, true)} version $version build $latestBuild")

        val latestBuildData: ByteArray

        runBlocking {
            latestBuildData = getSpecificBuild(client, folia, version, latestBuild)
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
                history =
                    history.substring(history.indexOf("\"currentVersion\":") + 17) // 17 is the length of: "currentVersion":
                history = history
                    .removeSuffix("}")
                    .removeSurrounding("\"")

                if (!history.startsWith("git-Paper-")) { // New version history format - without a prefix
                    val elements = history
                        .split("-")
                        .take(2)

                    return elements[1].toInt() to elements[0]
                }

                history = history // Old version history format
                    .removePrefix("git-Paper-")
                    .removeSuffix(")")
                if (!history.contains(" ")) {
                    logger.warn("Could not parse current version from version history")
                    return null
                }
                val currentBuild = history.substringBefore(" ").toIntOrNull()
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

    private suspend fun getSpecificBuild(client: HttpClient, folia: Boolean, version: String, build: Int): ByteArray {
        return client.get(getSpecificBuildUrl(folia, version, build)).readRawBytes()
    }

    private suspend fun getVersions(client: HttpClient, folia: Boolean): List<String> {
        return parseJSONArrayAttribute(client.get(getVersionsUrl(folia)).bodyAsText(), "versions")
    }

    private suspend fun getBuilds(folia: Boolean, client: HttpClient, version: String): List<Int> {
        val response = client.get(getBuildsUrl(folia, version))
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

    private fun getVersionsUrl(folia: Boolean): String {
        return "https://api.papermc.io/v2/projects/" + getProject(folia)
    }

    private fun getBuildsUrl(folia: Boolean, version: String): String {
        return "https://api.papermc.io/v2/projects/${getProject(folia)}/versions/$version"
    }

    private fun getSpecificBuildUrl(folia: Boolean, version: String, build: Int): String {
        return "https://api.papermc.io/v2/projects/${getProject(folia)}/versions/$version/builds/$build/downloads/${
            getProject(
                folia
            )
        }-$version-$build.jar"
    }

    private fun getProject(folia: Boolean, capitalized: Boolean = false): String {
        return if (capitalized) {
            if (folia) {
                "Folia"
            } else {
                "Paper"
            }
        } else {
            if (folia) {
                "folia"
            } else {
                "paper"
            }
        }
    }

}