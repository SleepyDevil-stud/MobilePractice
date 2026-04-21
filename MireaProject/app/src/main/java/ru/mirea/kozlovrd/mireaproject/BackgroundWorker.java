package ru.mirea.kozlovrd.mireaproject;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class BackgroundWorker extends Worker {

    public BackgroundWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Имитация длительной задачи
        try {
            // Предполагаем, что задача занимает 10 секунд
            for (int i = 0; i < 10; i++) {
                // Проверяем, не остановлена ли работа
                if (isStopped()) {
                    return Result.failure();
                }

                // Имитируем выполнение части работы (каждая секунда)
                Thread.sleep(1000);

                // Отправляем прогресс (опционально)
                setProgressAsync(new Data.Builder()
                        .putInt("progress", (i + 1) * 10)
                        .build());
            }

            // Возвращаем успешный результат
            return Result.success();

        } catch (InterruptedException e) {
            e.printStackTrace();
            return Result.failure();
        }
    }
}