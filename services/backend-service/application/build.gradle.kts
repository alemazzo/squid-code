import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage

plugins {
	id("org.jetbrains.kotlin.jvm") version "1.9.22"
	id("org.jetbrains.kotlin.plugin.allopen") version "1.9.22"
	id("com.google.devtools.ksp") version "1.9.22-1.0.17"
	id("com.github.johnrengelman.shadow") version "8.1.1"
	id("io.micronaut.application") version "4.5.3"
	id("io.micronaut.aot") version "4.5.3"
}

version = "0.1"
group = "io.github.alemazzo.squidcode.backend"

val kotlinVersion = project.properties.get("kotlinVersion")
repositories {
	mavenCentral()
}

dependencies {
	ksp("io.micronaut:micronaut-http-validation")
	ksp("io.micronaut.serde:micronaut-serde-processor")
	implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
	implementation("io.micronaut.serde:micronaut-serde-jackson")
	implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
	compileOnly("io.micronaut:micronaut-http-client")
	runtimeOnly("ch.qos.logback:logback-classic")
	runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")
	testImplementation("io.micronaut:micronaut-http-client")
	runtimeOnly("org.yaml:snakeyaml")
	// Jakarta
	implementation("jakarta.persistence:jakarta.persistence-api")
	implementation("jakarta.inject:jakarta.inject-api")
	// io.jsonwebtoken
	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
	implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")
	implementation("com.google.api-client:google-api-client:2.7.2") // Main library
	implementation("com.google.oauth-client:google-oauth-client:1.39.0") // OAuth
	implementation("com.google.http-client:google-http-client-jackson2:1.46.3") // Jackson factory

	// Micronaut MongoDB
	implementation("io.micronaut.data:micronaut-data-mongodb")
	implementation("io.micronaut.mongodb:micronaut-mongo-sync")
	runtimeOnly("org.mongodb:mongodb-driver-sync")
	ksp("io.micronaut.data:micronaut-data-document-processor")
}


application {
	mainClass.set("io.github.alemazzo.squidcode.backend.ApplicationKt")
}
java {
	sourceCompatibility = JavaVersion.toVersion("21")
}

graalvmNative.toolchainDetection.set(false)
micronaut {
	runtime("netty")
	testRuntime("junit5")
	processing {
		incremental(true)
		annotations("io.github.alemazzo.squidcode.backend.*")
	}
	aot {
		// Please review carefully the optimizations enabled below
		// Check https://micronaut-projects.github.io/micronaut-aot/latest/guide/ for more details
		optimizeServiceLoading.set(false)
		convertYamlToJava.set(false)
		precomputeOperations.set(true)
		cacheEnvironment.set(true)
		optimizeClassLoading.set(true)
		deduceEnvironment.set(true)
		optimizeNetty.set(true)
	}
}

tasks.named<io.micronaut.gradle.docker.MicronautDockerfile>("dockerfile") {
	baseImage("eclipse-temurin:21-jre-jammy")
}

tasks.named<io.micronaut.gradle.docker.NativeImageDockerfile>("dockerfileNative") {
	jdkVersion.set("21")
}