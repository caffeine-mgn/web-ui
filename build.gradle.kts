import pw.binom.getGitBranch
import pw.binom.publish.propertyOrNull

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("maven-publish")
}

val jsRun = System.getProperty("jsrun") != null

allprojects {
    val branch = getGitBranch()
    version = System.getenv("GITHUB_REF_NAME") ?: propertyOrNull("version")?.takeIf { it != "unspecified" }
        ?: "1.0.0-SNAPSHOT"
    group = "pw.binom"

    repositories {
        mavenLocal()
        mavenCentral()
        google()
    }
}

kotlin {
    if (jsRun) {
        js("js") {
            browser {
                testTask {
                    useKarma {
                        useFirefox()
//                        useFirefoxHeadless()
//                        useChromium()
                    }
                }
            }
            binaries.executable()
        }
    } else {
        var applled = false
        js(IR) {
            browser()
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(kotlin("stdlib-common"))
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${pw.binom.Versions.KOTLINX_COROUTINES_VERSION}")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jsMain by getting {
            dependencies {
                api(kotlin("stdlib-js"))
            }
        }

        val jsTest by getting {
            dependencies {
                api(kotlin("test-js"))
            }
        }
    }
}
apply<pw.binom.publish.plugins.PrepareProject>()
