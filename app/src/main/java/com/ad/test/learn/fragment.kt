package com.ad.test.learn

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ad.test.R
import kotlin.concurrent.thread

class YourFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.your_fragment_layout, container, false)
}

class DefaultFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.default_fragment_layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<Button>(R.id.button).setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    YourFragment()
                )
                .addToBackStack(null)
                .commit()
        }
    }
}

class ViewActivity : AppCompatActivity() {

    private val colors = arrayOf(Color.RED, Color.GREEN, Color.BLUE)
    private var color = colors[0]
    private val handler = Handler(Looper.getMainLooper())
    private val updateLight: Runnable = object : Runnable {
        override fun run() {
            color = colors[(colors.indexOf(color) + 1) % colors.size]
            window.decorView.setBackgroundColor(color)
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)

        findViewById<Button>(R.id.change_color_btn)
            .setOnClickListener {
                handler.removeCallbacks(updateLight)
                handler.post(updateLight)
            }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, DefaultFragment())
                .commit()
        }

        val button = findViewById<Button>(R.id.button2)
        val counterTextView = findViewById<TextView>(R.id.textView4)

        val sb = StringBuilder()
        button.setOnClickListener {
            button.isEnabled = false
            sb.setLength(0)
            thread {
                for (char in 'a'..'z') {
                    val string = sb.append(char).toString()
                    handler.post {
                        counterTextView.text = string
                        if (char == 'z')
                            button.isEnabled = true
                    }
                    Thread.sleep(100) // let's pretend we're doing some work
                }
            }
        }

        val receiver: BroadcastReceiver = YourBroadcastReceiver()
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
        }
        applicationContext.registerReceiver(receiver, filter)
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(updateLight)
    }
}