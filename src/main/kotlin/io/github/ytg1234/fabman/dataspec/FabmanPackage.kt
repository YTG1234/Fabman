package io.github.ytg1234.fabman.dataspec

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable
data class FabmanPackageImpl(
    override val name: String = "",
    override val mavenUrl: String = "",
    override val group: String,
    override val artifact: String,
    override val configurations: Set<String> = setOf("modImplementation")
) : FabmanPackage {
    override fun toString(): String {
        return "Package slug is $name, Gradle ID is $group:$artifact:<version>, and required maven repo is $mavenUrl"
    }
}

interface FabmanPackage {
    val name: String?
    val mavenUrl: String
    val group: String
    val artifact: String
    val configurations: Set<String>

    companion object {
        operator fun invoke(
            name: String = "",
            mavenUrl: String = "",
            group: String,
            artifact: String,
            configurations: Set<String> = setOf("modImplementation")
        ) = FabmanPackageImpl(name, mavenUrl, group, artifact, configurations)

        fun serializer(): KSerializer<out FabmanPackage> = FabmanPackageImpl.serializer()
    }
}
