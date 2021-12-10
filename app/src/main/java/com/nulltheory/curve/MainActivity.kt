package com.nulltheory.curve

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import org.json.JSONArray
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    lateinit var chart: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        chart = findViewById(R.id.chart)
        chart.legend.isEnabled = false
        chart.description.isEnabled = false
        chart.axisRight.isEnabled = false
        chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                val dataSet = chart.data.dataSets[0]

                var colours = IntArray(dataSet.entryCount) { Color.TRANSPARENT }
                colours[dataSet.getEntryIndex(e)] = Color.WHITE

                dataSet.setValueTextColors(colours.toMutableList())
            }

            override fun onNothingSelected() {
                chart.data.setValueTextColor(Color.TRANSPARENT)
            }
        })

        val xAxis = chart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.textColor = Color.WHITE
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                val formatter = SimpleDateFormat("MMM yyyy")
                return formatter.format(Date(value.toLong()))
            }
        }

        val yAxis = chart.axisLeft
        yAxis.setDrawGridLines(false)
        yAxis.setDrawAxisLine(false)
        yAxis.textColor = Color.WHITE
        yAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return "%.2f%%".format(value * 100)
            }
        }

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
        dataSet.color = Color.WHITE
        dataSet.setCircleColor(Color.WHITE)
        dataSet.valueTextColor = Color.TRANSPARENT
        dataSet.valueTextSize = 10f
        dataSet.setDrawHighlightIndicators(false)
        dataSet.valueFormatter = object : ValueFormatter() {
            override fun getPointLabel(entry: Entry?): String {
                val formatter = SimpleDateFormat("dd MMM yyyy")
                val date = formatter.format(Date(entry!!.x.toLong()))
                val yld = "%.2f%%".format(entry.y * 100)
                return "%s %s".format(yld, date)
            }
        }

        chart.data = LineData(dataSet)
        chart.invalidate()
    }
}