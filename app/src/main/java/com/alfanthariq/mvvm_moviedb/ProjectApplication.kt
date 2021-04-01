package com.alfanthariq.mvvm_moviedb

import android.app.Application
import com.alfanthariq.mvvm_moviedb.di.*

class ProjectApplication : Application() {
    companion object {
        lateinit var instance : ProjectApplication
        private set
    }
    lateinit var appComponent : AppComponent

    override fun onCreate() {
        super.onCreate()
        instance = this

        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .networkModule(NetworkModule(getString(R.string.default_base_url)))
            //.databaseModule(DatabaseModule(applicationContext))
            .build()
    }
}