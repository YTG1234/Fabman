package io.github.ytg1234.fabman.util

import io.github.ytg1234.fabman.client
import io.github.ytg1234.fabman.dataspec.FabmanPackage
import io.github.ytg1234.fabman.dataspec.PackageStorage
import io.github.ytg1234.fabman.jsonFormat
import io.github.ytg1234.fabman.logger
import io.ktor.client.request.get
import kotlinx.serialization.json.Json
import mu.KLogger

suspend fun computePackage(id: String, config: PackageStorage): FabmanPackage {
    return config.packages[id] ?: computePackage(id)
}

suspend fun computePackage(id: String): FabmanPackage {
    logger.info("Fetching ${String.format(Constants.packageUrlFormat, id)}")
    return jsonFormat.decodeFromString(FabmanPackage.serializer(), client.get(String.format(Constants.packageUrlFormat, id)))
}

fun debugOrInfo(logger: KLogger, message: String, debug: Boolean) =
    if (debug) logger.info(message) else logger.debug(message)
