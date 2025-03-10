package com.example.imctrack

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class Page3 : AppCompatActivity() {

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var lineChart: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.page3)
        sharedPreferencesHelper = SharedPreferencesHelper(this)
        setAppropriateTheme()

        val lineChart: LineChart = findViewById(R.id.lineChart)
        setupLineChart(lineChart)

        /*val clearDataButton: Button = findViewById(R.id.clearDataButton)
        clearDataButton.setOnClickListener {
            clearGraphData()
        }*/

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.page3)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configurer la Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Configurer la Bottom Navigation
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_previous -> {
                    // Naviguer vers Page2
                    val intent = Intent(this, Page2::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_home -> {
                    // Naviguer vers MainActivity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_clear_data -> {
                    clearGraphData()
                    true
                }
                else -> false
            }
        }

    }

    private fun setupLineChart(lineChart: LineChart) {

        /*val sharedPrefs = getSharedPreferences("IMC_TRACKER", MODE_PRIVATE)
        val heightListString = sharedPrefs.getString("HEIGHT_LIST", "") ?: ""
        val datesStr = sharedPrefs.getString("DATE_LIST", "") ?: ""
        // Transformer la chaîne en liste de valeurs float
        val heightValues = heightListString.split(",").mapNotNull { it.toFloatOrNull() }

        // Vérifier si on a des valeurs
        if (heightValues.isEmpty()) {
            lineChart.clear()
            return
        }

        // Générer les entrées pour le graphique
        val entries = heightValues.mapIndexed { index, height ->
            Entry(index.toFloat() + 1, height) // X = jour, Y = taille
        }*/

        /*val entries = listOf(
            Entry(1f, 50f),  // Jour 1, 50 cm
            Entry(2f, 52f),  // Jour 2, 52 cm
            Entry(3f, 55f),  // Jour 3, 55 cm
            Entry(4f, 57f),  // Jour 4, 57 cm
            Entry(5f, 60f)   // Jour 5, 60 cm
        )*/

        /*val dataSet = LineDataSet(entries, "Taille en cm").apply {
            color = getColor(android.R.color.holo_blue_dark)
            valueTextColor = getColor(android.R.color.black)
            lineWidth = 2f
            circleRadius = 4f
        }
        val yAxis = lineChart.axisLeft
        yAxis.textColor = getColor(android.R.color.black)
        yAxis.textSize = 12f
        yAxis.setDrawGridLines(true)
        yAxis.setDrawAxisLine(true)
        //lineChart.axisRight.isEnabled = false

        lineChart.data = LineData(dataSet)
        lineChart.invalidate() */

        val sharedPrefs = getSharedPreferences("IMC_TRACKER", MODE_PRIVATE)
        val heightsString = sharedPrefs.getString("HEIGHT_LIST", "") ?: ""
        val weightsString = sharedPrefs.getString("WEIGHT_LIST", "") ?: ""
        val bmiString = sharedPrefs.getString("BMI_LIST", "") ?: ""
        val datesString = sharedPrefs.getString("DATE_LIST", "") ?: ""

        if (heightsString.isEmpty() || weightsString.isEmpty() || bmiString.isEmpty() || datesString.isEmpty()) return

        val heights = heightsString.split(",").map { it.toFloat() }
        val weights = weightsString.split(",").map { it.toFloat() }
        val bmiValues = bmiString.split(",").map { it.toFloat() }
        val dates = datesString.split(",")

        // Créer les entrées pour chaque courbe
        val heightEntries = heights.mapIndexed { index, height -> Entry(index.toFloat(), height) }
        val weightEntries = weights.mapIndexed { index, weight -> Entry(index.toFloat(), weight) }
        val bmiEntries = bmiValues.mapIndexed { index, bmi -> Entry(index.toFloat(), bmi) }

        // Créer les datasets pour chaque courbe
        val heightDataSet = LineDataSet(heightEntries, "Height (cm)").apply {
            color = getColor(android.R.color.holo_blue_dark)
            valueTextColor = getColor(android.R.color.black)
            lineWidth = 2f
            circleRadius = 4f
        }

        val weightDataSet = LineDataSet(weightEntries, "Weight (kg)").apply {
            color = getColor(android.R.color.holo_green_dark)
            valueTextColor = getColor(android.R.color.black)
            lineWidth = 2f
            circleRadius = 4f
        }

        val bmiDataSet = LineDataSet(bmiEntries, "BMI").apply {
            color = getColor(android.R.color.holo_orange_dark)
            valueTextColor = getColor(android.R.color.black)
            lineWidth = 2f
            circleRadius = 4f
        }

        // Créer la LineData avec les trois datasets
        val lineData = LineData(heightDataSet, weightDataSet, bmiDataSet)

        // Appliquer la LineData au LineChart
        lineChart.data = lineData


        lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(dates)
        lineChart.xAxis.granularity = 1f
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM


        lineChart.axisRight.isEnabled = false

        lineChart.description = Description().apply { text = "" }
        lineChart.invalidate()
    }

    private fun clearGraphData() {

        val sharedPrefs = getSharedPreferences("IMC_TRACKER", MODE_PRIVATE)
        sharedPrefs.edit().remove("HEIGHT_LIST").apply()
        sharedPrefs.edit().remove("WEIGHT_LIST").apply()
        sharedPrefs.edit().remove("BMI_LIST").apply()

        Toast.makeText(this, "Data reset !", Toast.LENGTH_SHORT).show()

        finish()
        startActivity(intent)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_theme -> {
                toggleTheme()
                true
            }
            R.id.nav_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setAppropriateTheme() {
        val isDarkMode = sharedPreferencesHelper.getTheme()
        AppCompatDelegate.setDefaultNightMode(if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun toggleTheme() {
        val isDarkMode = sharedPreferencesHelper.getTheme()
        sharedPreferencesHelper.saveTheme(!isDarkMode)
        setAppropriateTheme()
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val themeItem = menu?.findItem(R.id.action_theme)
        val isDarkMode = sharedPreferencesHelper.getTheme()

        themeItem?.setIcon(
            if (isDarkMode) R.drawable.ic_sun else R.drawable.ic_moon
        )

        return super.onPrepareOptionsMenu(menu)
    }
}
