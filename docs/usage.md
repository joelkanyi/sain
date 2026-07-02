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

## Exporting the Signature

Sain ships with helpers to export the captured `ImageBitmap` in a platform-independent format on all targets (Android, iOS, JVM, JS, and WASM):

| Function | Returns | Description |
|----------|---------|-------------|
| `ImageBitmap.toPngByteArray()` | `ByteArray` | The bitmap encoded as PNG bytes. |
| `ImageBitmap.toBase64()` | `String` | The Base64 string of the PNG-encoded bitmap. |

```kotlin
Sain(
    onComplete = { signatureBitmap ->
        if (signatureBitmap != null) {
            val pngBytes = signatureBitmap.toPngByteArray()
            val base64 = signatureBitmap.toBase64()
            // Store or transfer the bytes or the Base64 string as needed
        }
    },
    ...
)
```

`toBase64()` returns only the Base64 payload. To display it in an HTML `img` tag or similar, prefix it with `data:image/png;base64,`.
