package taif.example.kkhrpr.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

@SuppressLint("ServiceCast")
class BluetoothManager(private val context: Context) {
    private val adapter: BluetoothAdapter? = null
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothGatt: BluetoothGatt? = null
    private var isScanning = false
    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            // Check if the scanned device is your wearable device
            if (result.device.name == "Galaxy Watch4 Classic(72KY)") {
                connectToDevice(result.device)
            }
        }
    }

    // Initialize BluetoothAdapter and check for BLE support
    init {
        val bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager?
        bluetoothAdapter = bluetoothManager?.adapter
        if (bluetoothAdapter == null || !context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            // Handle device without BLE support
//            return
        }
    }

    // Check and request Bluetooth and Location permissions if necessary
    fun checkAndRequestPermissions(activity: Activity) {
        val permissions = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                activity,
                permissions.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        } else {
            startScan()
        }
    }

    // Start scanning for BLE devices
    @SuppressLint("MissingPermission")
    fun startScan() {
        if (isScanning) return
        isScanning = true
        bluetoothAdapter?.bluetoothLeScanner?.startScan(scanCallback)
        // Stop scan after a certain period (e.g., 10 seconds)
        // Handler().postDelayed({ stopScan() }, SCAN_PERIOD)
    }

    // Stop scanning for BLE devices
    @SuppressLint("MissingPermission")
    fun stopScan() {
        if (!isScanning) return
        isScanning = false
        bluetoothAdapter?.bluetoothLeScanner?.stopScan(scanCallback)
    }

    // Connect to the selected BLE device
    @SuppressLint("MissingPermission")
    private fun connectToDevice(device: BluetoothDevice) {
        stopScan() // Stop scanning before connecting
        bluetoothGatt = device.connectGatt(
            context,
            false,
            gattCallback,
            BluetoothDevice.TRANSPORT_LE
        )
    }

    // Callback for GATT events (e.g., connection changes, services discovered)
    private val gattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(
            gatt: BluetoothGatt?,
            status: Int,
            newState: Int
        ) {
            super.onConnectionStateChange(gatt, status, newState)
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    // Device connected, discover services
                    gatt?.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    // Device disconnected, handle accordingly
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            // Services discovered, you can now interact with the device
        }
    }

    // Disconnect from the connected BLE device
    @SuppressLint("MissingPermission")
    fun disconnect() {
        bluetoothGatt?.disconnect()
    }

    // Close BluetoothGatt instance
    @SuppressLint("MissingPermission")
    fun close() {
        bluetoothGatt?.close()
        bluetoothGatt = null
    }

    companion object {
        const val PERMISSION_REQUEST_CODE = 101
        // private const val SCAN_PERIOD = 10000L // 10 seconds
    }
}
