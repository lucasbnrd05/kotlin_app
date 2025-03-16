package com.example.imctrack

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject


class Page4 : AppCompatActivity() {

    private lateinit var resultText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page4)

        resultText = findViewById(R.id.resultText)
        val scanButton: Button = findViewById(R.id.scanButton)

        scanButton.setOnClickListener {
            scanCode()
        }

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_previous -> {
                    finish()
                    true
                }
                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun scanCode() {
        val options = ScanOptions()
        options.setPrompt("Scan your product.")
        options.setBeepEnabled(true)
        options.setOrientationLocked(false)
        //options.setCaptureActivity(CaptureActivity::class.java)
        options.setCaptureActivity(CustomScannerActivity::class.java)
        barcodeLauncher.launch(options)
    }

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            resultText.text = "BarCode : ${result.contents}"
            fetchProductInfo(result.contents)
        }
    }

    private fun fetchProductInfo(barcode: String) {
        val url = "https://world.openfoodfacts.org/api/v0/product/$barcode.json"
        val client = OkHttpClient()

        val request = Request.Builder().url(url).build()

        Thread {
            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                if (responseBody.isNullOrEmpty()) throw Exception("No data received")

                val jsonResponse = JSONObject(responseBody)
                val product = jsonResponse.optJSONObject("product") ?: throw Exception("Product not found")

                val productName = product.optString("product_name", "Unknown")
                val brand = product.optString("brands", "Unknown brand")
                val nutriscore = product.optString("nutriscore_grade", "N/A").uppercase()
                val ingredients = product.optString("ingredients_text", "No ingredients info")
                val imageUrl = product.optString("image_url", "")

                val isUnhealthy = checkUnhealthyProduct(nutriscore, ingredients)

                runOnUiThread {
                    resultText.text = "📌 Product: $productName\n🏭 Brand: $brand\n🔠 NutriScore: $nutriscore\n🍽 Ingredients: $ingredients"

                    if (imageUrl.isNotEmpty()) {
                        val imageView: ImageView = findViewById(R.id.productImage)
                        Glide.with(this@Page4).load(imageUrl).into(imageView)
                    }

                    if (isUnhealthy) {
                        showWarningMessage(nutriscore)
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    resultText.text = "Error: ${e.message}"
                }
            }
        }.start()
    }

    private fun checkUnhealthyProduct(nutriscore: String, ingredients: String): Boolean {
        val unhealthyKeywords = listOf(
            "sucre", "huile de palme", "sirop de glucose", "graisses saturées", "sel",
            "sugar", "palm oil", "glucose syrup", "saturated fat", "salt",
            "zucker", "palmenöl", "glukosesirup", "gesättigte fette", "salz",
            "azúcar", "aceite de palma", "jarabe de glucosa", "grasas saturadas", "sal"
        )
        if (nutriscore == "D" || nutriscore == "E") {
            return true
        }
        for (keyword in unhealthyKeywords) {
            if (ingredients.contains(keyword, ignoreCase = true)) {
                return true
            }
        }

        return false
    }

    private fun showWarningMessage(nutriscore: String) {
        val message = when (nutriscore) {
            "D", "E" -> "⚠️ Warning! This product is not good for your sporting goals. It contains too much sugar or bad fats."
            else -> "✅ This product seems okay, but watch out for excess."
        }

        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Nutritional Alert")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()
        alertDialog.show()
    }

}
