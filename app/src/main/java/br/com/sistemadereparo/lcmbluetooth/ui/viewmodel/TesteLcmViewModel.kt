package br.com.sistemadereparo.lcmbluetooth.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.sistemadereparo.lcmbluetooth.repository.BluetoothHabilitadoRepository


class TesteLcmViewModel(private val bluetoothRepository: BluetoothHabilitadoRepository) : ViewModel() {

    private val _bluetoothEnabled = MutableLiveData<Boolean>()
    val bluetoothEnabled: LiveData<Boolean>
        get() = _bluetoothEnabled

    init {
        _bluetoothEnabled.value = false
    }

    fun checkBluetoothStatus() {
        _bluetoothEnabled.value = bluetoothRepository.bluetoothEstaHabilitado()
    }
}