package io.github.ytg1234.fabman.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.findOrSetObject
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import io.github.ytg1234.fabman.cli.command.InstallCommand
import io.github.ytg1234.fabman.cli.command.InstallManualCommand
import io.github.ytg1234.fabman.cli.command.SetupCommand
import io.github.ytg1234.fabman.cli.command.SubmitCommand
import io.github.ytg1234.fabman.cli.command.UninstallCommand
import io.github.ytg1234.fabman.cli.command.UninstallManualCommand
import io.github.ytg1234.fabman.util.setupGlobalConfig

object FabmanCommand :
    CliktCommand(help = "The main Fab-Man command", invokeWithoutSubcommand = false, name = "fabman") {
    private val verbose by option("-v", "--verbose", help = "Show verbose output").flag("-V", "--non-verbose", default = false)

    private val config by findOrSetObject { mutableMapOf<Unit, CommandConfig>() }
    override fun run() {
        config[Unit] = CommandConfig(verbose, setupGlobalConfig())
    }

    init {
        subcommands(SetupCommand, InstallCommand, InstallManualCommand, UninstallCommand, UninstallManualCommand, SubmitCommand)
    }
}
