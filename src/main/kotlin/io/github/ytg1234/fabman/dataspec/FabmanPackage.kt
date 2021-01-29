package io.github.ytg1234.fabman.dataspec

import kotlinx.serialization.Serializable

@Serializable
data class FabmanPackage(
    val name: String?,
    val mavenUrl: String,
    val group: String,
    val artifact: String,
    val configurations: List<String>
) {
    override fun toString(): String {
        return "Package slug is $name, Gradle ID is $group:$artifact:<version>, and required maven repo is $mavenUrl"
    }
}
