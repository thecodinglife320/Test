package com.ad.test.learn

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ad.test.R
import kotlin.concurrent.thread

class YourFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.your_fragment_layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(YourFragment::class.simpleName, "${arguments?.getString("key")}")

        view.findViewById<TextView>(R.id.textView2)
            ?.setOnClickListener {
                parentFragmentManager.setFragmentResult(
                    "requestKey",
                    bundleOf(
                        "name" to "Pham Ba Dat",
                    )
                )
                parentFragmentManager.popBackStack()
            }
    }
}

class DefaultFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentFragmentManager.setFragmentResultListener("requestKey", this) { _, bundle ->
            requireView()
                .findViewById<TextView>(R.id.textView)
                ?.text = bundle.getString("name")
        }
    }

    interface FragmentListener {
        fun doSomething()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as FragmentListener
        Log.d(this::class.simpleName, "attach")
    }

    private var callback: FragmentListener? = null

    override fun onDetach() {
        super.onDetach()
        callback = null
        Log.d(this::class.simpleName, "detach")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.default_fragment_layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<Button>(R.id.button).setOnClickListener {
//            requireActivity().supportFragmentManager.beginTransaction()
//                .replace(
//                    R.id.fragment_container,
//                    YourFragment()
//                )
//                .addToBackStack(null)
//                .commit()
            callback?.doSomething()
            findNavController().navigate(
                R.id.action_default_to_your,
                bundleOf("key" to "Hello my friend")
            )
        }
    }
}

class ViewActivity : AppCompatActivity(), DefaultFragment.FragmentListener {

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

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)

        findViewById<Button>(R.id.change_color_btn)
            ?.setOnClickListener {
                handler.removeCallbacks(updateLight)
                handler.post(updateLight)
            }

//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction()
//                .add(R.id.fragment_container, DefaultFragment())
//                .commit()
//        }

        val button = findViewById<Button?>(R.id.button2)
        val counterTextView = findViewById<TextView?>(R.id.textView4)

        val sb = StringBuilder()
        button?.setOnClickListener {
            button.isEnabled = false
            sb.setLength(0)
            thread {
                for (char in 'a'..'z') {
                    val string = sb.append(char).toString()
                    handler.post {
                        counterTextView?.text = string
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

        supportFragmentManager.setFragmentResultListener("requestKey", this) { _, bundle ->
            Log.d(this::class.simpleName, bundle.getString("key", "no data"))
        }

//        findViewById<ViewPager2>(R.id.view_pager)?.let {
//            it.adapter = object : FragmentStateAdapter(supportFragmentManager, lifecycle) {
//                override fun createFragment(position: Int) = when (position) {
//                    0 -> DefaultFragment()
//                    1 -> YourFragment()
//                    2 -> MainFragment()
//                    else -> throw AssertionError()
//                }
//
//                override fun getItemCount() = 3
//            }
//        }
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(updateLight)
    }

    override fun doSomething() {
        Log.d(ViewActivity::class.simpleName, "Do something")
    }
}