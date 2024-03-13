package br.com.sistemadereparo.lcmbluetooth.ui

import android.R
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import br.com.sistemadereparo.lcmbluetooth.databinding.ActivityListaDispositivosBinding
import br.com.sistemadereparo.lcmbluetooth.factory.BluetoothViewModelFactory
import br.com.sistemadereparo.lcmbluetooth.model.BluetoothDeviceModel
import br.com.sistemadereparo.lcmbluetooth.repository.BluetoothRepositoryImpl
import br.com.sistemadereparo.lcmbluetooth.ui.viewmodel.ListaDispositivosViewModel
import java.util.UUID

class ListaDispositivosActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_BLUETOOTH_PERMISSION = 1
        private const val REQUEST_ENABLE_BLUETOOTH = 2

    }
    private val listaDispositivosViewModel: ListaDispositivosViewModel by viewModels {
        BluetoothViewModelFactory(BluetoothRepositoryImpl())
    }
    private val binding by lazy {
        ActivityListaDispositivosBinding.inflate(layoutInflater)
    }

    private lateinit var listViewDispositivos: ListView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        checkBluetoothStatus()
        listViewDispositivos = binding.listViewDevices

        val dispositivoAdapter = ArrayAdapter<BluetoothDeviceModel>(
            this,
            R.layout.simple_list_item_1,
            ArrayList()
            
        )
        listViewDispositivos.adapter= dispositivoAdapter

        listViewDispositivos.setOnItemClickListener{ _, _, position, _ ->
            val dispositivoSelcionado = dispositivoAdapter.getItem(position)

            if (dispositivoSelcionado!=null){
                val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // Example UUID for SPP
                val conectado = listaDispositivosViewModel.connectToDevice(dispositivoSelcionado,uuid)
                
                if (conectado){
                    Toast.makeText(this, "Conectado ao ${dispositivoSelcionado.nome}", Toast.LENGTH_SHORT).show()
                }else {
                    Toast.makeText(this, "Conexão falhou ${dispositivoSelcionado.nome}", Toast.LENGTH_SHORT).show()
                    // Handle failed connection
                }
            }

        }


        listaDispositivosViewModel.getPairedDevices()
        requestBluetoothPermissions()
    }

    private fun requestBluetoothPermissions() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
            android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.BLUETOOTH,
                    android.Manifest.permission.BLUETOOTH_ADMIN,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQUEST_BLUETOOTH_PERMISSION
            )
        }
    }

    override fun onRestart() {
        super.onRestart()
        checkBluetoothStatus()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
            // Verificar se as permissões foram concedidas
            if (grantResults.isNotEmpty() && grantResults.all { it == android.content.pm.PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "Permissões consedidas", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Precisamos que conseda as permissões exigidas ", Toast.LENGTH_SHORT).show()            }
        }
    }

    private fun observeDevices() {
        listaDispositivosViewModel.pairedDevices.observe(this, { devices ->
            (listViewDispositivos.adapter as ArrayAdapter<BluetoothDeviceModel>).apply {
                clear()
                addAll(devices)
            }
        })
    }
    private fun checkBluetoothStatus() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Toast.makeText(this, "Bluetooth não suportado", Toast.LENGTH_SHORT).show()
        } else {
            if (!bluetoothAdapter.isEnabled) {
                // Bluetooth is not enabled, prompt the user to enable it
                val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth was enabled by the user
                Toast.makeText(this, "Bluetooth habilitado", Toast.LENGTH_SHORT).show()
                observeDevices()
            } else {
                // User denied the request to enable Bluetooth
                Toast.makeText(this, "Bluetooth precisa ser ligado ", Toast.LENGTH_SHORT).show()
                // You can handle this situation accordingly, maybe by finishing the activity or displaying a message
            }
        }
    }
}