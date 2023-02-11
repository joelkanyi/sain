package com.joelkanyi.composesignature

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class SignaturePadViewModel : ViewModel() {

    private val _path = mutableListOf<PathState>()
    val path: MutableList<PathState> = _path

    fun setPathState(value: PathState) {
        _path.add(value)
    }
}