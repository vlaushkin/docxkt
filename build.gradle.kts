plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.dokka) apply false
}

// Group namespace: `io.github.vlaushkin` is friction-free for Maven Central
// (no DNS verification — Central trusts io.github.<gh-user> when the user
// can prove ownership of the GitHub account). Bumping to a vanity
// `io.docxkt` later is a one-line flip after registering the namespace.
allprojects {
    group = "io.github.vlaushkin"
    version = "1.0.0"
}
