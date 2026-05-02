plugins {
    `maven-publish`
    signing
    id("com.vanniktech.maven.publish")
}


mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    pom {
        name.set(project.name)
        description.set("A collection of Kotlin Multiplatform libraries")
        url.set("https://github.com/mrenouf/bitgrind-kmp")

        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/license/mit")
            }
        }

        developers {
            developer {
                id.set("mark.renouf@gmail.com")
                name.set("Mark Renouf")
            }
        }

        scm {
            url.set("https://github.com/mrenouf/bitgrind-kmp")
            connection.set("scm:git:git://github.com/mrenouf/bitgrind-kmp.git")
            developerConnection.set("scm:git:ssh://github.com/mrenouf/bitgrind-kmp.git")
        }
    }
}

val signingKey = providers.gradleProperty("signingKey").orNull
val signingPassword = providers.gradleProperty("signingPassword").orNull

if (signingKey != null) {
    signing {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications)
    }
}