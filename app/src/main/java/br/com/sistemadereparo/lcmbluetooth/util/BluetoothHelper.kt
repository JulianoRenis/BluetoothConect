package br.com.sistemadereparo.lcmbluetooth.util

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import java.io.IOException
import java.util.UUID

object BluetoothHelper {

    fun getPairedDevices(): Set<BluetoothDevice> {
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        return bluetoothAdapter?.bondedDevices ?: emptySet()
        //criar verificações
    }

    fun connectToDevice(device: BluetoothDevice, uuid: UUID): Boolean {
        val socket: BluetoothSocket? = device.createRfcommSocketToServiceRecord(uuid)
        try {
            socket?.connect()
            // Se a conexão for bem-sucedida, você pode executar a lógica adicional aqui, se necessário
            return true
        } catch (e: IOException) {
            e.printStackTrace()
            // Lidar com exceções ou erros de conexão aqui
            return false
        } finally {
            try {
                socket?.close()
            } catch (closeException: IOException) {
                closeException.printStackTrace()
            }
        }
    }
}