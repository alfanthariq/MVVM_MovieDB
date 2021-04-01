package com.alfanthariq.mvvm_moviedb.base

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.viewbinding.ViewBinding
import com.alfanthariq.mvvm_moviedb.R
import io.github.inflationx.viewpump.ViewPumpContextWrapper

abstract class BaseActivity<VM : BaseViewModel> : AppCompatActivity(), BaseView {
    abstract fun nightMode() : Int

    abstract var viewModel : VM
    private var pDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        AppCompatDelegate.setDefaultNightMode(
            nightMode()
        )

        super.onCreate(savedInstanceState, persistentState)
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase!!))
    }

    override fun showLoadingDialog(message: String?) {
        if (pDialog == null) pDialog = Dialog(this)

        pDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        pDialog?.setContentView(R.layout.dialog_loading)
        pDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val msg = pDialog?.findViewById(R.id.txt_message) as TextView
        if (message != null) msg.text = message
        else msg.text = getString(R.string.loading)
        pDialog?.setCancelable(false)
        pDialog?.show()
    }

    override fun hideLoadingDialog() {
        if (pDialog != null && pDialog?.isShowing!!) {
            pDialog?.dismiss(); pDialog = null
        }
    }

    override fun setToolbar(
        mToolbar: Toolbar?,
        title: String?,
        setDisplayHomeAsUpEnabled: Boolean
    ) {
        setSupportActionBar(mToolbar)
        supportActionBar?.setDefaultDisplayHomeAsUpEnabled(setDisplayHomeAsUpEnabled)
    }

    inline fun <T : ViewBinding> AppCompatActivity.viewBinding(
        crossinline bindingInflater: (LayoutInflater) -> T) =
        lazy(LazyThreadSafetyMode.NONE) {
            bindingInflater.invoke(layoutInflater)
        }
}