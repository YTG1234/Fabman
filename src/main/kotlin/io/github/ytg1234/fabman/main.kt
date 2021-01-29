package io.github.ytg1234.fabman

import io.github.ytg1234.fabman.cli.FabmanCommand
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import mu.KotlinLogging

val logger = KotlinLogging.logger("Fab-Man")
val client = HttpClient(CIO)

@OptIn(ExperimentalSerializationApi::class)
val jsonFormat = Json {
    prettyPrint = true
    prettyPrintIndent = "  "
}

fun main(args: Array<String>) {
    FabmanCommand.main(args)
    client.close()
}
