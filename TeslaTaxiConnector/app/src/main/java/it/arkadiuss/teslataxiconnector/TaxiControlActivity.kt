package it.arkadiuss.teslataxiconnector

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.core.text.isDigitsOnly
import kotlin.math.abs

class TaxiControlActivity : AppCompatActivity() {
    private val VELOCITY = 0.75 //meters per second
    private val ANGLE_VELOCITY = 0.75 //meters per second

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
    }

    private fun go(forward: Double, leftRight: Double) {
        Log.d(TaxiControlActivity::class.java.simpleName, "$forward $leftRight")
        goForward(forward)
        if(leftRight < 0) turnLeft() else turnRight()
        goForward(abs(leftRight))
    }

    private fun goForward(meters: Double) {
        CarConnectorService.sendTxSynchronously('f')
        val timef = (meters / VELOCITY * 1000.0).toLong()
        Thread.sleep(timef)
        CarConnectorService.sendTxSynchronously('s')
    }

    private fun turnRight() {
        CarConnectorService.sendTxSynchronously('r')
//        Thread.sleep(200)
        turn()
    }

    private fun turnLeft() {
        CarConnectorService.sendTxSynchronously('l')
//        Thread.sleep(200)
        turn()
    }

    private fun turn() {
        CarConnectorService.sendTxSynchronously('f')
        val time = (1 / ANGLE_VELOCITY * 1000.0).toLong()
        Thread.sleep(time)
        CarConnectorService.sendTxSynchronously('w')
        CarConnectorService.sendTxSynchronously('s')
    }
}
