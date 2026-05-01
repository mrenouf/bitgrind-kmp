@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.library")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    jvm()
    jvmToolchain(17)
    linuxX64()
    wasmJs {
        browser()
        generateTypeScriptDefinitions()
        binaries.library()
        compilerOptions {
            target = "es2015"
        }
    }
    js {
        browser()
        useEsModules()
        generateTypeScriptDefinitions()
        binaries.library()
        compilerOptions {
            target = "es2015"
        }
    }
}

android {
    compileSdk = 36
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = 28
    }
}