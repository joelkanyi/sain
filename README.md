[![Maven central](https://img.shields.io/maven-central/v/io.github.joelkanyi/sain.svg)](https://search.maven.org/artifact/io.github.joelkanyi/sain) ![Build status](https://github.com/joelkanyi/sain/actions/workflows/build.yml/badge.svg)

<p align="center"><img src="demo/sain.gif" alt="Sign" height="100px"></p>

# Sain (サイン)

A Compose Multiplatform library for capturing and exporting signatures as ImageBitmap with customizable options. Perfect
for electronic signature, legal documents and more.

See the [project's website](https://joelkanyi.github.io/sain/) for documentation.

### Sample Usage
```kotlin
var imageBitmap by remember {
    mutableStateOf<ImageBitmap?>(null)
}

Sain(
    signatureHeight = 250.dp,
    signaturePadColor = Color.White,
    signatureBorderStroke = BorderStroke(
        width = .5.dp,
        color = MaterialTheme.colorScheme.onSurface,
    ),
    signaturePadShape = RoundedCornerShape(8.dp),
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
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Button(
            modifier = Modifier.weight(1f),
            onClick = {
                imageBitmap = null
                action(SignatureAction.CLEAR)
            },
        ) {
            Text("Clear")
        }
        Button(
            modifier = Modifier.weight(1f),
            onClick = {
                action(SignatureAction.COMPLETE)
            },
        ) {
            Text("Complete")
        }
    }
}
```

### Base64 Encoding for `ImageBitmap`

To make it easier to store or transfer `ImageBitmap` across platforms in a format that is platform-independent, we provide a utility to convert `ImageBitmap` to a Base64 string. This functionality is useful in scenarios like signature storage or image uploads.

Add the following lines to your `commonMain`, `androidMain`, and `iosMain` modules to implement this feature.

---

### Implementation

#### `commonMain`
In `commonMain`, define the `expect` function:

```kotlin
expect fun ImageBitmap.toBase64(): String
```

---

#### `androidMain`
In `androidMain`, implement the `actual` function:

```kotlin
actual fun ImageBitmap.toBase64(): String {
    val bitmap = this.asAndroidBitmap()
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.NO_WRAP)
}
```

**Note**: The `asAndroidBitmap()` extension function is from `androidx.compose.ui.graphics`.

---

#### `iosMain`
In `iosMain`, implement the `actual` function:

```kotlin
@OptIn(ExperimentalForeignApi::class)
fun ImageBitmap.toUIImage(): UIImage? {
    val width = this.width
    val height = this.height
    val buffer = IntArray(width * height)

    this.readPixels(buffer)

    val colorSpace = CGColorSpaceCreateDeviceRGB()
    val context = CGBitmapContextCreate(
        data = buffer.refTo(0),
        width = width.toULong(),
        height = height.toULong(),
        bitsPerComponent = 8u,
        bytesPerRow = (4 * width).toULong(),
        space = colorSpace,
        bitmapInfo = CGImageAlphaInfo.kCGImageAlphaPremultipliedLast.value
    )

    val cgImage = CGBitmapContextCreateImage(context)
    return cgImage?.let { UIImage.imageWithCGImage(it) }
}

actual fun ImageBitmap.toBase64(): String {
    val uiImage = this.toUIImage()
    val jpegData = uiImage?.let { UIImageJPEGRepresentation(it, 0.5) }
        ?: return ""
    return jpegData.base64Encoding()
}
```

---

### Usage

To use this function in your multiplatform project:

```kotlin
val base64String = imageBitmap.toBase64()
// Store or transfer the `base64String` as needed
```

#### License

```
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
