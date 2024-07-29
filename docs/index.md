# Sain (サイン)
A Compose Multiplatform library for capturing and exporting signatures as ImageBitmap with customizable options. Perfect for electronic signature, legal documents and more.

</br>

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

#### Usage
Add the `Sain` composable into your project and customize it according to your needs:
```kotlin
var imageBitmap: ImageBitmap? by remember {
    mutableStateOf(null)
}

val state = remember {
    SignatureState()
}

Sain(
    state = state,
    modifier = Modifier
        .fillMaxWidth()
        .height(250.dp)
        .border(
            BorderStroke(
                width = .5.dp,
                color = MaterialTheme.colorScheme.onSurface
            ),
            shape = RoundedCornerShape(8.dp)
        ),
    onComplete = { signatureBitmap ->
        if (signatureBitmap != null) {
            imageBitmap = signatureBitmap
        } else {
            println("Signature is empty")
        }
    },
) { action ->
    Row(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            modifier = Modifier.weight(1f),
            onClick = {
                imageBitmap = null
                action(SignatureAction.CLEAR)
            }) {
            Text("Clear")
        }
        Button(
            modifier = Modifier.weight(1f),
            onClick = {
                action(SignatureAction.COMPLETE)
            }) {
            Text("Complete")
        }
    }
}
```

</br>

#### Actions
The `Sain` composable takes in an `actions` parameter which is a lambda that takes in a `SignatureAction` enum. The `SignatureAction` enum has two values:
- `CLEAR` - Clears the signature
- `COMPLETE` - Completes the signature and returns the signature as an `ImageBitmap` in the `onComplete` lambda

</br>

#### Customization
- `signatureColor` - The color of the signature
- `signatureThickness` - The thickness of the signature
- `modifier` - The modifier for the `Sain` composable which allows you to customize the size, shape, background, border etc of the signature view

</br>

#### Known Issues
The library works well in one orientation. If you rotate the device, the signature will be lost. This is a known issue and will be fixed in the next release.

> Note: The library is now hosted on Maven Central. If you were using the previous version hosted on Jitpack, please update your dependencies to the latest version.

</br>


#### License
```xml
Copyright 2023 Joel Kanyi

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
