package lls.pjd.downloader

import com.apollographql.apollo.ApolloClient
import lls.pjd.generated.GetBuildUrlQuery
import lls.pjd.generated.GetBuildsQuery
import lls.pjd.generated.GetVersionsQuery
import lls.pjd.util.KtorUtil

object GraphQLDownloader : Downloader {

    private val client = ApolloClient
        .Builder()
        .serverUrl("https://fill.papermc.io/graphql")
        .addHttpHeader(
            "User-Agent",
            "PaperJarDownloader/3.0 (https://github.com/Lukas-LLS/PaperJarDownloader)"
        )
        .build()

    override suspend fun getSpecificBuild(
        folia: Boolean,
        version: String,
        build: Int
    ): ByteArray {
        val response = client.query(GetBuildUrlQuery(Downloader.getProject(folia), version, build)).execute()

        if (!response.errors.isNullOrEmpty()) {
            throw IllegalStateException("GraphQL returned errors while fetching build URL: ${response.errors}")
        }

        return response
            .data
            ?.project
            ?.version
            ?.build
            ?.downloads
            .orEmpty()
            .firstNotNullOf { it.url }
            .let { KtorUtil.getBytes(it) }
    }

    override suspend fun getVersions(folia: Boolean): List<String> {
        val response = client.query(GetVersionsQuery(Downloader.getProject(folia))).execute()

        if (!response.errors.isNullOrEmpty()) {
            throw IllegalStateException("GraphQL returned errors while fetching versions: ${response.errors}")
        }

        return response
            .data
            ?.project
            ?.versions
            ?.nodes
            .orEmpty()
            .mapNotNull { it?.key }
    }

    override suspend fun getBuilds(folia: Boolean, version: String): List<Int> {
        val response = client.query(GetBuildsQuery(Downloader.getProject(folia), version)).execute()

        if (!response.errors.isNullOrEmpty()) {
            throw IllegalStateException("GraphQL returned errors while fetching builds: ${response.errors}")
        }

        return response
            .data
            ?.project
            ?.version
            ?.builds
            ?.nodes
            .orEmpty()
            .mapNotNull { it?.number }
    }

}