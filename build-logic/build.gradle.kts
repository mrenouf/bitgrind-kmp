plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
}

fun DependencyHandlerScope.pluginImplementation(plugin: Provider<PluginDependency>) {
    val p = plugin.get()
    implementation("${p.pluginId}:${p.pluginId}.gradle.plugin:${p.version.displayName}")
}

dependencies {
    pluginImplementation(libs.plugins.android.library)
    pluginImplementation(libs.plugins.kotlinMultiplatform)
    pluginImplementation(libs.plugins.dokka)
    pluginImplementation(libs.plugins.nmcp.publish)
    pluginImplementation(libs.plugins.nmcp.aggregation)
}