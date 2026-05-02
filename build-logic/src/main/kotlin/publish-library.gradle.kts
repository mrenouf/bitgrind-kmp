import org.gradle.plugins.signing.SigningExtension

plugins {
    `maven-publish`
    signing
    id("com.gradleup.nmcp")
    id("org.jetbrains.dokka")
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier = "javadoc"
    from(tasks.named("dokkaHtml"))
}

publishing {
    publications.withType<MavenPublication>().configureEach {
        artifact(javadocJar)
        pom {
            url = "https://github.com/mrenouf/bitgrind-kmp"
            licenses {
                license {
                    name = "Apache License 2.0"
                    url = "https://www.apache.org/licenses/LICENSE-2.0"
                }
            }
            developers {
                developer {
                    id = "mrenouf"
                    name = "Mark Renouf"
                    email = "mark.renouf@gmail.com"
                }
            }
            scm {
                connection = "scm:git:git://github.com/mrenouf/bitgrind-kmp.git"
                developerConnection = "scm:git:ssh://github.com/mrenouf/bitgrind-kmp.git"
                url = "https://github.com/mrenouf/bitgrind-kmp"
            }
        }
    }
}

val signingKey = providers.gradleProperty("signingKey").orNull
val signingPassword = providers.gradleProperty("signingPassword").orNull

if (signingKey != null) {
    extensions.configure<SigningExtension> {
        useInMemoryPgpKeys(signingKey, signingPassword)
    }
    // configureEach fires per-publication as KMP registers them, avoiding ordering issues
    publishing.publications.withType<MavenPublication>().configureEach {
        extensions.getByType<SigningExtension>().sign(this)
    }
}