package io.github.ytg1234.fabman

import io.github.ytg1234.fabman.dataspec.FabmanPackage
import io.github.ytg1234.fabman.dataspec.PackageStorage
import io.github.ytg1234.fabman.util.Constants
import io.github.ytg1234.fabman.util.addDependency
import io.github.ytg1234.fabman.util.computePackage
import io.github.ytg1234.fabman.util.printCmdlineErrorAndExit
import io.github.ytg1234.fabman.util.printHelpGuide
import io.github.ytg1234.fabman.util.removeDependency
import io.github.ytg1234.fabman.util.saveConfig
import io.github.ytg1234.fabman.util.setupBuildscript
import io.github.ytg1234.fabman.util.setupGlobalConfig
import io.github.ytg1234.fabman.util.setupLocalConfig
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default

fun main(args: Array<String>) {
    val globalConfig = setupGlobalConfig()

//    val parser = ArgParser("fabman")
//    val verbose by parser.option(ArgType.Boolean, "verbose", "v", "Enable verbose output").default(false)

    if (args.isEmpty()) printCmdlineErrorAndExit()

    when (args[0]) {
        "setup" -> {
            if (Constants.fabmanLocalConfigPath.toFile().exists()) Constants.fabmanLocalConfigPath.toFile().delete()

            val config = setupLocalConfig()
            setupBuildscript(config.dsl)
        }
        "install" -> {
            val localConfig = setupLocalConfig()
            setupBuildscript(localConfig.dsl)

            if (args.size in 3..4 || args.size == 1) printCmdlineErrorAndExit()

            if (args.size <= 4) {
                addDependency(computePackage(args[1].split("@")[0], globalConfig), args[1].split("@")[1])
            } else {
                addDependency(
                    FabmanPackage(
                        null,
                        args[4],
                        args[1],
                        args[2],
                        listOf("modImplementation")
                    ),
                    args[3]
                )
            }
        }
        "uninstall" -> {
            val localConfig = setupLocalConfig()
            setupBuildscript(localConfig.dsl)

            if (args.size == 1) printCmdlineErrorAndExit()
            removeDependency(
                if (args.size == 2) computePackage(args[1], globalConfig) else FabmanPackage(
                    null,
                    "",
                    args[1],
                    args[2],
                    args.toList().subList(3, args.size)
                )
            )
        }
        "submit" -> {
            if (args.size in 1..5) printCmdlineErrorAndExit()

            val slug = args[1]
            val group = args[2]
            val artifact = args[3]
            val repo = args[4]
            val config = args.toList().subList(5, args.size)

            val newConfig = PackageStorage(
                globalConfig.packages + (slug to FabmanPackage(
                    slug,
                    repo,
                    group,
                    artifact,
                    config
                ))
            )
            saveConfig(newConfig)
            println("Added package $slug: $group:$artifact from $repo with ${config.joinToString(", ")}")
        }
        "help" -> {
            printHelpGuide()
        }
        else -> printCmdlineErrorAndExit()
    }
}
