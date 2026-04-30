import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    `maven-publish`
    signing
    alias(libs.plugins.dokka)
}

kotlin {
    explicitApi()
    jvmToolchain(21)

    jvm()
    androidTarget {
        publishLibraryVariants("release")
    }

    // Apple-Native targets. macosX64 was deprecated in 2.3.20 and is
    // intentionally not declared. The codec actuals in appleMain bind
    // libz through Kotlin/Native's bundled `platform.zlib` interop —
    // no custom cinterop needed.
    val iosTargets = listOf(iosX64(), iosArm64(), iosSimulatorArm64())
    val macosTarget = macosArm64()

    // XCFramework collects per-target iOS framework binaries into a
    // single distributable bundle (universal arm64 + simulator arm64
    // + simulator x64). Swift consumers reference it via SPM's
    // .binaryTarget(url:checksum:) or a CocoaPods podspec with
    // vendored_frameworks. Build with `./gradlew :core:assembleDocxktReleaseXCFramework`.
    val xcf = XCFramework("Docxkt")
    (iosTargets + macosTarget).forEach { target ->
        target.binaries.framework {
            baseName = "Docxkt"
            xcf.add(this)
        }
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        // Manual intermediate: jvmMain and androidMain are siblings under
        // commonMain in the default hierarchy template. JDK-bound code
        // (java.util.zip, java.io.OutputStream, java.time.Instant) lives
        // here so it compiles for both targets without duplication.
        val jvmAndAndroidMain by creating { dependsOn(commonMain.get()) }
        jvmMain.get().dependsOn(jvmAndAndroidMain)
        androidMain.get().dependsOn(jvmAndAndroidMain)

        commonMain.dependencies {
            // Single non-stdlib runtime dep accepted in :core. Only used for
            // the public IO Sink type on Document.writeTo(sink). Internal
            // code stays on per-platform stdlib (JDK java.util.zip, libz
            // cinterop on Apple).
            implementation(libs.kotlinx.io.core)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            // Hand-rolled XML normalizer in commonTest leans on xmlutil for
            // multiplatform parsing — XMLUnit is JVM-only.
            implementation(libs.xmlutil.core)
        }

        androidMain.dependencies {
            // androidx.core hosts FileProvider, used by ContentUriExtensions /
            // FileProviderExtensions for share-intent flows. Scoped to the
            // Android source set so appleMain doesn't pull these symbols.
            implementation(libs.androidx.core)
        }

        jvmTest.dependencies {
            implementation(libs.junit.jupiter)
            implementation(libs.junit.jupiter.params)
            runtimeOnly(libs.junit.platform.launcher)
            implementation(libs.xmlunit.core)
            implementation(libs.xmlunit.matchers)
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "io.docxkt.core"
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

    lint {
        abortOnError = true
        warningsAsErrors = false
        checkDependencies = false
    }
}

dependencies {
    "jvmTestImplementation"(platform(libs.junit.bom))
    "androidInstrumentedTestImplementation"(libs.androidx.test.runner)
    "androidInstrumentedTestImplementation"(libs.androidx.test.ext.junit)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    testLogging {
        events("passed", "failed", "skipped")
        showStandardStreams = false
    }
}

// Native test tasks read fixtures from the project's
// `src/jvmTest/resources/fixtures/` directory at runtime. The host
// macOS process inherits the env var via KotlinNativeTest configure;
// iOS Simulator processes do NOT inherit env vars through `simctl
// spawn`, so we ALSO bake the absolute path into a generated Kotlin
// constant the appleTest source set picks up. Either path resolves
// the same on-disk fixtures the JVM regression net hits.
val fixturesRoot = file("src/jvmTest/resources/fixtures").absolutePath
tasks.withType<org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest>().configureEach {
    environment("DOCXKT_FIXTURES_ROOT", fixturesRoot)
}

// Generate FixturesPath.kt at configuration time so the absolute on-
// disk path of the fixtures dir is baked into the appleTest binary.
// iOS Simulator processes don't inherit the env var via simctl spawn,
// so the constant is the reliable fallback. The config-time write
// keeps Gradle's configuration cache happy (no closures-over-script).
val fixturesPathSourceDir = layout.buildDirectory.dir("generated/sources/fixturesPath/appleTest/kotlin").get().asFile
fixturesPathSourceDir.mkdirs()
val fixturesPathPackage = fixturesPathSourceDir.resolve("io/docxkt/testing")
fixturesPathPackage.mkdirs()
fixturesPathPackage.resolve("FixturesPath.kt").writeText(
    """
    // AUTO-GENERATED by core/build.gradle.kts. Do not edit.
    package io.docxkt.testing

    internal const val FIXTURES_ROOT_BAKED: String =
        "$fixturesRoot"
    """.trimIndent() + "\n",
)

kotlin.sourceSets.named("appleTest") {
    kotlin.srcDir(fixturesPathSourceDir)
}

// Generate FixtureCatalog.kt at configuration time. Walks the fixture
// directory tree and emits a `Map<fixtureName, List<partPath>>` of
// every .xml / .rels file. MppFixtureTest auto-discovery reads this
// at runtime instead of doing a filesystem walk — keeps the harness
// platform-agnostic (no java.nio.Files.walk on Apple targets).
val fixtureCatalogSourceDir = layout.buildDirectory.dir("generated/sources/fixtureCatalog/commonTest/kotlin").get().asFile
fixtureCatalogSourceDir.mkdirs()
val fixtureCatalogPackage = fixtureCatalogSourceDir.resolve("io/docxkt/testing")
fixtureCatalogPackage.mkdirs()
val fixturesDir = file("src/jvmTest/resources/fixtures")
val catalogEntries = if (fixturesDir.isDirectory) {
    fixturesDir.listFiles()
        ?.filter { it.isDirectory }
        ?.sortedBy { it.name }
        ?.associate { fixtureFolder ->
            val parts = fixtureFolder.walkTopDown()
                .filter { it.isFile }
                .filter { it.name.endsWith(".xml") || it.name.endsWith(".rels") }
                .map { it.relativeTo(fixtureFolder).invariantSeparatorsPath }
                .sorted()
                .toList()
            fixtureFolder.name to parts
        }
        ?: emptyMap()
} else emptyMap()
fixtureCatalogPackage.resolve("FixtureCatalog.kt").writeText(
    buildString {
        appendLine("// AUTO-GENERATED by core/build.gradle.kts. Do not edit.")
        appendLine("package io.docxkt.testing")
        appendLine()
        appendLine("internal val FIXTURE_CATALOG: Map<String, List<String>> = mapOf(")
        for ((name, parts) in catalogEntries) {
            appendLine("    \"$name\" to listOf(")
            for (p in parts) {
                appendLine("        \"$p\",")
            }
            appendLine("    ),")
        }
        appendLine(")")
    },
)

kotlin.sourceSets.named("commonTest") {
    kotlin.srcDir(fixtureCatalogSourceDir)
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from(tasks.named("dokkaGeneratePublicationHtml"))
}

publishing {
    publications.withType<MavenPublication>().configureEach {
        artifact(javadocJar)
        artifactId = when (name) {
            "kotlinMultiplatform" -> "docxkt-core"
            else -> "docxkt-core-$name"
        }
        pom {
            name.set("docxkt-core")
            description.set(
                "Kotlin Multiplatform library for generating Microsoft " +
                    "Word .docx files. Targets JVM, Android, iOS, and " +
                    "macOS. Port of dolanmiu/docx (TypeScript)."
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

if (project.findProperty("signArtifacts") == "true") {
    signing {
        useGpgCmd()
        sign(publishing.publications)
    }
}
