import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	val ktVersion = "1.4.21"

	kotlin("jvm") version ktVersion
	kotlin("plugin.serialization") version ktVersion
	application
}

repositories {
	maven(url = "https://kotlin.bintray.com/kotlinx")
	jcenter()
	mavenCentral()
}

group = "io.github.ytg1234"
version = "1.0"

dependencies {
	implementation(kotlin("stdlib"))
	implementation("org.jetbrains.kotlinx", "kotlinx-serialization-json", "1.0.1")
	implementation("org.jetbrains.kotlinx", "kotlinx-cli", "0.3")
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
