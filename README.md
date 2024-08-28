[![Maven central](https://img.shields.io/maven-central/v/io.github.joelkanyi/sain.svg)](https://search.maven.org/artifact/io.github.joelkanyi/sain) ![Build status](https://github.com/joelkanyi/sain/actions/workflows/build.yml/badge.svg)

<p align="center"><img src="demo/sain.gif" alt="Sign" height="100px"></p>

# Sain (サイン)

A Compose Multiplatform library for capturing and exporting signatures as ImageBitmap with customizable options. Perfect
for electronic signature, legal documents and more.

See the [project's website](https://joelkanyi.github.io/sain/) for documentation.

```kotlin
var imageBitmap by remember {
    mutableStateOf<ImageBitmap?>(null)
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
                color = MaterialTheme.colorScheme.onSurface,
            ),
            shape = RoundedCornerShape(8.dp),
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
