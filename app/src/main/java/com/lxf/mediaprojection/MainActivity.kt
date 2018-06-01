package com.lxf.mediaprojection

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.lxf.capture.Capture
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        capture.setOnClickListener {
            Capture(this@MainActivity).startCapture()
        }
    }
}
