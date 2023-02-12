package com.joelkanyi.composesignature

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.joelkanyi.composesignature.ui.theme.ComposeSignatureTheme
import com.joelkanyi.composesignature.ui.theme.Pink40

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeSignatureTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var imageBitmap: ImageBitmap? by remember {
                        mutableStateOf(null)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        ComposeSignature(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            signaturePadColor = Color(0xFFEEEEEE),
                            signaturePadHeight = 400.dp,
                            signatureColor = Color.Black,
                            signatureThickness = 10f,
                            onComplete = { signatureBitmap ->
                                imageBitmap = signatureBitmap.asImageBitmap()
                            }
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        if (imageBitmap != null) {
                            Image(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .size(200.dp),
                                bitmap = imageBitmap!!,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeSignatureTheme {
        Column(Modifier.fillMaxSize()) {
            ComposeSignature(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                signaturePadColor = Color(0xFFEEEEEE),
                signaturePadHeight = 500.dp,
                signatureColor = Color.Black,
                signatureThickness = 10f,
                onComplete = {

                }
            )
        }
    }
}