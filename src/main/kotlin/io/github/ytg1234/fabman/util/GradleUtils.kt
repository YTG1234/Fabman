package io.github.ytg1234.fabman.util

import io.github.ytg1234.fabman.Dsl
import io.github.ytg1234.fabman.dataspec.FabmanPackage
import java.io.File

fun setupBuildscript(dsl: Dsl) {
    val projectBuildscript = when (dsl) {
        Dsl.GROOVY -> File("build.gradle")
        Dsl.KOTLIN -> File("build.gradle.kts")
    }

    val fabmanBuildscript = File(Constants.fabmanBuildscriptPath)
    if (!fabmanBuildscript.exists()) {
        fabmanBuildscript.createNewFile()
        fabmanBuildscript.writeText(Constants.fabmanBuildScriptDefaultText)
    }

    if (!projectBuildscript.readText().contains(Constants.fabmanBuildscriptPath)) {
        when (dsl) {
            Dsl.GROOVY -> projectBuildscript.appendText("\napply from: '${Constants.fabmanBuildscriptPath}'\n")
            Dsl.KOTLIN -> projectBuildscript.appendText("\n" + """apply(from = "${Constants.fabmanBuildscriptPath}")""" + "\n")
        }
    }
}

fun addDependency(pkg: FabmanPackage, version: String) {
    val fabmanBuildscript = File(Constants.fabmanBuildscriptPath)
    var txt = fabmanBuildscript.readText()

    // Add Repo
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

    txt = fabmanBuildscript.readText()

    // Add Dependency
    if (txt.contains(regexForConfigs(pkg))) txt = regexForConfigs(pkg).replace(txt, "")
    fabmanBuildscript.writeText(
        txt.replaceFirst(
            "    " + Constants.buildScriptDependencyMarker,
            pkg.configurations.joinToString("\n") {
                """
                |    configurations.getByName("$it")("${pkg.group}", "${pkg.artifact}", "$version")
                """.trimMargin("|")
            } + "\n    ${Constants.buildScriptDependencyMarker}"
        )
    )
    println("Successfully added ${pkg.group}:${pkg.artifact}:$version")
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

fun removeDependency(pkg: FabmanPackage) {
    val fabmanBuildscript = File(Constants.fabmanBuildscriptPath)
    val txt = fabmanBuildscript.readText()
    val regexConfiguration = regexForConfigs(pkg)

    fabmanBuildscript.writeText(regexConfiguration.replace(txt, ""))

    println("Successfully removed ${pkg.group}:${pkg.artifact}:UNKNOWN_VERSION")
}
