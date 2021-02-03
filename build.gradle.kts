import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	val ktVersion = "1.4.21"

	kotlin("jvm") version ktVersion
	kotlin("plugin.serialization") version ktVersion
	application
}

repositories {
	maven(url = "https://kotlin.bintray.com/kotlinx")
	maven(url = "https://repo.gradle.org/gradle/repo/")
}

group = "io.github.ytg1234"
version = "0.1.0"

dependencies {
	implementation(kotlin("stdlib"))
	runtimeOnly(kotlin("reflect"))

	// Json
	implementation("org.jetbrains.kotlinx", "kotlinx-serialization-json", "1.0.1")

	// CLI
	implementation("com.github.ajalt.clikt", "clikt", "3.1.0")

	// Logging
	implementation("io.github.microutils", "kotlin-logging-jvm", "2.0.4")
	runtimeOnly("org.slf4j", "slf4j-simple", "1.7.9")

	// HTTP
	implementation("io.ktor", "ktor-client-cio", "1.5.1")
}

tasks.withType<KotlinCompile> {
	kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Wrapper> {
	distributionType = Wrapper.DistributionType.ALL
	gradleVersion = "6.8.1"
}

application {
    mainClass.set("io.github.ytg1234.fabman.MainKt")
}
