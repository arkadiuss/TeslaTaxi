package it.arkadiuss.teslataxiconnector

import android.bluetooth.*
import android.content.Context
import android.util.Log
import java.util.*
import java.util.concurrent.locks.ReentrantLock

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
                    Log.d(TAG, "Characteristic changed "+characteristic?.value?.contentToString() + " "+characteristic?.properties)
                    readListener?.invoke(transformResponse(characteristic?.value))
                }
            }

            override fun onCharacteristicWrite(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?,
                status: Int
            ) {

//                Log.d(TAG, "Characteristic written "+characteristic?.value?.contentToString() + " "+characteristic?.properties)
                lock.lock()
                condition.signal()
                lock.unlock()
            }
        })
    }

    var lock = ReentrantLock()
    var condition = lock.newCondition()

    fun setOnReadListener(listener: (Int?) -> Unit) {
        this.readListener = listener
    }

    fun sendTx(a: Move) {
        Log.d(TAG, "Sending $a")
        val tx = bluetoothGatt?.getService(UART_UUID)?.getCharacteristic(TX_UUID)
        if(tx != null) {
            tx.value = byteArrayOf(a.character().toByte())
            bluetoothGatt?.writeCharacteristic(tx)
        }
    }

    fun sendTxSynchronously(a: Move) {
        sendTxSynchronously(byteArrayOf(a.character().toByte()))
    }

    fun sendTxSynchronously(a: ByteArray) {
        Log.d(TAG, "Sending $a")
        val tx = bluetoothGatt?.getService(UART_UUID)?.getCharacteristic(TX_UUID)
        if(tx != null) {
            tx.value = a
            bluetoothGatt?.writeCharacteristic(tx)
            lock.lock()
            condition.await()
            lock.unlock()
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