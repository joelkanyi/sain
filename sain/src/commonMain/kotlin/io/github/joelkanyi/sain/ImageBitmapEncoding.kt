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

import androidx.compose.ui.graphics.ImageBitmap
import kotlin.io.encoding.Base64

/**
 * Encodes this [ImageBitmap] to a PNG byte array.
 *
 * @return The PNG-encoded bytes of this bitmap.
 */
public expect fun ImageBitmap.toPngByteArray(): ByteArray

/**
 * Encodes this [ImageBitmap] to a Base64 string of its PNG representation.
 *
 * The returned string contains only the Base64 payload. To use it in an
 * HTML `img` tag or similar, prepend `data:image/png;base64,` to it.
 *
 * @return The Base64-encoded PNG representation of this bitmap.
 */
public fun ImageBitmap.toBase64(): String = Base64.encode(toPngByteArray())
