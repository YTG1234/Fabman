package io.github.ytg1234.fabman.util

import io.github.ytg1234.fabman.dataspec.FabmanPackage

class InstallablePackage(val fabmanPackage: FabmanPackage, val version: String) : FabmanPackage by fabmanPackage
