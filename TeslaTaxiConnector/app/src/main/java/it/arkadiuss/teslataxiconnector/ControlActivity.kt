package it.arkadiuss.teslataxiconnector

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView

class ControlActivity : AppCompatActivity() {
    private val forwardBtn by lazy {
        findViewById<Button>(R.id.forward_btn)
    }
    private val backwardBtn by lazy {
        findViewById<Button>(R.id.backward_btn)
    }
    private val leftBtn by lazy {
        findViewById<Button>(R.id.left_btn)
    }
    private val rightBtn by lazy {
        findViewById<Button>(R.id.right_btn)
    }
    private val connectionText by lazy {
        findViewById<TextView>(R.id.connection_text)
    }
    private val distanceText by lazy {
        findViewById<TextView>(R.id.distance_text)
    }
    private val refreshBtn by lazy {
        findViewById<Button>(R.id.refresh_btn)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)

        connectionText.text = CarConnectorService.connected.toString()
        refreshBtn.setOnClickListener {
            connectionText.text = CarConnectorService.connected.toString()
        }

        forwardBtn.setOnTouchListener { v: View,
                                        m: MotionEvent ->
            when (m.actionMasked) {
                MotionEvent.ACTION_DOWN -> CarConnectorService.sendTx('f')
                MotionEvent.ACTION_UP -> CarConnectorService.sendTx('s')
            }
            true
        }

        rightBtn.setOnClickListener {
            CarConnectorService.sendTx('r')
        }

        leftBtn.setOnClickListener {
            CarConnectorService.sendTx('l')
        }

        backwardBtn.setOnTouchListener { v: View,
                                         m: MotionEvent ->
            when (m.actionMasked) {
                MotionEvent.ACTION_DOWN -> CarConnectorService.sendTx('b')
                MotionEvent.ACTION_UP -> CarConnectorService.sendTx('s')
            }
            true
        }

        CarConnectorService.setOnReadListener { distance ->
            distanceText.text = "Distance: $distance"
        }
    }
}
