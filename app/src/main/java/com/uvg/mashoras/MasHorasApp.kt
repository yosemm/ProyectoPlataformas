package com.uvg.mashoras

import android.app.Application
import com.uvg.mashoras.data.AppContainer

class MasHorasApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

