package com.jway.stepwake

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Toast

class AlarmReceiver : BroadcastReceiver(), SensorEventListener {

    private var mediaPlayer: MediaPlayer? = null
    private var sensorManager: SensorManager? = null
    private var stepCount = 0
    private var initialStepCount = -1

    override fun onReceive(context: Context, intent: Intent?) {
        Toast.makeText(context, "Alarme disparado! Ande 20 passos para desligar.", Toast.LENGTH_LONG).show()

        // Tocar som do alarme
        val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        mediaPlayer = MediaPlayer.create(context, alarmSound)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()

        // Configurar Sensor de Passos
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor != null) {
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL)
        } else {
            Toast.makeText(context, "Sensor de passos não disponível.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            if (initialStepCount == -1) {
                initialStepCount = event.values[0].toInt()
            }

            stepCount = event.values[0].toInt() - initialStepCount

            if (stepCount >= 20) {
                // Parar o alarme após 20 passos
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null

                // Desregistrar o listener do sensor
                sensorManager?.unregisterListener(this)


            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Não necessário neste caso
    }
}


