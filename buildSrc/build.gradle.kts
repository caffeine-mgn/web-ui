buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
        maven(url = "https://repo.binom.pw")
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.21")
    }
}

plugins {
    kotlin("jvm") version "1.8.21"
    id("com.github.gmazzo.buildconfig") version "3.0.3"
}

repositories {
    mavenLocal()
    mavenCentral()
    maven(url = "https://repo.binom.pw")
    maven(url = "https://plugins.gradle.org/m2/")
}

val kotlinVersion = project.property("kotlin.version") as String
val kotlinxCoroutinesVersion = project.property("kotlinx_coroutines.version") as String

buildConfig {
    packageName(project.group.toString())
    buildConfigField("String", "KOTLIN_VERSION", "\"$kotlinVersion\"")
    buildConfigField("String", "KOTLINX_COROUTINES_VERSION", "\"$kotlinxCoroutinesVersion\"")
}

dependencies {
    api("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    api("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    api("pw.binom:binom-publish:0.1.8")
}


