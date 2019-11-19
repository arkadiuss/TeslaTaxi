package it.arkadiuss.teslataxiconnector

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DeviceAdapter(val onClick: (BluetoothDevice) -> Unit): RecyclerView.Adapter<DeviceAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    private val devices: MutableList<BluetoothDevice> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.device_item, parent, false);
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return devices.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nameTV = holder.itemView.findViewById<TextView>(R.id.name)
        val addressTV = holder.itemView.findViewById<TextView>(R.id.address)
        nameTV.text = devices[position].name
        addressTV.text = devices[position].address;
        holder.itemView.setOnClickListener { onClick(devices[position]) }
    }

    fun addDevice(device: BluetoothDevice) {
        devices.add(device)
        notifyDataSetChanged()
    }
}