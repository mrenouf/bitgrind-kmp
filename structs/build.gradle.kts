group = "com.bitgrind.kmp"
version = "0.1.0"

plugins {
    id("multiplatform-library")
    id("publish-library")
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