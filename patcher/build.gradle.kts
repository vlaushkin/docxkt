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
    // `api` so consumers of :patcher transitively see :core's public types
    // (Paragraph, TableRow, etc. — used as Patch inputs in Phase 32+).
    api(project(":core"))

    // :patcher is otherwise stdlib-only at runtime. javax.xml.parsers and
    // javax.xml.transform are JDK-bundled — DOM parse + serialize for the
    // read side is allowed; emission stays hand-rolled.
    // Do NOT add runtime dependencies — keep the patcher minimal.

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
            artifactId = "docxkt-patcher"
            from(components["java"])
            artifact(javadocJar)
            pom {
                name.set("docxkt-patcher")
                description.set(
                    "Patch existing .docx templates: token replacement, " +
                        "paragraph/row injection, image insertion. Companion " +
                        "to docxkt-core."
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

if (project.findProperty("signArtifacts") == "true") {
    signing {
        useGpgCmd()
        sign(publishing.publications["maven"])
    }
}
