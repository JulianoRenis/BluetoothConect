package br.com.sistemadereparo.lcmbluetooth.ui.view

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import br.com.sistemadereparo.lcmbluetooth.broadcast.BroadcastReceiverBluetoothStatus
import br.com.sistemadereparo.lcmbluetooth.databinding.ActivityTesteLcmBinding
import br.com.sistemadereparo.lcmbluetooth.factory.TesteLcmViewModelFactory
import br.com.sistemadereparo.lcmbluetooth.repository.BluetoothHabilitadoRepositoryImpl
import br.com.sistemadereparo.lcmbluetooth.ui.viewmodel.TesteLcmViewModel
import br.com.sistemadereparo.lcmbluetooth.util.BluetoothConst.REQUEST_ENABLE_BLUETOOTH

class TesteLcmActivity : AppCompatActivity(),BroadcastReceiverBluetoothStatus.BluetoothStatusListener {


    private val bluetoothViewModel: TesteLcmViewModel by viewModels {
        TesteLcmViewModelFactory(BluetoothHabilitadoRepositoryImpl())
    }
    private val binding by lazy { ActivityTesteLcmBinding.inflate(layoutInflater) }

    private  var conectado=false
    private lateinit var bluetoothBroadcastReceiver: BroadcastReceiverBluetoothStatus

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        checkBluetoothStatus()
        binding.btnConectar.isEnabled=false

        bluetoothBroadcastReceiver = BroadcastReceiverBluetoothStatus(this)

        IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        }.also { intentFilter->
            registerReceiver(bluetoothBroadcastReceiver, intentFilter)

        }

        // Check Bluetooth status
        bluetoothViewModel.checkBluetoothStatus()

       val dadosRecebidos= intent.getBooleanExtra("conectado",conectado)

        if (dadosRecebidos){
            binding.btnConectar.text ="Conectado"
            binding.btnConectar.isEnabled=true

        }else {
            binding.btnConectar.text="Conectar"

        }

        binding.btnConectar.setOnClickListener {
            startActivity(Intent(this, ListaDispositivosActivity::class.java))
        }




    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(bluetoothBroadcastReceiver)
    }

    private fun checkBluetoothStatus() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {

            Toast.makeText(this, "Bluetooth não suportado", Toast.LENGTH_SHORT).show()
        } else {
            if (!bluetoothAdapter.isEnabled) {

                val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth foi habilitado. Pode acrescentar algo que queira fazer quando bluetooth está habilidado
                //Toast.makeText(applicationContext, "Bluetooth habilitado", Toast.LENGTH_SHORT).show()
            } else {
                binding.btnConectar.text="Conectar"
                // Pode tomar medidas para que o usuario entenda que é preciso ligar o bluettooth
            }
        }
    }

    override fun onBluetoothEnabled(enabled: Boolean) {

        if (enabled) {
            Toast.makeText(applicationContext, "Bluetooth ligado", Toast.LENGTH_SHORT).show()
            checkBluetoothStatus()
            binding.btnConectar.isEnabled=true

        } else {
            checkBluetoothStatus()
            Toast.makeText(applicationContext, "Bluetooth desligado", Toast.LENGTH_SHORT).show()
            binding.btnConectar.isEnabled=false



        }

    }


}