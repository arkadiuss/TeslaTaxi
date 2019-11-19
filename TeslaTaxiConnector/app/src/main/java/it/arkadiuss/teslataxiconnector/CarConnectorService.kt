package it.arkadiuss.teslataxiconnector

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.content.Context
import android.util.Log
import java.util.*

object CarConnectorService {
    var UART_UUID = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb")
    var TX_UUID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb")
    var RX_UUID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb")

    var bluetoothGatt: BluetoothGatt? = null
    var connected = false

    val TAG = "CarConnector"

    fun connect(context: Context, device: BluetoothDevice) {
        device.connectGatt(context, false, object: BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                Log.d(TAG, "state change")
                if (newState == BluetoothAdapter.STATE_CONNECTED) {
                    Log.d(TAG, "state connected")
                    connected = true
                    gatt?.discoverServices()
                } else if(newState == BluetoothAdapter.STATE_DISCONNECTED) {
                    Log.d(TAG, "state disconnected")
                    connected = false
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    bluetoothGatt = gatt
                }
            }
        })
    }


    fun sendTx(a: Char) {
        val tx = bluetoothGatt?.getService(UART_UUID)?.getCharacteristic(TX_UUID)
        if(tx != null) {
            tx.value = byteArrayOf(a.toByte())
            bluetoothGatt?.writeCharacteristic(tx)
        }
    }
}