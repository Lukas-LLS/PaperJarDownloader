package lls.pjd.downloader

import org.slf4j.Logger
import java.io.File

interface Downloader {

    suspend fun getSpecificBuild(folia: Boolean, version: String, build: Int): ByteArray

    suspend fun getVersions(folia: Boolean): List<String>

    suspend fun getBuilds(folia: Boolean, version: String): List<Int>

    companion object {
        fun selectDownloader(): Downloader {
            System.getenv("LEGACY_DOWNLOADER")?.let {
                return if (it.toBoolean()) {
                    @Suppress("DEPRECATION")
                    RestDownloader
                } else {
                    GraphQLDownloader
                }
            }
            return GraphQLDownloader
        }

        fun getProject(folia: Boolean, capitalized: Boolean = false): String {
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

        fun checkForCurrentVersion(serverDir: File, logger: Logger): Pair<Int, String>? {
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
    }

}