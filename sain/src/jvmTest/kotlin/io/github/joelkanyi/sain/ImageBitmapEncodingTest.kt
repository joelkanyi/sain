/*
 * Copyright 2026 Joel Kanyi.
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

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.io.encoding.Base64
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ImageBitmapEncodingTest {

    private val pngMagicBytes = byteArrayOf(
        0x89.toByte(),
        0x50,
        0x4E,
        0x47,
        0x0D,
        0x0A,
        0x1A,
        0x0A,
    )

    private fun sampleBitmap() = toImageBitmap(
        width = 20,
        height = 10,
        signatureColor = Color.Black,
        signatureSize = 2.dp,
        signatureSignatureLines = listOf(
            SignatureLine(
                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                end = androidx.compose.ui.geometry.Offset(20f, 10f),
            ),
        ),
    )

    @Test
    fun toPngByteArrayProducesPng() {
        val bytes = sampleBitmap().toPngByteArray()
        assertTrue(bytes.size > pngMagicBytes.size)
        assertContentEquals(pngMagicBytes, bytes.copyOf(pngMagicBytes.size))
    }

    @Test
    fun toBase64EncodesPngBytes() {
        val bitmap = sampleBitmap()
        val base64 = bitmap.toBase64()
        assertTrue(base64.isNotEmpty())
        assertContentEquals(bitmap.toPngByteArray(), Base64.decode(base64))
    }

    @Test
    fun encodedImageKeepsDimensions() {
        val bytes = sampleBitmap().toPngByteArray()
        val image = org.jetbrains.skia.Image.makeFromEncoded(bytes)
        assertEquals(20, image.width)
        assertEquals(10, image.height)
    }
}
