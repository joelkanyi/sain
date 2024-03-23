pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
    }
}

rootProject.name = "ComposeSignature"
include(":composesignature")
include(":sample:shared")
include(":sample:android")
include(":sample:desktop")
include(":sample:web-wasm")
include(":sample:web-js")
