group = "com.bitgrind.kmp"
version = "0.1.0"

plugins {
    id("multiplatform-library")
    id("publish-library")
}

publishing {
    publications.withType<MavenPublication>().configureEach {
        pom {
            name = "BitGrind Structs"
            description = "Kotlin Multiplatform data structures"
        }
    }
}

kotlin {
    sourceSets {
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.bitgrind.structs"
}