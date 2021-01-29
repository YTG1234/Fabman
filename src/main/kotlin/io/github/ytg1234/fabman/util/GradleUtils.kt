package io.github.ytg1234.fabman.util

import io.github.ytg1234.fabman.config.Dsl
import io.github.ytg1234.fabman.dataspec.FabmanPackage
import io.github.ytg1234.fabman.logger
import java.io.File

fun setupBuildscript(dsl: Dsl, verbose: Boolean) {
    debugOrInfo(logger, "Setting up Fab-Man buildscript", verbose)
    val projectBuildscript = when (dsl) {
        Dsl.GROOVY -> File("build.gradle")
        Dsl.KOTLIN -> File("build.gradle.kts")
    }

    val fabmanBuildscript = File(Constants.fabmanBuildscriptPath)
    if (!fabmanBuildscript.exists()) {
        fabmanBuildscript.createNewFile()
        fabmanBuildscript.writeText(Constants.fabmanBuildScriptDefaultText)
    }

    debugOrInfo(logger, "Adding apply from to the main buildscript", verbose)
    if (!projectBuildscript.readText().contains(Constants.fabmanBuildscriptPath)) {
        when (dsl) {
            Dsl.GROOVY -> projectBuildscript.appendText("\napply from: '${Constants.fabmanBuildscriptPath}'\n")
            Dsl.KOTLIN -> projectBuildscript.appendText("\n" + """apply(from = "${Constants.fabmanBuildscriptPath}")""" + "\n")
        }
    }
}

fun addDependency(pkg: InstallablePackage, verbose: Boolean) {
    val fabmanBuildscript = File(Constants.fabmanBuildscriptPath)
    var txt = fabmanBuildscript.readText()

    // Add Repo
    debugOrInfo(logger, "Adding Maven repo ${pkg.mavenUrl}", verbose)
    if (!txt.contains("""maven(url = "${pkg.mavenUrl}")""")) {
        fabmanBuildscript.writeText(
            txt.replaceFirst(
                "    " + Constants.buildScriptRepositoryMarker, """
                |    maven(url = "${pkg.mavenUrl}")
                |    ${Constants.buildScriptRepositoryMarker}
                """.trimMargin("|")
            )
        )
    }
    debugOrInfo(logger, "Added Repo ${pkg.mavenUrl}", verbose)

    txt = fabmanBuildscript.readText()

    // Add Dependency
    debugOrInfo(logger, "Adding package", verbose)
    if (txt.contains(regexForConfigs(pkg))) txt = regexForConfigs(pkg).replace(txt, "")
    fabmanBuildscript.writeText(
        txt.replaceFirst(
            "    " + Constants.buildScriptDependencyMarker,
            pkg.configurations.joinToString("\n") {
                debugOrInfo(logger, "Adding for configuration $it", verbose)

                """
                |    configurations.getByName("$it")("${pkg.group}", "${pkg.artifact}", "${pkg.version}")
                """.trimMargin("|")
            } + "\n    ${Constants.buildScriptDependencyMarker}"
        )
    )
    logger.info("Successfully added ${pkg.group}:${pkg.artifact}:${pkg.version}")
}

fun regexForConfigs(pkg: FabmanPackage): Regex =
    Regex(
        pkg.configurations.joinToString("|") {
            """\s+configurations\.getByName\("$it"\)\("${
                pkg.group.replace(
                    ".",
                    "\\."
                )
            }", "${pkg.artifact.replace(".", "\\.")}", "(.+)"\)"""
        }
    )

fun removeDependency(pkg: FabmanPackage, verbose: Boolean) {
    val fabmanBuildscript = File(Constants.fabmanBuildscriptPath)
    val txt = fabmanBuildscript.readText()
    val regexConfiguration = regexForConfigs(pkg)

    debugOrInfo(logger, "Removing package", verbose)
    fabmanBuildscript.writeText(regexConfiguration.replace(txt, ""))

    logger.info("Successfully removed ${pkg.group}:${pkg.artifact}:UNKNOWN_VERSION")
}
