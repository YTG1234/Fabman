package io.github.ytg1234.fabman.util

import com.google.gson.Gson
import io.github.ytg1234.fabman.Dsl
import io.github.ytg1234.fabman.config.FabmanConfig
import io.github.ytg1234.fabman.dataspec.PackageStorage

fun setupLocalConfig(gson: Gson): FabmanConfig {
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
        Constants.fabmanLocalConfigPath.toFile().writeText(gson.toJson(
            FabmanConfig(
                dsl,
                multiProject,
                applyToRoot
            )
        ))
    }

    return gson.fromJson(Constants.fabmanLocalConfigPath.toFile().readText(), FabmanConfig::class.java)
}

fun setupGlobalConfig(gson: Gson): PackageStorage {
    if (!Constants.fabmanGlobalConfigPath.toFile().exists()) {
        Constants.fabmanGlobalConfigPath.toFile().parentFile.mkdirs()
        Constants.fabmanGlobalConfigPath.toFile().createNewFile()
        Constants.fabmanGlobalConfigPath.toFile().writeText(gson.toJson(PackageStorage(mapOf())))
    }

    return gson.fromJson(Constants.fabmanGlobalConfigPath.toFile().readText(), PackageStorage::class.java)
}

fun saveConfig(gson: Gson, config: PackageStorage) {
    Constants.fabmanGlobalConfigPath.toFile().writeText(gson.toJson(config))
}

fun saveConfig(gson: Gson, config: FabmanConfig) {
    Constants.fabmanLocalConfigPath.toFile().writeText(gson.toJson(config))
}
