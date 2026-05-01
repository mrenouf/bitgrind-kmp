plugins {
    alias(libs.plugins.nmcp.aggregation)
}

nmcpAggregation {
    centralPortal {
        username = providers.gradleProperty("mavenCentralUsername")
        password = providers.gradleProperty("mavenCentralPassword")
        publishingType = "USER_MANAGED"
    }
}