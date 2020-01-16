package it.arkadiuss.teslataxiconnector

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.core.text.isDigitsOnly
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_taxi_control)

        goBtn.setOnClickListener {
            if(forwardText.text.isNotEmpty() && leftRightText.text.isNotEmpty()) {
                val forward = forwardText.text.toString().toDouble()
                val leftRight = leftRightText.text.toString().toDouble()
                go(forward, leftRight)
            }
        }

        CarConnectorService.setOnReadListener {
            Log.d(TaxiControlActivity::class.java.simpleName, "Distance $it")
            lastDistance[0]=lastDistance[1]
            lastDistance[1]=lastDistance[2]
            lastDistance[2]=it ?: 0
        }
    }

    private fun go(forward: Double, leftRight: Double) {
        if(!goForward(forward)) return
        if(leftRight != 0.0) {
            if (leftRight < 0) turnLeft() else turnRight()
            goForward(abs(leftRight))
        }
    }

    private fun goForward(meters: Double): Boolean {
        CarConnectorService.sendTxSynchronously('f')
        var timef = (meters / VELOCITY * 1000.0).toLong()
        /*while(timef > 0) {
            Thread.sleep(10)
            timef -= 10
            if(lastDistance.all { it < 40 }) {
                stopEverything()
                return false
            }
        }*/
        Thread.sleep(timef);
        CarConnectorService.sendTxSynchronously('s')
        return true
    }

    private fun turnRight() {
        CarConnectorService.sendTxSynchronously('r')
        turn()
    }

    private fun turnLeft() {
        CarConnectorService.sendTxSynchronously('l')
        turn()
    }

    private fun turn() {
        CarConnectorService.sendTxSynchronously('f')
        val time = (1.85 / VELOCITY * 1000.0).toLong()
        Thread.sleep(time)
        CarConnectorService.sendTxSynchronously('w')
        CarConnectorService.sendTxSynchronously('s')
    }

    private fun stopEverything() {
        CarConnectorService.sendTxSynchronously('w')
        CarConnectorService.sendTxSynchronously('s')
    }
}
