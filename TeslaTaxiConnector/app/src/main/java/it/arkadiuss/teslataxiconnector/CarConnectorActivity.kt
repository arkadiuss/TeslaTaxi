package it.arkadiuss.teslataxiconnector

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.STATE_CONNECTED
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService


class CarConnectorActivity : AppCompatActivity() {
    private val adapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val devicesListAdapter = DeviceAdapter {
        deviceSelected(it)
    }
    private val REQUEST_ENABLE_BT = 1


    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    if (device != null) {
                        devicesListAdapter.addDevice(device)
                    }
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_car_connector)

        val devicesView = findViewById<RecyclerView>(R.id.devices_list)
        devicesView.adapter = devicesListAdapter
        devicesView.layoutManager = LinearLayoutManager(this)

        val searchButton = findViewById<Button>(R.id.search_button)
        searchButton.setOnClickListener {
            if(adapter?.isDiscovering != true) {
                adapter?.startDiscovery()
            }
        }

        enableBluetooth()
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    private fun enableBluetooth() {
        if(adapter == null) {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_LONG).show()
        } else if(!adapter.isEnabled){
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
    }

    private fun deviceSelected(device: BluetoothDevice) {
        CarConnectorService.connect(this, device)
        showDialog {
            it.setMessage("What do you want to do?")
            it.setNeutralButton("Manual Control") { _, _ ->
                val intent = Intent(this, ControlActivity::class.java)
                startActivity(intent)
            }
            it.setPositiveButton("Taxi mode") { _, _ ->
                val intent = Intent(this, TaxiControlActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
