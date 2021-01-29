package io.github.ytg1234.fabman.cli.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.arguments.unique
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import io.github.ytg1234.fabman.dataspec.FabmanPackage
import io.github.ytg1234.fabman.jsonFormat

object SubmitCommand :
    CliktCommand(help = "Submit a package to the official repository", name = "submit") {

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

    override fun run() {
        echo(
            """
            Here's what you need to do:
            1. Fork the GitHub repository https://github.com/YTG1234/fabman-repository.
            2. Create a file 'packages/$slug.json' and add in the Json.
            3. Fortunately we already generated the Json for you, you can copy it from below:
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
