package com.example.readium

import android.app.Application
import com.example.readium.firebase.FirebaseConfig

class ReadiumApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        //inicializa Firebase
        FirebaseConfig.initialize(this)
    }
}