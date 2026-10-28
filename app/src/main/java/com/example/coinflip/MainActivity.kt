package com.example.coinflip

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import kotlin.random.Random

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("coinflip", MODE_PRIVATE)
        var streak = prefs.getInt("streak", 0)

        val result = TextView(this).apply {
            textSize = 28f
            gravity = Gravity.CENTER
            setTextColor(Color.DKGRAY)
        }
        val count = TextView(this).apply {
            textSize = 96f
            gravity = Gravity.CENTER
            text = streak.toString()
        }
        val label = TextView(this).apply {
            text = "heads in a row"
            textSize = 18f
            gravity = Gravity.CENTER
        }
        val flip = Button(this).apply {
            text = "Flip"
            textSize = 24f
            setOnClickListener {
                if (Random.nextBoolean()) {
                    streak++
                    result.text = "Heads"
                } else {
                    streak = 0
                    result.text = "Tails"
                }
                count.text = streak.toString()
                prefs.edit().putInt("streak", streak).apply()
            }
        }

        setContentView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(48, 48, 48, 48)
            addView(count)
            addView(label)
            addView(result)
            addView(flip)
        })
    }
}
