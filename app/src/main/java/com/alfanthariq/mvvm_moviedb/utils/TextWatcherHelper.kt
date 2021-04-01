package com.alfanthariq.mvvm_moviedb.utils

import android.text.Editable
import android.text.TextWatcher

class TextWatcherHelper : TextWatcher {

    private var listener : TextWatcherListener? = null

    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

    override fun afterTextChanged(editable: Editable) {
        listener?.onAfterChange(editable)
    }

    interface TextWatcherListener {
        fun onAfterChange(editable: Editable)
    }

    fun setListener(_listener: TextWatcherListener){
        listener = _listener
    }
}