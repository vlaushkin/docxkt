plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-library`
    `maven-publish`
    signing
    alias(libs.plugins.dokka)
}

kotlin {
    jvmToolchain(21)
    explicitApi()
}

java {
    withSourcesJar()
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from(tasks.named("dokkaGeneratePublicationHtml"))
}

dependencies {
    // :core is stdlib-only at runtime.
    // Do NOT add runtime dependencies — :core is stdlib-only by design.

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.xmlunit.core)
    testImplementation(libs.xmlunit.matchers)
    testImplementation(libs.kotlin.test)
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "failed", "skipped")
        showStandardStreams = false
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "docxkt-core"
            from(components["java"])
            artifact(javadocJar)
            pom {
                name.set("docxkt-core")
                description.set(
                    "Pure Kotlin/JVM library for generating Microsoft Word " +
                        ".docx files. Port of dolanmiu/docx (TypeScript)."
                )
                url.set("https://github.com/vlaushkin/docxkt")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        id.set("vlaushkin")
                        name.set("Vasily Laushkin")
                        email.set("vaslinux@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/vlaushkin/docxkt.git")
                    developerConnection.set("scm:git:ssh://git@github.com/vlaushkin/docxkt.git")
                    url.set("https://github.com/vlaushkin/docxkt")
                }
            }
        }
    }
}

// Sign artifacts only when explicitly requested via -PsignArtifacts=true
// (Maven Central release flow). When unset (local dev, JitPack,
// publishToMavenLocal), no .asc artifacts are added to the publication
// so the publish task does not demand them.
if (project.findProperty("signArtifacts") == "true") {
    signing {
        useGpgCmd()
        sign(publishing.publications["maven"])
    }
}
