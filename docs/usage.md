# Usage

## Basic Usage

Add the `Sain` composable to your project and customize it according to your needs:

```kotlin
var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

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

The `actions` trailing lambda gives you an `action` callback. Call it with a `SignatureAction` to control the signature pad.

## Actions

The `SignatureAction` enum has two values:

| Action | Description |
|--------|-------------|
| `SignatureAction.CLEAR` | Clears the signature pad |
| `SignatureAction.COMPLETE` | Completes the signature and delivers the result as an `ImageBitmap?` through the `onComplete` callback |

## Customization

### Signature Pad Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `onComplete` | `(ImageBitmap?) -> Unit` | - | Callback with the completed signature bitmap. **Required.** |
| `modifier` | `Modifier` | `Modifier` | Modifier applied to the signature pad layout. |
| `signatureColor` | `Color` | `Color.Black` | The color of the signature stroke. |
| `signatureThickness` | `Dp` | `5.dp` | The thickness of the signature stroke. |
| `signatureHeight` | `Dp` | `250.dp` | The height of the signature pad. |
| `signaturePadColor` | `Color` | `Color.White` | The background color of the signature pad. |
| `signatureBorderStroke` | `BorderStroke` | `BorderStroke(0.5.dp, Color.Black)` | The border stroke of the signature pad. |
| `signaturePadShape` | `CornerBasedShape` | `RoundedCornerShape(8.dp)` | The shape of the signature pad. |
| `state` | `SignatureState` | `rememberSignatureState()` | The state object for the signature pad. |
| `actions` | `@Composable ((SignatureAction) -> Unit) -> Unit` | - | A composable lambda for rendering action buttons (e.g. Clear, Complete). **Required.** |

### Guideline Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `showGuideline` | `Boolean` | `true` | Whether to show a guideline on the signature pad. |
| `guidelineColor` | `Color` | `Color.Gray` | The color of the guideline. |
| `guidelineStrokeWidth` | `Dp` | `1.dp` | The stroke width of the guideline. |
| `guidelineDashIntervals` | `FloatArray` | `floatArrayOf(16f, 16f)` | The dash pattern intervals of the guideline. |
| `guidelinePadding` | `Dp` | `16.dp` | The padding of the guideline from the edges of the signature pad. |
| `guidelineCornerRadius` | `Dp` | `8.dp` | The corner radius of the guideline. |

### Hint Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `hintText` | `String` | `"Sign within this area"` | The hint text displayed when the signature pad is empty. |
| `hintTextStyle` | `TextStyle` | Gray, 16sp, centered | The text style of the hint text. |

## Base64 Encoding

To store or transfer an `ImageBitmap` in a platform-independent format, you can convert it to a Base64 string. Define an `expect`/`actual` function across your source sets:

### commonMain

```kotlin
expect fun ImageBitmap.toBase64(): String
```

### androidMain

```kotlin
actual fun ImageBitmap.toBase64(): String {
    val bitmap = this.asAndroidBitmap()
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.NO_WRAP)
}
```

### iosMain

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

### Usage

```kotlin
val base64String = imageBitmap.toBase64()
// Store or transfer the base64String as needed
```
