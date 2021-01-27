import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.4.21"
	application
}

repositories {
	mavenCentral()
}

group = "io.github.ytg1234"
version = "1.0"

dependencies {
	implementation(kotlin("stdlib"))
	implementation("com.google.code.gson", "gson", "2.8.6")
}

tasks.withType<KotlinCompile> {
	kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Wrapper> {
	distributionType = Wrapper.DistributionType.ALL
	gradleVersion = "6.8.1"
}

application {
    mainClass.set("io.github.ytg1234.fabman.FabmanKt")
}
