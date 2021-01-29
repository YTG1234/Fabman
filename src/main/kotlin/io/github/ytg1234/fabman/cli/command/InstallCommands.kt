package io.github.ytg1234.fabman.cli.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.arguments.unique
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import io.github.ytg1234.fabman.cli.CommandConfig
import io.github.ytg1234.fabman.dataspec.FabmanPackage
import io.github.ytg1234.fabman.util.InstallablePackage
import io.github.ytg1234.fabman.util.addDependency
import io.github.ytg1234.fabman.util.computePackage
import io.github.ytg1234.fabman.util.setupBuildscript
import io.github.ytg1234.fabman.util.setupLocalConfig
import kotlinx.coroutines.runBlocking

object InstallCommand : CliktCommand(help = "Installs a package.", name = "install", printHelpOnEmptyArgs = true) {
    private val config by requireObject<MutableMap<Unit, CommandConfig>>()

    private val pkg by argument(name = "package", help = "The package to install, in the format of slug@version")

    override fun run() = runBlocking {
        val localConf = setupLocalConfig()
        setupBuildscript(localConf.dsl, config[Unit]!!.verbose)

        addDependency(
            InstallablePackage(
                computePackage(pkg.split("@")[0], config[Unit]!!.globalConfig),
                pkg.split("@")[1]
            ),
            config[Unit]!!.verbose
        )
    }
}

object InstallManualCommand :
    CliktCommand(help = "Manually installs a package.", name = "installManual", printHelpOnEmptyArgs = true) {
    private val config by requireObject<MutableMap<Unit, CommandConfig>>()

    private val groupId by option("-g", "--group-id", "--group", help = "The Maven group ID of the package").required()
    private val artifactId by option("-a", "--artifact-id", "--artifact", help = "The Maven artifact ID of the package").required()
    private val version by option("-v", "--version", help = "The package version to install").required()
    private val mavenUrl by option(
        "-r",
        "--repo",
        help = "The Maven repository required to install the package"
    ).required()
    private val configurations by argument(
        name = "configurations",
        help = "The Gradle configuration to use when importing the package"
    ).multiple(true).unique().optional()

    override fun run() {
        val localConf = setupLocalConfig()
        setupBuildscript(localConf.dsl, config[Unit]!!.verbose)

        addDependency(
            InstallablePackage(
                FabmanPackage(mavenUrl = mavenUrl, group = groupId, artifact = artifactId, configurations = configurations ?: setOf("modImplementation")),
                version
            ),
            config[Unit]!!.verbose
        )
    }
}
