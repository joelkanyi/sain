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

import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.ui.geometry.Offset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SignatureStateTest {

    private fun saveState(state: SignatureState): Any? = with(SignatureState.Saver) {
        SaverScope { true }.save(state)
    }

    @Test
    fun initialStateHasEmptyLines() {
        val state = SignatureState()
        assertTrue(state.signatureLines.isEmpty())
    }

    @Test
    fun initialStateHasNullSignature() {
        val state = SignatureState()
        assertNull(state.signature)
    }

    @Test
    fun addSignatureLineIncreasesCount() {
        val state = SignatureState()
        val line = SignatureLine(
            start = Offset(0f, 0f),
            end = Offset(10f, 10f),
        )
        state.addSignatureLine(line)
        assertEquals(1, state.signatureLines.size)
    }

    @Test
    fun addSignatureLinePreservesCoordinates() {
        val state = SignatureState()
        val line = SignatureLine(
            start = Offset(5f, 15f),
            end = Offset(25f, 35f),
        )
        state.addSignatureLine(line)
        val result = state.signatureLines.first()
        assertEquals(Offset(5f, 15f), result.start)
        assertEquals(Offset(25f, 35f), result.end)
    }

    @Test
    fun addMultipleSignatureLines() {
        val state = SignatureState()
        val line1 = SignatureLine(start = Offset(0f, 0f), end = Offset(1f, 1f))
        val line2 = SignatureLine(start = Offset(2f, 2f), end = Offset(3f, 3f))
        val line3 = SignatureLine(start = Offset(4f, 4f), end = Offset(5f, 5f))
        state.addSignatureLine(line1)
        state.addSignatureLine(line2)
        state.addSignatureLine(line3)
        assertEquals(3, state.signatureLines.size)
        assertEquals(line1, state.signatureLines[0])
        assertEquals(line2, state.signatureLines[1])
        assertEquals(line3, state.signatureLines[2])
    }

    @Test
    fun clearSignatureLinesRemovesAll() {
        val state = SignatureState()
        state.addSignatureLine(SignatureLine(start = Offset(0f, 0f), end = Offset(1f, 1f)))
        state.addSignatureLine(SignatureLine(start = Offset(2f, 2f), end = Offset(3f, 3f)))
        state.clearSignatureLines()
        assertTrue(state.signatureLines.isEmpty())
    }

    @Test
    fun clearSignatureLinesOnEmptyStateDoesNotThrow() {
        val state = SignatureState()
        state.clearSignatureLines()
        assertTrue(state.signatureLines.isEmpty())
    }

    @Test
    fun signatureLinesReturnsDefensiveCopy() {
        val state = SignatureState()
        state.addSignatureLine(SignatureLine(start = Offset(0f, 0f), end = Offset(1f, 1f)))
        val snapshot = state.signatureLines
        state.addSignatureLine(SignatureLine(start = Offset(2f, 2f), end = Offset(3f, 3f)))
        assertEquals(1, snapshot.size, "Snapshot should not be affected by subsequent additions")
        assertEquals(2, state.signatureLines.size)
    }

    @Test
    fun saverRoundTripsEmptyState() {
        val original = SignatureState()
        val saved = saveState(original)!!
        val restored = SignatureState.Saver.restore(saved)!!
        assertTrue(restored.signatureLines.isEmpty())
    }

    @Test
    fun saverRoundTripsStateWithLines() {
        val original = SignatureState()
        val line1 = SignatureLine(start = Offset(1f, 2f), end = Offset(3f, 4f))
        val line2 = SignatureLine(start = Offset(5f, 6f), end = Offset(7f, 8f))
        original.addSignatureLine(line1)
        original.addSignatureLine(line2)

        val saved = saveState(original)!!
        val restored = SignatureState.Saver.restore(saved)!!

        assertEquals(2, restored.signatureLines.size)
        assertEquals(line1, restored.signatureLines[0])
        assertEquals(line2, restored.signatureLines[1])
    }

    @Test
    fun saverPreservesExactCoordinates() {
        val original = SignatureState()
        val line = SignatureLine(
            start = Offset(1.5f, 2.7f),
            end = Offset(100.123f, 200.456f),
        )
        original.addSignatureLine(line)

        val saved = saveState(original)!!
        val restored = SignatureState.Saver.restore(saved)!!

        val restoredLine = restored.signatureLines.first()
        assertEquals(1.5f, restoredLine.start.x)
        assertEquals(2.7f, restoredLine.start.y)
        assertEquals(100.123f, restoredLine.end.x)
        assertEquals(200.456f, restoredLine.end.y)
    }

    @Test
    fun clearAfterAddThenAddAgainWorks() {
        val state = SignatureState()
        state.addSignatureLine(SignatureLine(start = Offset(0f, 0f), end = Offset(1f, 1f)))
        state.clearSignatureLines()
        val newLine = SignatureLine(start = Offset(10f, 10f), end = Offset(20f, 20f))
        state.addSignatureLine(newLine)
        assertEquals(1, state.signatureLines.size)
        assertEquals(newLine, state.signatureLines.first())
    }
}
