package io.github.ytg1234.fabman.cli.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import io.github.ytg1234.fabman.cli.CommandConfig
import io.github.ytg1234.fabman.util.setupBuildscript
import io.github.ytg1234.fabman.util.setupLocalConfig

object SetupCommand : CliktCommand(help = "Set up the Fab-Man buildscript", name = "setup") {
    val config by requireObject<MutableMap<Unit, CommandConfig>>()

    override fun run() {
        val localConf = setupLocalConfig()
        setupBuildscript(localConf.dsl, config[Unit]!!.verbose)
    }
}
