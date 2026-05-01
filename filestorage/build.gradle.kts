group = "com.bitgrind"
version = "0.1.3"

plugins {
    id("multiplatform-library")
    id("publish-library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.io.core)
            implementation(libs.kotlinx.coroutines.core)
        }
        webMain.dependencies {
            implementation(libs.kotlin.wrappers.browser)
            implementation(libs.kotlin.wrappers.js)
            implementation(libs.kotlin.wrappers.web)
            implementation(libs.kotlinx.browser)
        }
        jvmTest.dependencies {
            implementation(libs.junit.jupiter.api)
            implementation(libs.junit.jupiter.params)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

android {
    namespace = "com.bitgrind.filestorage"
}

tasks.withType<Test> {
    useJUnitPlatform()
}