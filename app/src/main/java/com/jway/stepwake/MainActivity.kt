package com.jway.stepwake

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import java.util.Calendar

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var alarmManager: AlarmManager
    private lateinit var alarmIntent: PendingIntent
    private lateinit var dbHelper: AlarmDbHelper
    private val ACTIVITY_RECOGNITION_PERMISSION_CODE = 100


        private var selectedHour = 0
        private var selectedMinute = 0

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            val hourTextView: TextView = findViewById(R.id.hourTextView)
            val minuteTextView: TextView = findViewById(R.id.minuteTextView)
            val setAlarmButton: Button = findViewById(R.id.setAlarmButton)

            // Configuração inicial com a hora atual


            // Restaurar dados salvos
            val preferences = getSharedPreferences("AlarmPreferences", Context.MODE_PRIVATE)
            selectedHour = preferences.getInt("savedHour", 0)
            selectedMinute = preferences.getInt("savedMinute", 0)

            hourTextView.text = String.format("%02d", selectedHour)
            minuteTextView.text = String.format("%02d", selectedMinute)

            checkPermission()

            // Abrir TimePicker ao clicar no TextView de hora
            hourTextView.setOnClickListener {
                showTimePicker(hourTextView, true)
            }

            // Abrir TimePicker ao clicar no TextView de minuto
            minuteTextView.setOnClickListener {
                showTimePicker(minuteTextView, false)
            }

            setAlarmButton.setOnClickListener {
                saveAlarmPreferences(selectedHour, selectedMinute)
                setAlarm() // Configurar o alarme
                Toast.makeText(this, "Alarme configurado para $selectedHour:$selectedMinute", Toast.LENGTH_SHORT).show()

            }
        }

    private fun saveAlarmPreferences(hour: Int, minute: Int) {
        val preferences = getSharedPreferences("AlarmPreferences", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putInt("savedHour", hour)
        editor.putInt("savedMinute", minute)
        editor.apply()
    }

        private fun showTimePicker(textView: TextView, isHour: Boolean) {
            val timePickerDialog = TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    if (isHour) {
                        selectedHour = hourOfDay
                        textView.text = String.format("%02d", hourOfDay)
                    } else {
                        selectedMinute = minute
                        textView.text = String.format("%02d", minute)
                    }
                },
                selectedHour,
                selectedMinute,
                true
            )
            timePickerDialog.show()
        }

    // Adicionando AlarmManager
    private fun setAlarm() {

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
            calendar.set(Calendar.MINUTE, selectedMinute)
            calendar.set(Calendar.SECOND, 0)

            // Verificar se o horário já passou
            if (calendar.timeInMillis < System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }

            val alarmIntent = Intent(this, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )

            Toast.makeText(this, "Alarme configurado para $selectedHour:$selectedMinute", Toast.LENGTH_SHORT).show()
        }
    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                ACTIVITY_RECOGNITION_PERMISSION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ACTIVITY_RECOGNITION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissão concedida!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permissão negada. O alarme pode não funcionar corretamente.", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
