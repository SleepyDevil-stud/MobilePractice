package ru.mirea.kozlovrd.mireaproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ToolsFragment# newInstance} factory method to
 * create an instance of this fragment.
 */
public class ToolsFragment extends Fragment {

    private TextView statusTextView;
    private Button startWorkerButton;
    private ProgressBar progressBar;
    private WorkManager workManager;
    private
    Handler mainHandler;

    public ToolsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tools, container, false);

        // Инициализация элементов
        statusTextView = view.findViewById(R.id.statusTextView);
        startWorkerButton = view.findViewById(R.id.startWorkerButton);
        progressBar = view.findViewById(R.id.progressBar);

        // Инициализация WorkManager и Handler
        workManager = WorkManager.getInstance(requireContext());
        mainHandler = new Handler(Looper.getMainLooper());

        // Обработчик кнопки
        startWorkerButton.setOnClickListener(v -> startBackgroundWork());

        return view;
    }

    private void startBackgroundWork() {
        // Меняем UI - задача запущена
        statusTextView.setText("Обработка информации...");
        startWorkerButton.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        // Создаем ограничения (опционально)
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED) // Сеть не требуется
                .build();

        // Создаем данные для передачи (опционально)
        Data inputData = new Data.Builder()
                .putString("task_name", "Обработка данных")
                .build();

        // Настраиваем Worker
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(BackgroundWorker.class)
                .setConstraints(constraints)
                .setInputData(inputData)
                .addTag("background_task")
                .build();

        // Запускаем Worker
        workManager.enqueue(workRequest);

        // Отслеживаем статус выполнения
        workManager.getWorkInfoByIdLiveData(workRequest.getId())
                .observe(getViewLifecycleOwner(), workInfo -> {
                    if (workInfo != null) {
                        switch (workInfo.getState()) {
                            case SUCCEEDED:
                                // Задача выполнена успешно
                                mainHandler.post(() -> {
                                    statusTextView.setText("Выполнено ");
                                    startWorkerButton.setEnabled(true);
                                    progressBar.setVisibility(View.GONE);

                                    // Через 3 секунды сбрасываем статус
                                    mainHandler.postDelayed(() -> {
                                        if (isAdded()) {
                                            statusTextView.setText("Не запущено");
                                        }
                                    }, 3000);
                                });
                                break;

                            case FAILED:
                                // Задача завершилась с ошибкой
                                mainHandler.post(() -> {
                                    statusTextView.setText("Ошибка выполнения ✗");
                                    startWorkerButton.setEnabled(true);
                                    progressBar.setVisibility(View.GONE);
                                });
                                break;

                            case CANCELLED:
                                // Задача отменена
                                mainHandler.post(() -> {
                                    statusTextView.setText("Отменено");
                                    startWorkerButton.setEnabled(true);
                                    progressBar.setVisibility(View.GONE);
                                });
                                break;
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Отменяем все задачи при уничтожении фрагмента (опционально)
        // workManager.cancelAllWorkByTag("background_task");
    }
}