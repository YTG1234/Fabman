package io.github.ytg1234.fabman.util

import io.github.ytg1234.fabman.config.Dsl
import io.github.ytg1234.fabman.config.FabmanConfig
import io.github.ytg1234.fabman.dataspec.PackageStorage
import kotlinx.serialization.json.Json

fun setupLocalConfig(): FabmanConfig {
    if (!Constants.fabmanLocalConfigPath.toFile().exists()) {
        println("Hello! Looks like this is the first time you ran Fab-Man in this project, let's get you set up!")

        print("Which language does your Gradle buildscript use? (Groovy/Kotlin, default: Groovy) > ")
        val dsl = Dsl.valueOf(run {
            val tmpReadline = readLine() ?: ""
            if (tmpReadline == "") "Groovy" else tmpReadline
        }.toUpperCase())

        print("Is this a multiproject build? (true/false, default: false) > ")
        val multiProject = run {
            val tmpReadline = readLine() ?: ""
            if (tmpReadline == "") "false" else tmpReadline
        }.toBoolean()

        print("If this is a multiproject build, is the root project a mod or only the subprojects? (true/false, default: true) > ")
        val applyToRoot = run {
            val tmpReadline = readLine() ?: ""
            if (tmpReadline == "") "true" else tmpReadline
        }.toBoolean()

        Constants.fabmanLocalConfigPath.toFile().createNewFile()
        Constants.fabmanLocalConfigPath.toFile().writeText(
            Json.encodeToString(
                FabmanConfig.serializer(), FabmanConfig(
                    dsl,
                    multiProject,
                    applyToRoot
                )
            )
        )
    }

    return Json.decodeFromString(FabmanConfig.serializer(), Constants.fabmanLocalConfigPath.toFile().readText())
}

fun setupGlobalConfig(): PackageStorage {
    if (!Constants.fabmanGlobalConfigPath.toFile().exists()) {
        Constants.fabmanGlobalConfigPath.toFile().parentFile.mkdirs()
        Constants.fabmanGlobalConfigPath.toFile().createNewFile()
        Constants.fabmanGlobalConfigPath.toFile().writeText(Json.encodeToString(PackageStorage.serializer(), PackageStorage(mapOf())))
    }

    return Json.decodeFromString(PackageStorage.serializer(), Constants.fabmanGlobalConfigPath.toFile().readText())
}

fun saveConfig(config: PackageStorage) {
    Constants.fabmanGlobalConfigPath.toFile().writeText(Json.encodeToString(PackageStorage.serializer(), config))
}

fun saveConfig(config: FabmanConfig) {
    Constants.fabmanLocalConfigPath.toFile().writeText(Json.encodeToString(FabmanConfig.serializer(), config))
}
