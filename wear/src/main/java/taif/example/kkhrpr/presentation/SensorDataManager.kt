package taif.example.kkhrpr.presentation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class SensorDataManager(private val context: Context) : SensorEventListener {
    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null
    private var sensorListener: SensorListener? = null

    // Initialize SensorManager and sensors
    init {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager?
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    }

    // Check and request sensor-related permissions if necessary
    fun checkAndRequestPermissions(activity: Activity) {
        val permissions = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BODY_SENSORS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(Manifest.permission.BODY_SENSORS)
        }
        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                activity,
                permissions.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        } else {
            startSensorDataCollection()
        }
    }

    // Start collecting sensor data
    fun startSensorDataCollection() {
        accelerometer?.let { sensor ->
            sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        gyroscope?.let { sensor ->
            sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    // Stop collecting sensor data
    fun stopSensorDataCollection() {
        sensorManager?.unregisterListener(this)
    }

    // Set the listener to receive sensor data
    fun setSensorListener(listener: MainActivity) {
        this.sensorListener = listener
    }

    // Callback for sensor data changes
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            when (it.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    // Handle accelerometer data
                    sensorListener?.onAccelerometerDataChanged(it.values)
                }
                Sensor.TYPE_GYROSCOPE -> {
                    // Handle gyroscope data
                    sensorListener?.onGyroscopeDataChanged(it.values)
                }

                else -> {}
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Handle accuracy changes if needed
    }

    // Interface for receiving sensor data
    interface SensorListener {
        fun onAccelerometerDataChanged(values: FloatArray)
        fun onGyroscopeDataChanged(values: FloatArray)
    }

    companion object {
        const val PERMISSION_REQUEST_CODE = 101
    }
}
