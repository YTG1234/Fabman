package io.github.ytg1234.fabman.dataspec

import kotlinx.serialization.Serializable

@Serializable
data class PackageStorage(val packages: Map<String, FabmanPackageImpl>)
