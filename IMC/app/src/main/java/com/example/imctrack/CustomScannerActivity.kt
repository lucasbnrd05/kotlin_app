package com.example.imctrack

import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import com.journeyapps.barcodescanner.CaptureActivity

class CustomScannerActivity : CaptureActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val backButton = Button(this).apply {
            text = "Back to app"
            textSize = 16f
            setPadding(20, 20, 20, 20)
            setOnClickListener {
                finish()
            }
        }

        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(30, 50, 0, 0)
        }

        addContentView(backButton, params)
    }
}
