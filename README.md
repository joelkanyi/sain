<p align="center"><img src="demo/ComposeSignature.png" alt="MealTime" height="150px"></p>

<h1 align="center">ComposeSignature</h1></br>

<p align="center">
✍️ A Jetpack Compose library for capturing and exporting signatures as Bitmap with customizable options. Perfect for electronic signature, legal documents and more.
</p>
</br>

## Including it in your project:

### Step 1. Add the JitPack repository to your `settings.gradle` file
```gradle
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
```

### Step 2. Add the dependency
```gradle
    dependencies {
        implementation 'com.github.JoelKanyi:ComposeSignature:1.0.2'
    }
```

### Usage
Add the `ComposeSignature` composable into your project and customize it according to your needs:
```kotlin
var imageBitmap: ImageBitmap? by remember {
    mutableStateOf(null)
}

ComposeSignature(
    modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
    signaturePadColor = Color(0xFFEEEEEE),
    signaturePadHeight = 400.dp,
    signatureColor = Color.Black,
    signatureThickness = 10f,
    onComplete = { signatureBitmap ->
        imageBitmap = signatureBitmap.asImageBitmap()
    },
    onClear = {
        imageBitmap = null
    }
)
```

### Known Issues
- Currently the library works well on protrait mode, when you rotate your device to landscape and then enter your signature, you might endup having an incomplete signature.

### Demo
<img src="demo/demo.gif" width="250"/>
</br>

### License
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
