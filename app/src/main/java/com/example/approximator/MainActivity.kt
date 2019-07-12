package com.example.approximator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val coordinateSystem = CoordinateSystem(this)
        coordinateSystem.systemUiVisibility = SYSTEM_UI_FLAG_FULLSCREEN
        setContentView(coordinateSystem)

    }
}
