group = "com.bitgrind.kmp"
version = "0.1.0"

plugins {
    id("multiplatform-library")
    id("publish-library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlincrypto.random)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmTest.dependencies {
            implementation(libs.kotlin.test.junit)
            implementation(libs.junit.jupiter.api)
        }
    }
}

android {
    namespace = "com.bitgrind.kmp.ulid"
}

publishing {
    publications.withType<MavenPublication>().configureEach {
        pom {
            name = "BitGrind ULID"
            description = "Kotlin Multiplatform implementation of Universally Unique Lexicographically Sortable Identifiers (ULID)"
        }
    }
}