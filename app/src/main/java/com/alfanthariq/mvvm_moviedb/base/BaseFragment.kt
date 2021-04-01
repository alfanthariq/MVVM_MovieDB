package com.alfanthariq.mvvm_moviedb.base

import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VM : BaseViewModel> : DialogFragment() {

    abstract var viewModel : VM

    inline fun <T : ViewBinding> Fragment.viewBinding(
        crossinline bindingInflater: (LayoutInflater) -> T) =
        lazy(LazyThreadSafetyMode.NONE) {
            bindingInflater.invoke(layoutInflater)
        }
}