package com.alfanthariq.mvvm_moviedb.di

import android.app.Application
import com.alfanthariq.mvvm_moviedb.R
import dagger.Module
import dagger.Provides
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import javax.inject.Singleton

@Module
class AppModule(val apps : Application) {
    @Provides
    @Singleton
    fun provideApplication() : Application {
        ViewPump.init(
            ViewPump.builder()
                .addInterceptor(
                    CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                            .setDefaultFontPath("fonts/FallingSky.otf")
                            .setFontAttrId(R.attr.fontPath)
                            .build()
                    )
                )
                .build()
        )

        return apps
    }
}