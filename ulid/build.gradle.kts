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
    }
}

android {
    namespace = "com.bitgrind.kmp.ulid"
}