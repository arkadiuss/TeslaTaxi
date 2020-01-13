package it.arkadiuss.teslataxiconnector

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

        forwardBtn.setOnPressing({
            CarConnectorService.sendTx('f')
        }, {
            CarConnectorService.sendTx('s')
        })

        rightBtn.setOnPressing({
            CarConnectorService.sendTx('r')
        }, {
            CarConnectorService.sendTx('w')
        })

        leftBtn.setOnPressing({
            CarConnectorService.sendTx('l')
        }, {
            CarConnectorService.sendTx('w')
        })

        backwardBtn.setOnPressing({
            CarConnectorService.sendTx('b')
        }, {
            CarConnectorService.sendTx('s')
        })

        CarConnectorService.setOnReadListener { distance ->
            runOnUiThread {
                Log.d(ControlActivity::class.java.simpleName, "Distance: $distance")
                distanceText.setText("Distance: $distance")
            }
        }
    }
}
