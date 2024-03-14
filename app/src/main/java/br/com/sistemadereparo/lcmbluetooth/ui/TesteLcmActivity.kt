package br.com.sistemadereparo.lcmbluetooth.ui

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import br.com.sistemadereparo.lcmbluetooth.R
import br.com.sistemadereparo.lcmbluetooth.databinding.ActivityTesteLcmBinding
import br.com.sistemadereparo.lcmbluetooth.util.BluetoothConst.REQUEST_ENABLE_BLUETOOTH

class TesteLcmActivity : AppCompatActivity() {


    private val binding by lazy { ActivityTesteLcmBinding.inflate(layoutInflater) }

    private  var conectado=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnConectar.isEnabled=false
        checkBluetoothStatus()

       val dadosRecebidos= intent.getBooleanExtra("conectado",conectado)
        if (dadosRecebidos==true){
            binding.btnConectar.text ="Conectado"
            binding.btnConectar.isEnabled=true


        }else {
            binding.btnConectar.text="Conectar"
        }

        binding.btnConectar.setOnClickListener {
            startActivity(Intent(this,ListaDispositivosActivity::class.java))
        }

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
                // Bluetooth foi habilitado. Pode acrecentar algo que queira fazer quando bluetooth está habilidado
                Toast.makeText(this, "Bluetooth habilitado", Toast.LENGTH_SHORT).show()
                binding.btnConectar.isEnabled=true
            } else {

                Toast.makeText(this, "Bluetooth precisa ser ligado ", Toast.LENGTH_SHORT).show()
                // Pode tomar medidas para que o usuario entenda que é preciso ligar o bluettooth
            }
        }
    }

}