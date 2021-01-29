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
import io.github.ytg1234.fabman.util.computePackage
import io.github.ytg1234.fabman.util.removeDependency
import io.github.ytg1234.fabman.util.setupBuildscript
import io.github.ytg1234.fabman.util.setupLocalConfig
import kotlinx.coroutines.runBlocking

object UninstallCommand :
    CliktCommand(help = "Uninstalls a package.", name = "uninstall", printHelpOnEmptyArgs = true) {
    private val config by requireObject<MutableMap<Unit, CommandConfig>>()

    private val pkg by argument(name = "package", help = "The slug of the package to uninstall")

    override fun run() = runBlocking {
        val localConf = setupLocalConfig()
        setupBuildscript(localConf.dsl, config[Unit]!!.verbose)

        removeDependency(computePackage(pkg, config[Unit]!!.globalConfig), config[Unit]!!.verbose)
    }
}

object UninstallManualCommand : CliktCommand(help = "Manually uninstalls a package.", name = "uninstallManual", printHelpOnEmptyArgs = true) {
    private val config by requireObject<MutableMap<Unit, CommandConfig>>()

    private val groupId by option("-g", "--group-id", "--group", help = "The Maven group ID of the package").required()
    private val artifactId by option("-a", "--artifact-id", "--artifact", help = "The Maven artifact ID of the package").required()
    private val configurations by argument(name = "configurations", help = "The Gradle configurations to remove").multiple(true).unique().optional()

    override fun run() {
        val localConf = setupLocalConfig()
        setupBuildscript(localConf.dsl, config[Unit]!!.verbose)

        removeDependency(FabmanPackage(group = groupId, artifact = artifactId, configurations = configurations ?: setOf("modImplementation")), config[Unit]!!.verbose)
    }
}
