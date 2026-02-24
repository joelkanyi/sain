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

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SignatureActionTest {

    @Test
    fun enumHasExactlyTwoValues() {
        assertEquals(2, SignatureAction.entries.size)
    }

    @Test
    fun valueOfClear() {
        assertEquals(SignatureAction.CLEAR, SignatureAction.valueOf("CLEAR"))
    }

    @Test
    fun valueOfComplete() {
        assertEquals(SignatureAction.COMPLETE, SignatureAction.valueOf("COMPLETE"))
    }

    @Test
    fun entriesContainsBothValues() {
        assertTrue(SignatureAction.entries.contains(SignatureAction.CLEAR))
        assertTrue(SignatureAction.entries.contains(SignatureAction.COMPLETE))
    }

    @Test
    fun ordinalValuesAreStable() {
        assertEquals(0, SignatureAction.CLEAR.ordinal)
        assertEquals(1, SignatureAction.COMPLETE.ordinal)
    }
}
