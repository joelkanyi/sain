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

import androidx.compose.ui.geometry.Offset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class SignatureLineTest {

    @Test
    fun equalityForIdenticalLines() {
        val line1 = SignatureLine(start = Offset(1f, 2f), end = Offset(3f, 4f))
        val line2 = SignatureLine(start = Offset(1f, 2f), end = Offset(3f, 4f))
        assertEquals(line1, line2)
    }

    @Test
    fun inequalityForDifferentStart() {
        val line1 = SignatureLine(start = Offset(1f, 2f), end = Offset(3f, 4f))
        val line2 = SignatureLine(start = Offset(5f, 6f), end = Offset(3f, 4f))
        assertNotEquals(line1, line2)
    }

    @Test
    fun inequalityForDifferentEnd() {
        val line1 = SignatureLine(start = Offset(1f, 2f), end = Offset(3f, 4f))
        val line2 = SignatureLine(start = Offset(1f, 2f), end = Offset(7f, 8f))
        assertNotEquals(line1, line2)
    }

    @Test
    fun copyPreservesValues() {
        val original = SignatureLine(start = Offset(10f, 20f), end = Offset(30f, 40f))
        val copy = original.copy()
        assertEquals(original, copy)
    }

    @Test
    fun copyCanOverrideStart() {
        val original = SignatureLine(start = Offset(10f, 20f), end = Offset(30f, 40f))
        val modified = original.copy(start = Offset(99f, 99f))
        assertEquals(Offset(99f, 99f), modified.start)
        assertEquals(Offset(30f, 40f), modified.end)
    }

    @Test
    fun copyCanOverrideEnd() {
        val original = SignatureLine(start = Offset(10f, 20f), end = Offset(30f, 40f))
        val modified = original.copy(end = Offset(99f, 99f))
        assertEquals(Offset(10f, 20f), modified.start)
        assertEquals(Offset(99f, 99f), modified.end)
    }

    @Test
    fun hashCodeConsistentWithEquals() {
        val line1 = SignatureLine(start = Offset(1f, 2f), end = Offset(3f, 4f))
        val line2 = SignatureLine(start = Offset(1f, 2f), end = Offset(3f, 4f))
        assertEquals(line1.hashCode(), line2.hashCode())
    }

    @Test
    fun zeroLengthLine() {
        val line = SignatureLine(start = Offset(5f, 5f), end = Offset(5f, 5f))
        assertEquals(line.start, line.end)
    }

    @Test
    fun negativeCoordinates() {
        val line = SignatureLine(start = Offset(-10f, -20f), end = Offset(-30f, -40f))
        assertEquals(Offset(-10f, -20f), line.start)
        assertEquals(Offset(-30f, -40f), line.end)
    }
}
