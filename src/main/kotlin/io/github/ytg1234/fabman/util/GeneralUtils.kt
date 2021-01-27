package io.github.ytg1234.fabman.util

import io.github.ytg1234.fabman.dataspec.PackageStorage
import io.github.ytg1234.fabman.dataspec.FabmanPackage
import kotlin.system.exitProcess

fun computePackage(id: String, config: PackageStorage): FabmanPackage = config.packages[id] ?: throw IllegalArgumentException("Package $id not found!")

fun printHelpGuide() {
    println(Constants.helpGuide)
}

fun printCmdlineErrorAndExit() {
    System.err.println(Constants.helpGuide)
    exitProcess(-1)
}
