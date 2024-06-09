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

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

/**
 * Draws the given [SignatureLine]s to a Canvas and returns the resulting [ImageBitmap].
 * @param width The width of the resulting [ImageBitmap].
 * @param height The height of the resulting [ImageBitmap].
 * @param signatureColor The color of the signature lines.
 * @param signatureSize The thickness of the signature lines. Note that
 * the thickness is multiplied by 3 to make the signature lines more visible.
 * @param signatureSignatureLines The list of signature lines to draw.
 */
internal fun toImageBitmap(
    width: Int,
    height: Int,
    signatureColor: Color,
    signatureSize: Dp,
    signatureSignatureLines: List<SignatureLine>,
): ImageBitmap {
    val imgBitmap = ImageBitmap(width, height)

    Canvas(imgBitmap).apply {
        CanvasDrawScope().draw(
            density = Density(1f, 1f),
            layoutDirection = LayoutDirection.Ltr,
            canvas = this,
            size = Size(width.toFloat(), height.toFloat()),
        ) {
            signatureSignatureLines.forEach { line ->
                drawLine(
                    color = signatureColor,
                    start = line.start,
                    end = line.end,
                    strokeWidth = (signatureSize * 3).toPx(),
                    cap = StrokeCap.Round,
                )
            }
        }
    }

    return imgBitmap
}
