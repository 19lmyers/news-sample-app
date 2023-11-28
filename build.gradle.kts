@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.gradle.versions)
}
true // Needed to make the Suppress annotation work for the plugins block