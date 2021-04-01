package com.alfanthariq.mvvm_moviedb.utils

import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener

abstract class AppBarStateChangedListener : OnOffsetChangedListener {
    enum class State {
        EXPANDED, COLLAPSED, IDLE
    }

    var mCurrentState = State.IDLE

    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        if (verticalOffset == 0) {
            setCurrentStateAndNotify(appBarLayout, State.EXPANDED)
        } else if (Math.abs(verticalOffset) >= appBarLayout.totalScrollRange) {
            setCurrentStateAndNotify(appBarLayout, State.COLLAPSED)
        } else {
            setCurrentStateAndNotify(appBarLayout, State.IDLE)
        }
    }

    fun setCurrentStateAndNotify(
        appBarLayout: AppBarLayout,
        state: State
    ) {
        if (mCurrentState != state) {
            onStateChanged(appBarLayout, state)
        }
        mCurrentState = state
    }

    abstract fun onStateChanged(
        appBarLayout: AppBarLayout,
        state: State
    )
}