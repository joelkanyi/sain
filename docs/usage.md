Add the `Sain` composable into your project and customize it according to your needs:

#### Usage
Add the `Sain` composable into your project and customize it according to your needs:
```kotlin
var imageBitmap: ImageBitmap? by remember {
    mutableStateOf(null)
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

</br>

#### Actions
The `Sain` composable takes in an `actions` parameter which is a lambda that takes in a `SignatureAction` enum. The `SignatureAction` enum has two values:
- `CLEAR` - Clears the signature
- `COMPLETE` - Completes the signature and returns the signature as an `ImageBitmap` in the `onComplete` lambda

</br>

#### Customization
- `signatureColor` - The color of the signature
- `signatureThickness` - The thickness of the signature
- `signatureHeight`: The height of the signature pad.
- `signaturePadColor`: The color of the signature pad.
- `signatureBorderStroke`: The border stroke of the signature pad.
- `signaturePadShape`: The shape of the signature pad.
  </br>
