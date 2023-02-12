package com.joelkanyi.composesignature

import android.annotation.SuppressLint
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class SignaturePadViewModel : ViewModel() {

    @SuppressLint("MutableCollectionMutableState")
    private val _path = mutableStateOf(mutableListOf<PathState>())
    val path: State<MutableList<PathState>> = _path

    fun setPathState(value: PathState) {
        _path.value.add(value)
    }

    fun clearPathState() {
        _path.value = mutableListOf()
    }
}