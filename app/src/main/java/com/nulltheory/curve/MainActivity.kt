package com.nulltheory.curve

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import org.json.JSONArray
import java.net.URL

class MainActivity : AppCompatActivity() {
    lateinit var chart: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        chart = findViewById(R.id.chart)
        renderChart()
    }

    private fun getJSON(): String {
        var json = "[]"

        val t = Thread {
            try {
                json = URL(BuildConfig.api).readText()
            } catch (e: Exception) {
                Log.d("api", e.toString())
            }
        }
        t.start()
        t.join()

        return json
    }

    private fun renderChart() {
        val entries = ArrayList<Entry>()
        val yields = JSONArray(getJSON())

        for(i in 0 until yields.length()) {
            val item = yields.getJSONObject(i)
            entries.add(Entry(
                item.optLong("expiration").toFloat(),
                item.optDouble("yield").toFloat()))
        }

        val dataSet = LineDataSet(entries, "yields")
        chart.data = LineData(dataSet)
        chart.invalidate()
    }
}