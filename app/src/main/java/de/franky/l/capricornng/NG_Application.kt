package de.franky.l.capricornng

import android.app.Application
import android.content.Context
import android.net.wifi.WifiConfiguration
import java.nio.file.Files.size
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager



class NG_Application : Application() {

    init {
        instance = this
    }
    companion object {
        private var instance: NG_Application? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        // initialize for any
    }


}

