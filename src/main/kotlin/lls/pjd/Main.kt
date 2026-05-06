package lls.pjd

import kotlinx.coroutines.runBlocking
import lls.pjd.downloader.Downloader
import lls.pjd.util.KtorUtil
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

        val downloader = Downloader.selectDownloader()

        if (version == "latest") {
            val versions: List<String>
            runBlocking {
                versions = downloader.getVersions(folia)
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
            latestBuild = downloader.getBuilds(folia, version).maxOfOrNull { it }
        }

        latestBuild ?: run {
            logger.error("Could not get latest build for version $version (${Downloader.getProject(folia, true)})")
            KtorUtil.close()
            return
        }

        logger.info("Checking if current version is up to date")

        val serverDir = File("server")

        val currentVersion = Downloader.checkForCurrentVersion(serverDir, logger)

        if (currentVersion != null) {
            if (currentVersion.second == version && currentVersion.first == latestBuild) {
                logger.info(
                    "Current version is up to date (${
                        Downloader.getProject(
                            folia,
                            true
                        )
                    } - MC ${currentVersion.second} - build ${currentVersion.first})"
                )
                KtorUtil.close()
                return
            } else {
                logger.info(
                    "Current version is not up to date (${
                        Downloader.getProject(
                            folia,
                            true
                        )
                    } - MC ${currentVersion.second} - build ${currentVersion.first})"
                )
            }
        } else {
            logger.info("Could not check if current version is up to date")
        }

        logger.info("Downloading ${Downloader.getProject(folia, true)} version $version build $latestBuild")

        val latestBuildData: ByteArray

        runBlocking {
            latestBuildData = downloader.getSpecificBuild(folia, version, latestBuild)
        }

        if (latestBuildData.isEmpty()) {
            logger.error("Could not download build")
            KtorUtil.close()
            return
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

        KtorUtil.close()

        logger.info("Done")
    }

}