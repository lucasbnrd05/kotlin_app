package com.example.imctrack
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Page2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page2)

        // Views
        val heightEditText: EditText = findViewById(R.id.editHeight)
        val weightEditText: EditText = findViewById(R.id.editWeight)
        val calculateButton: Button = findViewById(R.id.calculateBMI)
        val resultTextView: TextView = findViewById(R.id.resultBMI)

        // Button click to calculate BMI
        calculateButton.setOnClickListener {
            // Get the height and weight values from the user
            val heightStr = heightEditText.text.toString()
            val weightStr = weightEditText.text.toString()

            // Validate inputs
            if (heightStr.isEmpty() || weightStr.isEmpty()) {
                Toast.makeText(this, "Please enter both height and weight", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Convert height and weight to float
            val height = heightStr.toFloat()
            val weight = weightStr.toFloat()

            // Calculate BMI (height is in cm, so convert to meters)
            val heightInMeters = height / 100
            val bmi = weight / (heightInMeters * heightInMeters)

            // Display BMI result
            resultTextView.text = "Your BMI is: %.2f".format(bmi)
        }
    }
}
