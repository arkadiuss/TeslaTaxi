package it.arkadiuss.teslataxiconnector

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class TaxiControlActivity : AppCompatActivity() {
    private val VELOCITY = 0.75 //meters per second

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
            val forward = forwardText.text.toString().toDouble()
            go(forward, 0.0)
        }
    }

    private fun go(forward: Double, leftRight: Double) {
        CarConnectorService.sendTx('f')
        val timef = (forward / VELOCITY * 1000.0).toLong()
        Thread.sleep(timef)
        CarConnectorService.sendTx('s')
    }
}
