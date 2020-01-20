package it.arkadiuss.teslataxiconnector

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
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
    private val velocityText by lazy {
        findViewById<EditText>(R.id.velocity_text)
    }
    private val setVelocityBtn by lazy {
        findViewById<Button>(R.id.set_velocity_btn)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)

        connectionText.text = CarConnectorService.connected.toString()
        refreshBtn.setOnClickListener {
            connectionText.text = CarConnectorService.connected.toString()
        }
        setVelocityBtn.setOnClickListener {
            val v = velocityText.text.toString().toInt()
            CarConnectorService.sendTxSynchronously("v${v}".toByteArray())
        }

        forwardBtn.setOnPressing({
            CarConnectorService.sendTx(Move.FORWARD)
        }, {
            CarConnectorService.sendTx(Move.STOP)
        })

        rightBtn.setOnPressing({
            CarConnectorService.sendTx(Move.RIGHT)
        }, {
            CarConnectorService.sendTx(Move.STOP_TURN)
        })

        leftBtn.setOnPressing({
            CarConnectorService.sendTx(Move.LEFT)
        }, {
            CarConnectorService.sendTx(Move.STOP_TURN)
        })

        backwardBtn.setOnPressing({
            CarConnectorService.sendTx(Move.BACKWARD)
        }, {
            CarConnectorService.sendTx(Move.STOP)
        })

        CarConnectorService.setOnReadListener { distance ->
            runOnUiThread {
                Log.d(ControlActivity::class.java.simpleName, "Distance: $distance")
                distanceText.setText("Distance: $distance")
            }
        }
    }
}
