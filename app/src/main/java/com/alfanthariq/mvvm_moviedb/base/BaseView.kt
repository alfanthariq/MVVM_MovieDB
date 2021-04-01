package com.alfanthariq.mvvm_moviedb.base

import androidx.appcompat.widget.Toolbar

interface BaseView {
    fun showLoadingDialog(message: String?)

    fun hideLoadingDialog()

    fun setToolbar(mToolbar: Toolbar?, title: String?, setDisplayHomeAsUpEnabled: Boolean)
}