package taif.example.kkhrpr.presentation.theme

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattServer
import android.bluetooth.BluetoothGattServerCallback
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import java.util.*

@SuppressLint("MissingPermission")
class BluetoothServerManager(private val context: Context) {
    private var bluetoothManager: BluetoothManager? = null
    private var bluetoothGattServer: BluetoothGattServer? = null
    private var sensorDataCharacteristic: BluetoothGattCharacteristic? = null
    private var sensorData: ByteArray? = null

    // Initialize BluetoothManager and BluetoothGattServer
    init {
        bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager?
//        bluetoothGattServer = bluetoothManager?.openGattServer(context, gattServerCallback)
        setupGattServer()
    }

    // Set up BluetoothGattServer and add services
    private fun setupGattServer() {
        // Create a service UUID for sensor data
        val serviceUuid = UUID.fromString("your_service_uuid_here")
        val service = bluetoothGattServer?.getService(serviceUuid)
//            ?: bluetoothGattServer?.addService(serviceUuid)

        // Create a characteristic UUID for sensor data
        val characteristicUuid = UUID.fromString("your_characteristic_uuid_here")
        sensorDataCharacteristic =
            BluetoothGattCharacteristic(
                characteristicUuid,
                BluetoothGattCharacteristic.PROPERTY_READ or BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_READ
            )

        // Add the characteristic to the service
//        service?.addCharacteristic(sensorDataCharacteristic)
    }

    // Update sensor data value
    fun updateSensorData(data: ByteArray) {
        sensorData = data
        sensorDataCharacteristic?.value = data
        // Notify connected devices about the updated sensor data
        bluetoothGattServer?.notifyCharacteristicChanged(
            bluetoothGattServer?.connectedDevices?.get(0),
            sensorDataCharacteristic,
            false
        )
    }

    // Callback for BluetoothGattServer events
    private val gattServerCallback = object : BluetoothGattServerCallback() {
        override fun onConnectionStateChange(
            device: android.bluetooth.BluetoothDevice?,
            status: Int,
            newState: Int
        ) {
            super.onConnectionStateChange(device, status, newState)
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // Device connected, handle accordingly
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // Device disconnected, handle accordingly
            }
        }
    }

    // Close BluetoothGattServer
    fun closeGattServer() {
        bluetoothGattServer?.close()
    }
}
