package com.example.osmexample;

import android.content.Context;
import android.hardware.SensorEventListener;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;

public class CompassActivity extends AppCompatActivity implements SensorEventListener {
    private ImageView compassImageView;
    private SensorManager sensorManager;
    private Sensor magneticSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //compassImageView = findViewById(R.id.compassImageView);
        // Другие инициализации и настройки
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == magneticSensor) {
            float azimuth = event.values[0]; // Угол азимута в радианах
            // Обновите ваш пользовательский интерфейс с новым углом азимута
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Необходимо реализовать этот метод
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
