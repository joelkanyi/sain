# Getting Started

## Prerequisites

- A **Kotlin Multiplatform** or **Android** project with the **Compose Multiplatform** plugin applied

## Add Repository

Ensure `mavenCentral()` is in your repositories block (this is the default for most projects):

```kotlin
repositories {
    mavenCentral()
}
```

## Add Dependency

### Multiplatform Projects

Add the dependency to your `commonMain` source set dependencies:

=== "Kotlin DSL"

    ```kotlin
    kotlin {
        sourceSets {
            commonMain {
                dependencies {
                    implementation("io.github.joelkanyi:sain:<latest-version>")
                }
            }
        }
    }
    ```

=== "Version Catalog"

    Add to your `libs.versions.toml`:

    ```toml
    [versions]
    sain = "<latest-version>"

    [libraries]
    sain = { module = "io.github.joelkanyi:sain", version.ref = "sain" }
    ```

    Then in your `build.gradle.kts`:

    ```kotlin
    kotlin {
        sourceSets {
            commonMain {
                dependencies {
                    implementation(libs.sain)
                }
            }
        }
    }
    ```

### Android Projects

If you are working on an Android-only project, add the dependency directly to your app's `build.gradle.kts`:

=== "Kotlin DSL"

    ```kotlin
    dependencies {
        implementation("io.github.joelkanyi:sain:<latest-version>")
    }
    ```

=== "Version Catalog"

    Add to your `libs.versions.toml`:

    ```toml
    [versions]
    sain = "<latest-version>"

    [libraries]
    sain = { module = "io.github.joelkanyi:sain", version.ref = "sain" }
    ```

    Then in your `build.gradle.kts`:

    ```kotlin
    dependencies {
        implementation(libs.sain)
    }
    ```

## Platform Notes

| Platform | Status |
|----------|--------|
| **Android** | Works out of the box |
| **iOS** | Requires Compose Multiplatform iOS support enabled in your project |
| **Desktop (JVM)** | Works out of the box with JVM target |
| **Web (JS)** | Works with `js(IR)` browser target |
| **Web (WasmJS)** | Works with `wasmJs` browser target |

## Next Steps

Head over to the [Usage](usage.md) page to learn how to use the signature pad in your app.
