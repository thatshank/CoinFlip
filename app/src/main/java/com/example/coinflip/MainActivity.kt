package com.example.coinflip

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

class MainActivity : Activity() {

    // A short, bright "ding" generated in code, so no sound file is needed.
    private val sampleRate = 44100
    private val dingSamples: ShortArray by lazy {
        val n = (sampleRate * 0.6).toInt()
        ShortArray(n) { i ->
            val t = i / sampleRate.toDouble()
            val tone = sin(2 * PI * 2600 * t) + 0.4 * sin(2 * PI * 5200 * t)
            (tone * exp(-t * 8) * 9000).toInt().toShort()
        }
    }

    private fun playDing() {
        Thread {
            val bytes = dingSamples.size * 2
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bytes)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()
            track.write(dingSamples, 0, dingSamples.size)
            track.play()
            Thread.sleep(800)
            track.release()
        }.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val density = resources.displayMetrics.density
        val prefs = getSharedPreferences("coinflip", MODE_PRIVATE)
        var streak = prefs.getInt("streak", 0)

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

        val coinSize = (200 * density).toInt()
        val coin = TextView(this).apply {
            textSize = 72f
            gravity = Gravity.CENTER
            setTextColor(Color.rgb(90, 60, 0))
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.rgb(255, 193, 7))
                setStroke((6 * density).toInt(), Color.rgb(184, 134, 11))
            }
            cameraDistance = 8000f * density
            text = "?"
            layoutParams = LinearLayout.LayoutParams(coinSize, coinSize).apply {
                topMargin = (32 * density).toInt()
                bottomMargin = (32 * density).toInt()
            }
        }

        val flip = Button(this).apply {
            text = "Flip"
            textSize = 24f
        }

        flip.setOnClickListener {
            flip.isEnabled = false
            playDing()
            coin.text = ""
            val heads = Random.nextBoolean()
            coin.animate()
                .rotationXBy(1080f)
                .setDuration(900)
                .withEndAction {
                    coin.rotationX = 0f
                    if (heads) {
                        streak++
                        coin.text = "H"
                        coin.background = (coin.background as GradientDrawable).apply {
                            setColor(Color.rgb(255, 193, 7))
                        }
                    } else {
                        streak = 0
                        coin.text = "T"
                        coin.background = (coin.background as GradientDrawable).apply {
                            setColor(Color.rgb(176, 190, 197))
                        }
                    }
                    count.text = streak.toString()
                    prefs.edit().putInt("streak", streak).apply()
                    flip.isEnabled = true
                }
                .start()
        }

        setContentView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(48, 48, 48, 48)
            addView(count)
            addView(label)
            addView(coin)
            addView(flip)
        })
    }
}
