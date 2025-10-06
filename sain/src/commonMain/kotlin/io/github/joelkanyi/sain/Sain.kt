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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
 * @param signatureHeight The height of the signature.
 * @param signaturePadColor The color of the signature pad.
 * @param signatureBorderStroke The border stroke of the signature pad.
 * @param signaturePadShape The shape of the signature pad.
 * @param state The state of the signature.
 * @param showGuideline Whether to show the guideline or not.
 * @param guidelineColor The color of the guideline.
 * @param guidelineStrokeWidth The stroke width of the guideline.
 * @param guidelineDashIntervals The dash intervals of the guideline.
 * @param guidelinePadding The padding of the guideline from the edges of
 *    the signature pad.
 * @param guidelineCornerRadius The corner radius of the guideline.
 *   This is only applicable if the guideline shape is a rounded rectangle.
 * @param hintText The hint text to show when the signature pad is empty.
 * @param hintTextStyle The text style of the hint text.
 * @param actions The composable that provides the actions that can be
 */
@Composable
public fun Sain(
    onComplete: (signature: ImageBitmap?) -> Unit,
    modifier: Modifier = Modifier,
    signatureColor: Color = Color.Black,
    signatureThickness: Dp = 5.dp,
    signatureHeight: Dp = 250.dp,
    signaturePadColor: Color = Color.White,
    signatureBorderStroke: BorderStroke = BorderStroke(
        color = Color.Black,
        width = .5.dp,
    ),
    signaturePadShape: CornerBasedShape = RoundedCornerShape(8.dp),
    state: SignatureState = rememberSignatureState(),
    showGuideline: Boolean = true,
    guidelineColor: Color = Color.Gray,
    guidelineStrokeWidth: Dp = 1.dp,
    guidelineDashIntervals: FloatArray = floatArrayOf(16f, 16f),
    guidelinePadding: Dp = 16.dp,
    guidelineCornerRadius: Dp = 8.dp,
    hintText: String = "Sign within this area",
    hintTextStyle: TextStyle = TextStyle(
        color = Color.Gray,
        fontSize = 16.sp,
        textAlign = TextAlign.Center,
    ),
    actions: @Composable (action: (SignatureAction) -> Unit) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(signatureHeight)
                .background(signaturePadColor)
                .border(
                    border = signatureBorderStroke,
                    shape = signaturePadShape,
                )
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

                    // Only draw guideline if enabled
                    if (showGuideline) {
                        val paddingPx = guidelinePadding.toPx()
                        val rectWidth = size.width - (paddingPx * 2)
                        val rectHeight = size.height - (paddingPx * 2)

                        drawRoundRect(
                            color = guidelineColor,
                            topLeft = Offset(paddingPx, paddingPx),
                            size = Size(rectWidth, rectHeight),
                            style = Stroke(
                                width = guidelineStrokeWidth.toPx(),
                                pathEffect = PathEffect.dashPathEffect(guidelineDashIntervals),
                            ),
                            cornerRadius = CornerRadius(guidelineCornerRadius.toPx()),
                        )
                    }

                    // Update signature bitmap
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
            // Render signature image if exists
            state.signature?.let {
                Image(
                    bitmap = it,
                    contentDescription = "Signature",
                    modifier = Modifier.fillMaxSize(),
                )
            }

            // Show hint text only when empty & guideline enabled
            if (showGuideline && state.signatureLines.isEmpty()) {
                BasicText(
                    text = hintText,
                    modifier = Modifier.align(Alignment.Center),
                    style = hintTextStyle,
                )
            }
        }

        actions {
            when (it) {
                SignatureAction.CLEAR -> state.clearSignatureLines()
                SignatureAction.COMPLETE -> {
                    onComplete(
                        if (state.signatureLines.isNotEmpty()) state.signature else null,
                    )
                }
            }
        }
    }
}

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
 * @deprecated This function has been deprecated in favor of using the Sain composable with the signatureHeight, signaturePadColor, signatureBorderStroke, and signaturePadShape parameters.
 */

@Suppress("ktlint:compose:modifier-not-used-at-root")
@Deprecated(
    message = """
    This function has been deprecated in favor of using the
    Sain composable with the signatureHeight, signaturePadColor, signatureBorderStroke, and signaturePadShape parameters.
    """,
    level = DeprecationLevel.WARNING,
    replaceWith = ReplaceWith(
        imports = [
            "io.github.joelkanyi.sain.Sain",
        ],
        expression =
        "\nSain(\n" +
            "    signatureHeight = 250.dp,\n" +
            "    signaturePadColor = Color.White,\n" +
            "    signatureBorderStroke = BorderStroke(\n" +
            "        width = .5.dp,\n" +
            "        color = MaterialTheme.colorScheme.onSurface,\n" +
            "    ),\n" +
            "    signaturePadShape = RoundedCornerShape(8.dp),\n" +
            "    onComplete = onComplete\n" +
            ") { action ->  }",
    ),
)
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
                        .fillMaxSize(),
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
        public val Saver: Saver<SignatureState, Any> = Saver(
            save = { state ->
                // Store only values that are Parcelable/Serializable
                state.signatureLines.map { line ->
                    listOf(line.start.x, line.start.y, line.end.x, line.end.y)
                }
            },
            restore = { saved ->
                @Suppress("UNCHECKED_CAST")
                val points = saved as List<List<Float>>
                SignatureState().apply {
                    _signatureLines.addAll(
                        points.map {
                            SignatureLine(
                                start = Offset(it[0], it[1]),
                                end = Offset(it[2], it[3]),
                            )
                        },
                    )
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
