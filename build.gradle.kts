import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.github.hierynomus.license") version "0.16.1"
    id("com.gradle.plugin-publish") version "0.16.0"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
    id("com.github.ben-manes.versions") version "0.39.0"
    id("com.redpillanalytics.gradle-analytics") version "1.4.3"
    id("com.github.breadmoirai.github-release") version "2.2.12"
    id("com.adarshr.test-logger") version "3.0.0"
    id("org.jetbrains.dokka") version "1.5.30"
    id("build-dashboard")
    `java-gradle-plugin`
    kotlin("jvm") version "1.5.31"
    `kotlin-dsl`
}

group = "com.redpillanalytics"

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation(platform("com.google.cloud:libraries-bom:23.1.0"))
    implementation("com.google.cloud:google-cloud-storage:2.1.7") {
        // exclude guava as it conflicts with the Android Gradle plugin
        exclude("com.google.guava:guava")
    }
    implementation(kotlin("stdlib-jdk8"))
}

gradlePlugin {
    plugins {
        create("gcsBuildCache") {
            id = "com.redpillanalytics.gradle-build-cache"
            implementationClass = "com.redpillanalytics.gradle.buildcache.GCSBuildCachePlugin"
            displayName = "GCS Build Cache"
            description = "A Gradle Build Cache implementation using Google Cloud Storage (GCS) to store the build artifacts. Since this is a settings plugin the build script snippets below won't work. Please consult the documentation at Github."
        }
    }
}

pluginBundle {
    website = "https://github.com/redpillanalytics/gradle-build-cache"
    vcsUrl = "https://github.com/redpillanalytics/gradle-build-cache.git"
    tags = listOf("build-cache", "gcs", "Google Cloud Storage", "cache", "Google Cloud Platform", "gcp")
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

tasks.register("publish") {
    description = "Custom publish task."
    group = "publishing"
    dependsOn(tasks.publishPlugins, tasks.githubRelease, tasks.build)
}

tasks.githubRelease {
    mustRunAfter(tasks.build, tasks.publishPlugins)
}
