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

import io.github.joelkanyi.sain.SignatureAction.CLEAR
import io.github.joelkanyi.sain.SignatureAction.COMPLETE

/**
 * SignatureAction is an enum class that represents the actions that can be performed on a signature.
 * [CLEAR] clears the signature. You can take advantage here and clear the signature from your implementation size.
 * [COMPLETE] completes the signature. Once the signature is completed, you receive an ImageBitmap of the signature.
 **/
public enum class SignatureAction {
    CLEAR,
    COMPLETE,
}
