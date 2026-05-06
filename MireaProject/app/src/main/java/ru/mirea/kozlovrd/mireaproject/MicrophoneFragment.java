package ru.mirea.kozlovrd.mireaproject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MicrophoneFragment extends Fragment {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private MediaRecorder mediaRecorder;
    private String audioFilePath;
    private boolean isRecording = false;

    private Button recordButton;
    private Button playLastButton;
    private TextView statusText;
    private TextView tipsText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_microphone, container, false);

        recordButton = view.findViewById(R.id.recordButton);
        playLastButton = view.findViewById(R.id.playLastButton);
        statusText = view.findViewById(R.id.statusText);
        tipsText = view.findViewById(R.id.tipsText);

        recordButton.setOnClickListener(v -> checkAudioPermission());
        playLastButton.setOnClickListener(v -> playLastRecording());

        return view;
    }

    private void checkAudioPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
        } else {
            toggleRecording();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                toggleRecording();
            } else {
                Toast.makeText(getContext(), "Нужно разрешение для использования микрофона", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void toggleRecording() {
        if (isRecording) {
            stopRecording();
        } else {
            startRecording();
        }
    }

    private void startRecording() {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File audioDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC);
            File audioFile = new File(audioDir, "recording_" + timeStamp + ".3gp");
            audioFilePath = audioFile.getAbsolutePath();

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(audioFilePath);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;

            recordButton.setText("⏹️ Остановить запись");
            statusText.setText("Идёт запись...");
            playLastButton.setEnabled(false);

            Toast.makeText(getContext(), "Запись началась", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getContext(), "Ошибка записи", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;

            recordButton.setText("🎤 Начать запись");
            statusText.setText("Запись сохранена: " + new File(audioFilePath).getName());
            playLastButton.setEnabled(true);

            // Анализ голоса для творческой задачи
            analyzeVoiceRecording();

            Toast.makeText(getContext(), "Запись сохранена", Toast.LENGTH_SHORT).show();
        }
    }

    private void analyzeVoiceRecording() {
        // Творческая задача: анализ длительности и рекомендации
        File audioFile = new File(audioFilePath);
        long fileSize = audioFile.length();
        long duration = fileSize / 16000; // Приблизительная оценка в секундах

        String advice;
        if (duration < 5) {
            advice = "Короткая запись. Попробуйте рассказать больше!";
        } else if (duration < 15) {
            advice = "Хорошая длительность для заметки голосом.";
        } else {
            advice = "Длинная запись. Вы можете использовать её как аудиодневник.";
        }

        tipsText.setText(String.format("📊 Анализ:\nРазмер: %.1f KB\nПримерная длина: %d сек\n%s",
                fileSize / 1024.0, duration, advice));
    }

    private void playLastRecording() {
        if (audioFilePath != null) {
            // Используем системный медиаплеер
            android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW);
            android.net.Uri uri = androidx.core.content.FileProvider.getUriForFile(requireContext(),
                    "ru.mirea.kozlovrd.mireaproject.fileprovider",
                    new File(audioFilePath));
            intent.setDataAndType(uri, "audio/3gp");
            intent.addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "Нет сохранённых записей", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isRecording) {
            stopRecording();
        }
    }
}