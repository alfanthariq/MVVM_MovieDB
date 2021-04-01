package com.alfanthariq.mvvm_moviedb.utils

import android.app.Activity
import android.content.Context
import android.content.Intent

object AppRoute {
    fun open(context : Context, cls : Class<*>){
        val intent = Intent(context, cls)
        context.startActivity(intent)
    }

    fun open(context : Context, cls : Class<*>, param : HashMap<String, String>){
        val intent = Intent(context, cls)
        param.entries.forEach {
            intent.putExtra(it.key, it.value)
        }

        context.startActivity(intent)
    }

    fun open(context : Context, activity : Activity, cls : Class<*>, requestCode : Int){
        val intent = Intent(context, cls)
        activity.startActivityForResult(intent, requestCode)
    }

    fun open(context : Context, activity : Activity, cls : Class<*>, param : HashMap<String, String>, requestCode : Int){
        val intent = Intent(context, cls)
        intent.putExtra("parameter", param)

        activity.startActivityForResult(intent, requestCode)
    }
}