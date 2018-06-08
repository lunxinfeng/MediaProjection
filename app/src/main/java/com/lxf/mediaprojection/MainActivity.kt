package com.lxf.mediaprojection

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.lxf.capture.Capture
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Capture.init(application)
        capture.setOnClickListener {
            Capture.startCapture(this) {
                println("MainActivity:${it.width};${it.height}")
            }
        }
    }
}
