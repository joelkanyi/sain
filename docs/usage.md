Add the `Sain` composable into your project and customize it according to your needs:
```kotlin
var imageBitmap: ImageBitmap? by remember {
    mutableStateOf(null)
}

Sain(
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
