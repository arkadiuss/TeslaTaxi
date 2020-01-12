package it.arkadiuss.teslataxiconnector

import android.bluetooth.*
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
    private var readListener: ((Int?) -> Unit)? = null

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
                    val rx = bluetoothGatt?.getService(UART_UUID)?.getCharacteristic(RX_UUID)
                    for(descriptor in rx!!.descriptors) {
                        descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        bluetoothGatt?.writeDescriptor(descriptor)
                    }
                    bluetoothGatt?.setCharacteristicNotification(rx, true)
                }
            }

            override fun onCharacteristicChanged(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?
            ) {
                if(characteristic?.uuid == RX_UUID) {
                    val test = characteristic?.getStringValue(0)
                    readListener?.invoke(transformResponse(characteristic?.value))
                }
            }
        })
    }

    fun setOnReadListener(listener: (Int?) -> Unit) {
        this.readListener = listener
    }

    fun sendTx(a: Char) {
        Log.d(TAG, "Sending $a")
        val tx = bluetoothGatt?.getService(UART_UUID)?.getCharacteristic(TX_UUID)
        if(tx != null) {
            tx.value = byteArrayOf(a.toByte())
            bluetoothGatt?.writeCharacteristic(tx)
        }
    }

    fun transformResponse(bytes: ByteArray?): Int? {
        if(bytes == null) return null
        var i = 0
        var r = 0
        while(i < bytes.size - 1 && !(bytes[i] == 13.toByte() && bytes[i + 1] == 10.toByte())) {
            r = r*10 + (bytes[i] - 48)
            i++
        }
        return r
    }
}