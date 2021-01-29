package io.github.ytg1234.fabman.config

import kotlinx.serialization.Serializable

@Serializable
data class FabmanConfig(val dsl: Dsl, val isMultiProject: Boolean, val isApplyToRoot: Boolean)
