package it.arkadiuss.teslataxiconnector

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import kotlin.math.abs

class TaxiControlActivity : AppCompatActivity() {
    private val VELOCITY = 1 //meters per second
    private var lastDistance: Array<Int> = Array(3) { 100 }

    private val goBtn by lazy {
        findViewById<Button>(R.id.go_btn)
    }
    private val leftRightText by lazy {
        findViewById<EditText>(R.id.left_right_text)
    }
    private val forwardText by lazy {
        findViewById<EditText>(R.id.forward_text)
    }
    private val bypassCheckbox by lazy {
        findViewById<CheckBox>(R.id.bypass_checkbox)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_taxi_control)

        goBtn.setOnClickListener {
            if(forwardText.text.isNotEmpty() && leftRightText.text.isNotEmpty()) {
                val forward = forwardText.text.toString().toDouble()
                val leftRight = leftRightText.text.toString().toDouble()
                go(forward, leftRight, bypassCheckbox.isChecked)
            }
        }

        CarConnectorService.setOnReadListener {
            Log.d(TaxiControlActivity::class.java.simpleName, "Distance $it")
            lastDistance.shiftLeft(it ?: 0)
        }
    }

    private fun go(forward: Double, leftRight: Double, bypass: Boolean) {
        if(!goForward(forward, bypass)) return
        if(leftRight != 0.0) {
            if (leftRight < 0) turnLeft() else turnRight()
            goForward(abs(leftRight), bypass)
        }
    }

    private fun goForward(meters: Double, bypass: Boolean): Boolean {
        val time = (meters / VELOCITY * 1000.0).toLong()
        return forward(time, bypass)
    }

    private fun turnRight() {
        turn(Move.RIGHT)
    }

    private fun turnLeft() {
        turn(Move.LEFT)
    }

    private fun forward(time: Long, bypassAllowed: Boolean = true): Boolean {
        CarConnectorService.sendTxSynchronously(Move.FORWARD)
        var timeLeft = time
        val step = 10L
        var done = true
        while(timeLeft > 0) {
            // TODO: Kill sleep with fire
            Thread.sleep(step)
            timeLeft -= step
            if(lastDistance.all { it < 60 }) {
                if(bypassAllowed && !bypass()) done = false
                else if(!bypassAllowed) {
                    stopEverything()
                    done = false
                }
            }
        }
        CarConnectorService.sendTxSynchronously(Move.STOP)
        return done
    }

    private fun turn(d: Move) {
        CarConnectorService.sendTxSynchronously(d)
        val time = (1.85 / VELOCITY * 1000.0).toLong()
        forward(time, false)
        CarConnectorService.sendTxSynchronously(Move.STOP_TURN)
    }

    private fun stopEverything() {
        CarConnectorService.sendTxSynchronously(Move.STOP_TURN)
        CarConnectorService.sendTxSynchronously(Move.STOP)
    }

    private fun bypass(): Boolean {
        val seq = listOf(Move.RIGHT,Move.LEFT,Move.LEFT,Move.RIGHT)
        val step = (1.85 / 3 / VELOCITY * 1000.0).toLong()
        for(d in seq) {
            CarConnectorService.sendTxSynchronously(d)
            // TODO: Kill sleep with fire
            Thread.sleep(step)
        }
        CarConnectorService.sendTxSynchronously(Move.STOP_TURN)
        return true
    }
}
