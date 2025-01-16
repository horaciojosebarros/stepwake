package com.jway.stepwake

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.widget.Toast

class AlarmService : android.app.Service(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var stepCounter: Sensor? = null
    private var initialStepCount: Int = 0
    private var stepGoal = 20
    private var stepsTaken = 0
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate() {
        super.onCreate()

        mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound)
        mediaPlayer.isLooping = true
        mediaPlayer.start()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepCounter != null) {
            sensorManager.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_NORMAL)
        } else {
            Toast.makeText(this, R.string.step_sensor_not_available_message, Toast.LENGTH_SHORT).show()
            stopSelf()
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null && event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            if (initialStepCount == 0) {
                initialStepCount = event.values[0].toInt()
            }

            stepsTaken = event.values[0].toInt() - initialStepCount

            if (stepsTaken >= stepGoal) {
                stopAlarm()
            }
        }
    }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

        private fun stopAlarm() {
            mediaPlayer.stop()
            mediaPlayer.release()
            sensorManager.unregisterListener(this)
            stopSelf()
            Toast.makeText(this, getString(R.string.alarm_stopped_message, stepsTaken), Toast.LENGTH_SHORT).show()
        }

        override fun onBind(intent: Intent?) = null
}