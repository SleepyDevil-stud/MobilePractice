package ru.mirea.kozlovrd.mireaproject;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CompassFragment extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private ImageView compassImage;
    private TextView directionText;
    private TextView azimuthText;

    private float[] lastAccelerometer = new float[3];
    private float[] lastMagnetometer = new float[3];
    private boolean lastAccelerometerSet = false;
    private boolean lastMagnetometerSet = false;
    private float[] rotationMatrix = new float[9];
    private float[] orientation = new float[3];
    private float currentDegree = 0f;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compass, container, false);

        compassImage = view.findViewById(R.id.compassImage);
        directionText = view.findViewById(R.id.directionText);
        azimuthText = view.findViewById(R.id.azimuthText);

        sensorManager = (SensorManager) requireActivity().getSystemService(requireContext().SENSOR_SERVICE);

        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }

        if (accelerometer == null || magnetometer == null) {
            Toast.makeText(getContext(), "Датчики не найдены на устройстве", Toast.LENGTH_LONG).show();
            directionText.setText("Датчики недоступны");
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
        if (magnetometer != null) {
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == accelerometer) {
            System.arraycopy(event.values, 0, lastAccelerometer, 0, event.values.length);
            lastAccelerometerSet = true;
        } else if (event.sensor == magnetometer) {
            System.arraycopy(event.values, 0, lastMagnetometer, 0, event.values.length);
            lastMagnetometerSet = true;
        }

        if (lastAccelerometerSet && lastMagnetometerSet) {
            SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer, lastMagnetometer);
            SensorManager.getOrientation(rotationMatrix, orientation);

            float azimuthInRadians = orientation[0];
            float azimuthInDegrees = (float) Math.toDegrees(azimuthInRadians);
            azimuthInDegrees = (azimuthInDegrees + 360) % 360;

            azimuthText.setText(String.format("Азимут: %.1f°", azimuthInDegrees));

            // Определяем направление
            String direction = getDirection(azimuthInDegrees);
            directionText.setText(direction);

            // Анимация поворота компаса
            RotateAnimation ra = new RotateAnimation(
                    currentDegree,
                    -azimuthInDegrees,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);

            ra.setDuration(250);
            ra.setFillAfter(true);
            compassImage.startAnimation(ra);
            currentDegree = -azimuthInDegrees;
        }
    }

    private String getDirection(float azimuth) {
        if (azimuth >= 337.5f || azimuth < 22.5f) return "Север ↑";
        if (azimuth >= 22.5f && azimuth < 67.5f) return "Северо-Восток ↗";
        if (azimuth >= 67.5f && azimuth < 112.5f) return "Восток →";
        if (azimuth >= 112.5f && azimuth < 157.5f) return "Юго-Восток ↘";
        if (azimuth >= 157.5f && azimuth < 202.5f) return "Юг ↓";
        if (azimuth >= 202.5f && azimuth < 247.5f) return "Юго-Запад ↙";
        if (azimuth >= 247.5f && azimuth < 292.5f) return "Запад ←";
        return "Северо-Запад ↖";
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}