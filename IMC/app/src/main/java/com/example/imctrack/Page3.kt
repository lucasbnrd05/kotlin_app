package com.example.imctrack

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import androidx.appcompat.widget.Toolbar

class Page3 : AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.page3)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.page3)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configurer la Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val buttonNext: Button = findViewById(R.id.gauche)
         buttonNext.setOnClickListener {
             val intent = Intent(this, Page2::class.java)
             startActivity(intent)
         }
    }
}