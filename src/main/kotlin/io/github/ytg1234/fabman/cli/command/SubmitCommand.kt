package io.github.ytg1234.fabman.cli.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.arguments.unique
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import io.github.ytg1234.fabman.cli.CommandConfig
import io.github.ytg1234.fabman.client
import io.github.ytg1234.fabman.dataspec.FabmanPackage
import io.github.ytg1234.fabman.jsonFormat
import io.github.ytg1234.fabman.logger
import io.github.ytg1234.fabman.util.Constants
import io.github.ytg1234.fabman.util.GithubUtils
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import java.util.Base64
import java.util.UUID

object SubmitCommand :
    CliktCommand(help = "Submit a package to the official repository", name = "submit") {

    val config by requireObject<MutableMap<Unit, CommandConfig>>()

    private val token by option(
        "-t",
        "--token",
        "--github-token",
        help = "The GitHub token that will be used for creating a PR. If not specified, will print instructions instead."
    )

    private val org by option("-o", "--organization", help = "The GitHub organization to fork to, if forking.")

    private val slug by option(
        "-s",
        "--slug",
        "--package-slug",
        help = "The slug that will be used for installing the package"
    ).required()

    private val groupId by option("-g", "--group-id", "--group", help = "The Maven group ID of the package").required()

    private val artifactId by option(
        "-a",
        "--artifact-id",
        "--artifact",
        help = "The Maven artifact ID of the package"
    ).required()

    private val mavenUrl by option(
        "-r",
        "--repo",
        help = "The Maven repository that will be used when importing with Gradle"
    ).required()

    private val configurations by argument(
        name = "configurations",
        help = "The Gradle configurations that will be used when importing with Gradle"
    ).multiple(true).unique()

    override fun run() = runBlocking {
        if (token != null) {
            println("Permissions required for this token: ${Constants.submitPerms.joinToString(", ")}. Make sure you have all of those enabled before proceeding. Press ^C to cancel.")
            val ghUtils = GithubUtils(token!!, client, jsonFormat, verbose = config[Unit]!!.verbose)

            val branchUuid = UUID.randomUUID().toString()
            val forkedRepo =
                (ghUtils.fork(Constants.repoAuthor, Constants.repoName, org)["url"]?.jsonPrimitive?.contentOrNull ?: run {
                    echo("Could not fork the repository.", err = true)
                    return@runBlocking
                }).replace("${Constants.defaultGithubApiHost}/repos/", "")

            ghUtils.createBranch(forkedRepo, branchUuid)
            ghUtils.createOrModifyFile(
                forkedRepo, branchUuid, "packages/$slug.json", Base64.getEncoder().encodeToString(
                    jsonFormat.encodeToString(
                        FabmanPackage.serializer(),
                        FabmanPackage(
                            slug,
                            mavenUrl,
                            groupId,
                            artifactId,
                            configurations.toList()
                        )
                    ).toByteArray()
                ), "Created $slug"
            )

            val prTitle = run {
                print("Enter the pull request title (default: Create $slug) > ")
                val rl = readLine()!!
                if (rl == "") "Create $slug" else rl
            }
            val prBody = run {
                println("Enter the pull request body (press ^D when you're done):")
                val rl = generateSequence(::readLine).joinToString("\n")
                if (rl == "") {
                    echo("Please specify a body.", err = true)
                    throw RuntimeException()
                } else rl
            }
            val pr = ghUtils.createPr(
                "YTG1234",
                "fabman-repository",
                forkedRepo.split("/")[0],
                "main",
                branchUuid,
                prTitle,
                prBody
            )

            logger.info("No errors? Great! The PR was created successfully! You can see it at ${pr["html_url"]!!.jsonPrimitive.content}")
        } else echo(
            """
            Here's what you need to do:
            1. Fork the GitHub repository ${Constants.repoUrl}.
            2. Create a file 'packages/$slug.json' and add in the Json. Then create a pull request and fill in all the necessary text fields.
            Fortunately we already generated the Json for you, you can copy it from below:
            """.trimIndent() + "\n" + jsonFormat.encodeToString(
                FabmanPackage.serializer(),
                FabmanPackage(
                    slug,
                    mavenUrl,
                    groupId,
                    artifactId,
                    configurations.toList()
                )
            )
        )
    }
}
