package io.github.ytg1234.fabman.util

import io.github.ytg1234.fabman.logger
import io.ktor.client.HttpClient
import io.ktor.client.features.ClientRequestException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.util.Base64

class GithubUtils(
    val token: String,
    val client: HttpClient,
    val json: Json,
    val api: String = "https://api.github.com",
    val verbose: Boolean
) {
    suspend fun fork(author: String, repo: String, org: String? = null) = fork("$author/$repo", org)

    suspend fun fork(repo: String, org: String? = null): JsonObject {
        debugOrInfo(logger, "Forking repository $repo...", verbose)
        return json.decodeFromString(
            client.post("$api/repos/$repo/forks") {
                addHeaders()
                if (org != null) body = JsonObject(mapOf("organization" to org.json)).toString()
            }
        )
    }

    suspend fun createBranch(repo: String, branchName: String): JsonObject {
        debugOrInfo(logger, "Trying to create branch $branchName in repository $repo", verbose)

        val info: JsonArray =
            json.decodeFromString(client.get("$api/repos/$repo/git/refs/heads", addHeaders))
        val sha = info[0].jsonObject["object"]?.jsonObject?.get("sha")?.jsonPrimitive?.contentOrNull
            ?: throw IllegalArgumentException("Could not get sha")

        return json.decodeFromString(
            client.post("$api/repos/$repo/git/refs") {
                addHeaders()
                body = JsonObject(mapOf("sha" to sha.json, "ref" to "refs/heads/$branchName".json)).toString()
            }
        )
    }

    suspend fun createBranch(author: String, repo: String, branchName: String) =
        createBranch("$author/$repo", branchName)

    suspend fun createOrModifyFile(
        author: String,
        repo: String,
        branch: String,
        path: String,
        content: String,
        message: String = "Created $path"
    ) = createOrModifyFile("$author/$repo", branch, path, content, message)

    suspend fun createOrModifyFile(
        repo: String,
        branch: String,
        path: String,
        content: String,
        message: String = "Created $path"
    ): JsonObject {
        debugOrInfo(logger, "Creating file $path in $repo@$branch", verbose)
        val sha: String? = try {
            debugOrInfo(logger, "Trying to get existing file $path in $repo@$branch", verbose)
            val resp = client.get<HttpResponse>("$api/repos/$repo/contents/$path") {
                addHeaders()
                body = JsonObject(mapOf("ref" to branch.json)).toString()
            }

            resp.run {
                if (status != HttpStatusCode.NotFound) {
                    val obj: JsonObject = json.decodeFromString(readText())
                    obj["sha"]?.jsonPrimitive?.content
                        ?: throw IllegalArgumentException("Could not find sha of existing file!")
                } else null
            }
        } catch (e: ClientRequestException) {
            if (!e.message!!.contains("invalid: 404")) {
                throw e
            } else {
                debugOrInfo(logger, "File didn't exist already, not using hash", verbose)
                null
            }
        }

        debugOrInfo(logger, "Actually creating file $path in $repo@$branch", verbose)
        return json.decodeFromString(client.put("$api/repos/$repo/contents/$path") {
            addHeaders()
            val interBody = JsonObject(
                mapOf(
                    "message" to message.json,
                    "branch" to branch.json,
                    "content" to Base64.getEncoder().encodeToString(content.toByteArray()).json
                )
            )
            body = interBody.toString()
            if (sha != null) {
                val map = interBody.toMutableMap()
                map["sha"] = sha.json
                body = JsonObject(map).toString()
            }
        })
    }


    suspend fun createPr(
        author: String,
        repo: String,
        forkAuthor: String = author,
        base: String = "main",
        head: String,
        title: String,
        contents: String
    ) = createPr("$author/$repo", forkAuthor, base, head, title, contents)

    suspend fun createPr(
        repo: String,
        forkAuthor: String = repo.split("/")[0],
        base: String = "main",
        head: String,
        title: String,
        contents: String
    ): JsonObject {
        debugOrInfo(logger, "Creating a pull request in $repo@$base from $forkAuthor:$head", verbose)
        return json.decodeFromString(
            client.post("$api/repos/$repo/pulls") {
                addHeaders()

                body = JsonObject(
                    mapOf(
                        "title" to title.json,
                        "head" to "${if (forkAuthor != repo.split("/")[0]) "$forkAuthor:" else ""}$head".json,
                        "base" to base.json,
                        "body" to contents.json,
                        "maintainer_can_modify" to true.json
                    )
                ).toString()
            }
        )
    }

    private val String.json: JsonPrimitive
        get() = JsonPrimitive(this)
    private val Boolean.json: JsonPrimitive
        get() = JsonPrimitive(this)

    val addHeaders: HttpRequestBuilder.() -> Unit = {
        header("Authorization", "token $token")
        header("Accept", "application/vnd.github.v3+json")
    }
}
