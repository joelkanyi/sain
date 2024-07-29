### Including it in your project:

#### Add the Maven Central repository if it is not already there:
```gradle
repositories {
    mavenCentral()
}
```

#### In multiplatform projects, add a dependency to the commonMain source set dependencies
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

#### In Android projects, add the dependency to your dependencies block in your app's build.gradle file:
```kotlin
dependencies {
    implementation("io.github.joelkanyi:sain:<latest-version>")
}
```

#### For those using Gradle Version Catalog, you can add the dependency as follows:
```libs.version.toml
[versions]
sain = "<latest-version>"

[libraries]
sain = { module = "io.github.joelkanyi:sain", version.ref = "sain" }
```

#### Add then include the dependency in your project as follows:
```kotlin
dependencies {
    implementation(libs.sain)
}
```
</br>
