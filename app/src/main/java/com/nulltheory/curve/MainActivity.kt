package com.nulltheory.curve

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
        val yields = JSONArray(getJSON())

        for(i in 0 until yields.length()) {
            val item = yields.getJSONObject(i)
            val ts = item.optLong("expiration").toLong()
            val y = item.optDouble("yield").toDouble() * 100
            Log.d("api", "%d - %.2f%%".format(ts, y))
        }
    }
}