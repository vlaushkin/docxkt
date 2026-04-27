plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    `maven-publish`
    signing
    alias(libs.plugins.dokka)
}

android {
    namespace = "io.docxkt.android"
    compileSdk = 36

    defaultConfig {
        // API 23 (Android 6 Marshmallow) — runtime permissions model. Scoped
        // storage and MediaStore.Downloads still require API 29+ at runtime;
        // the saveToDownloads() legacy path serves API 23-28.
        minSdk = 23
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    // Keep Lint strict — our instrumented tests exist precisely so host-
    // side spot-checks can't mask real API-level or resource issues.
    lint {
        abortOnError = true
        warningsAsErrors = false
        checkDependencies = false
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

kotlin {
    jvmToolchain(21)
    explicitApi()
}

dependencies {
    // `api` so consumers of :docxkt-android transitively see :docxkt-core
    // without declaring both dependencies.
    api(project(":core"))

    // AndroidX core is the home of androidx.core.content.FileProvider, used
    // by consumers for share-intent flows; keeping a single, small runtime
    // dependency is acceptable for an Android-specific convenience layer.
    implementation(libs.androidx.core)

    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.ext.junit)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                artifactId = "docxkt-android"
                from(components["release"])
                pom {
                    name.set("docxkt-android")
                    description.set(
                        "Android conveniences for docxkt: MediaStore-based " +
                            "saveToDownloads, FileProvider URI generation, " +
                            "share-intent builders. Wraps docxkt-core."
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
            sign(publishing.publications["release"])
        }
    }
}
