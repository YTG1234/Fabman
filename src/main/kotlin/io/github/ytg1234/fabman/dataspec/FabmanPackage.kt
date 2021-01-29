package io.github.ytg1234.fabman.dataspec

import kotlinx.serialization.Serializable

@Serializable
data class FabmanPackageImpl(
    override val name: String = "",
    override val mavenUrl: String = "",
    override val group: String,
    override val artifact: String,
    override val configurations: List<String>
) : FabmanPackage

interface FabmanPackage {
    val name: String?
    val mavenUrl: String
    val group: String
    val artifact: String
    val configurations: List<String>

    companion object {
        operator fun invoke(
            name: String = "",
            mavenUrl: String = "",
            group: String,
            artifact: String,
            configurations: List<String>
        ) = FabmanPackageImpl(name, mavenUrl, group, artifact, configurations)

        fun serializer() = FabmanPackageImpl.serializer()
    }
}
