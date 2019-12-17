package it.arkadiuss.teslataxiconnector

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        forwardBtn.setOnClickListener {
            CarConnectorService.sendTx('f')
        }

        rightBtn.setOnClickListener {
            CarConnectorService.sendTx('r')
        }

        leftBtn.setOnClickListener {
            CarConnectorService.sendTx('l')
        }

        backwardBtn.setOnClickListener {
            CarConnectorService.sendTx('b')
        }
    }
}
