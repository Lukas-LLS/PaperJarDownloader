package lls.pjd.downloader

import lls.pjd.util.KtorUtil

@Deprecated("Only supports up to version 1.21.11", replaceWith = ReplaceWith("GraphQLDownloader"))
object RestDownloader : Downloader {

    override suspend fun getSpecificBuild(folia: Boolean, version: String, build: Int): ByteArray {
        return KtorUtil.getBytes(getSpecificBuildUrl(folia, version, build))
    }

    override suspend fun getVersions(folia: Boolean): List<String> {
        return parseJSONArrayAttribute(KtorUtil.getBodyAsText(getVersionsUrl(folia)), "versions")
    }

    override suspend fun getBuilds(folia: Boolean, version: String): List<Int> {
        return parseJSONArrayAttribute(
            KtorUtil.getBodyAsText(getBuildsUrl(folia, version)),
            "builds"
        ).mapNotNull { it.toIntOrNull() }
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
        return "https://api.papermc.io/v2/projects/" + Downloader.getProject(folia)
    }

    private fun getBuildsUrl(folia: Boolean, version: String): String {
        return "https://api.papermc.io/v2/projects/${Downloader.getProject(folia)}/versions/$version"
    }

    private fun getSpecificBuildUrl(folia: Boolean, version: String, build: Int): String {
        return "https://api.papermc.io/v2/projects/${Downloader.getProject(folia)}/versions/$version/builds/$build/downloads/${
            Downloader.getProject(
                folia
            )
        }-$version-$build.jar"
    }

}