/*
 * Copyright 2024 Joel Kanyi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.joelkanyi.sain

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Sain is a composable that allows the user to draw a signature on the
 * screen. The resulting signature is returned as an [ImageBitmap].
 *
 * @param onComplete The callback that is called when the signature is
 *    completed. This callback receives a nullable ImageBitmap of the
 *    signature and can be utilized to check if the signature is empty.
 * @param modifier The modifier to apply to the signature.
 * @param signatureColor The color of the signature.
 * @param signatureThickness The thickness of the signature. Note that the
 *    thickness is multiplied by 3 to make the signature more visible.
 * @param state The state of the signature.
 * @param actions The composable that provides the actions that can be
 *    performed on the signature. We only have two actions:
 *    [SignatureAction.CLEAR] and [SignatureAction.COMPLETE].
 */
@Suppress("ktlint:compose:modifier-not-used-at-root")
@Composable
public fun Sain(
    onComplete: (signature: ImageBitmap?) -> Unit,
    modifier: Modifier = Modifier,
    signatureColor: Color = Color.Black,
    signatureThickness: Dp = 5.dp,
    state: SignatureState = rememberSignatureState(),
    actions: @Composable (
        action: (SignatureAction) -> Unit,
    ) -> Unit,
) {
    Column {
        Box(
            modifier = modifier
                .pointerInput(true) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()

                        val signatureLine = SignatureLine(
                            start = change.position - dragAmount,
                            end = change.position,
                        )

                        state.addSignatureLine(signatureLine)
                    }
                }
                .drawWithContent {
                    drawContent()
                    state.updateSignature(
                        toImageBitmap(
                            width = size.width.toInt(),
                            height = size.height.toInt(),
                            signatureColor = signatureColor,
                            signatureSize = signatureThickness,
                            signatureSignatureLines = state.signatureLines,
                        ),
                    )
                },
        ) {
            state.signature?.let {
                Image(
                    bitmap = it,
                    contentDescription = "Signature",
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }

        actions {
            when (it) {
                SignatureAction.CLEAR -> {
                    state.clearSignatureLines()
                }

                SignatureAction.COMPLETE -> {
                    onComplete(
                        if (state.signatureLines.isNotEmpty()) {
                            state.signature
                        } else {
                            null
                        },
                    )
                }
            }
        }
    }
}

/**
 * SignatureState is a class that holds the state of the signature. It
 * holds the signature lines and the signature itself.
 *
 * @constructor Creates a new instance of SignatureState.
 * @property signatureLines The list of signature lines.
 * @property signature The signature as an ImageBitmap.
 * @see SignatureLine
 * @see ImageBitmap
 * @see toImageBitmap
 */
@Stable
public class SignatureState {
    private val _signatureLines = mutableStateListOf<SignatureLine>()
    public val signatureLines: List<SignatureLine> get() = _signatureLines.toList()

    private val _signature = mutableStateOf<ImageBitmap?>(null)
    public val signature: ImageBitmap? get() = _signature.value

    public fun addSignatureLine(signatureLine: SignatureLine) {
        _signatureLines.add(signatureLine)
    }

    public fun clearSignatureLines() {
        _signatureLines.clear()
    }

    public fun updateSignature(bitmap: ImageBitmap) {
        _signature.value = bitmap
    }

    public companion object {
        public val Saver: Saver<SignatureState, *> = Saver(
            save = {
                it.signatureLines to it.signature
            },
            restore = {
                SignatureState().apply {
                    _signatureLines.addAll(it.first)
                    _signature.value = it.second
                }
            },
        )
    }
}

/**
 * [rememberSignatureState] is a composable that returns a [SignatureState]
 * instance
 */
@Composable
public fun rememberSignatureState(): SignatureState = rememberSaveable(saver = SignatureState.Saver) {
    SignatureState()
}
