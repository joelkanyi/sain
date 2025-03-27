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
package com.composesignature.sample

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import io.github.joelkanyi.sain.Sain
import io.github.joelkanyi.sain.SignatureAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Sample(
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {
                            // Handle navigation back
                        },
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                title = {
                    Text("Compose Signature")
                },
            )
        },
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            var imageBitmap by remember {
                mutableStateOf<ImageBitmap?>(null)
            }

            Column(
                modifier = Modifier
                    .scrollable(rememberScrollState(), orientation = Orientation.Horizontal)
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "By signing you agree to our terms and conditions. Please sign below.",
                    style = MaterialTheme.typography.bodyMedium,
                )

                Sain(
                    signatureHeight = 250.dp,
                    signaturePadColor = Color.White,
                    signatureBorderStroke = BorderStroke(
                        width = .5.dp,
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                    signaturePadShape = RoundedCornerShape(8.dp),
                    onComplete = { signatureBitmap ->
                        if (signatureBitmap != null) {
                            imageBitmap = signatureBitmap
                        } else {
                            println("Signature is empty")
                        }
                    },
                ) { action ->
                    Row(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                imageBitmap = null
                                action(SignatureAction.CLEAR)
                            },
                        ) {
                            Text("Clear")
                        }
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                action(SignatureAction.COMPLETE)
                            },
                        ) {
                            Text("Complete")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                imageBitmap?.let {
                    Image(
                        modifier = Modifier,
                        bitmap = imageBitmap!!,
                        contentDescription = null,
                    )
                }
            }
        }
    }
}
