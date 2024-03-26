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

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset

/**
 * SignatureLine is a data class that represents a line in a signature.
 * @param start The starting point of the line.
 * @param end The ending point of the line.
 */
@Immutable
public data class SignatureLine(
    val start: Offset,
    val end: Offset,
)
