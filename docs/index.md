# Sain (サイン)

A Compose Multiplatform library for capturing and exporting signatures as `ImageBitmap` with customizable options. Perfect for electronic signatures, legal documents, and more.

**Supported platforms:** Android · iOS · Desktop (JVM) · Web (JS) · Web (WasmJS)

## Features

- Capture signatures as `ImageBitmap` on all platforms
- Customizable signature color, thickness, pad color, shape, and border
- Optional guideline with configurable style, padding, and dash pattern
- Hint text when the signature pad is empty
- State management with `rememberSignatureState()` for persistence
- Clear and complete actions via `SignatureAction`

## Installation

Add the Maven Central repository if it is not already there:

```kotlin
repositories {
    mavenCentral()
}
```

### Multiplatform Projects

Add the dependency to your `commonMain` source set:

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

    ```toml title="libs.versions.toml"
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

Add the dependency to your app's `build.gradle.kts`:

=== "Kotlin DSL"

    ```kotlin
    dependencies {
        implementation("io.github.joelkanyi:sain:<latest-version>")
    }
    ```

=== "Version Catalog"

    ```toml title="libs.versions.toml"
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

---

[Get Started :material-arrow-right:](get-started.md){ .md-button .md-button--primary }
[View on GitHub :material-github:](https://github.com/joelkanyi/sain){ .md-button }
