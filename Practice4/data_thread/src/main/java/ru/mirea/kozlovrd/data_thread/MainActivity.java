package ru.mirea.kozlovrd.data_thread;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import ru.mirea.kozlovrd.data_thread.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private StringBuilder logBuilder = new StringBuilder();

    String explanation =
            "1. runOnUiThread(Runnable action):\n" +
            "   - Выполняет код в UI потоке\n" +
            "   - Можно вызывать из любого потока\n" +
            "   - Выполняется немедленно или после текущих задач\n\n" +
            "2. post(Runnable action):\n" +
            "   - Отправляет задачу в очередь Handler'а\n" +
            "   - Выполняется в UI потоке\n" +
            "   - Выполняется как можно скорее, но после текущих задач\n\n" +
            "3. postDelayed(Runnable action, long delayMillis):\n" +
            "   - Отправляет задачу с задержкой\n" +
            "   - Выполняется в UI потоке через указанное время\n" +
            "   - Не блокирует другие операции";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.explanationView.setText(explanation);


        binding.secondButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLog("UI thread button activated");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(() -> {
                            binding.secondButton.setText("Running on UI thread");
                            addLog("runOnUiThread: Код выполнен в UI потоке");

                        });
                    }
                }).start();
            }
        });
        binding.firstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLog("PostDelayed button activated");
                long startTime = System.currentTimeMillis();
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    long endTime = System.currentTimeMillis() - startTime;
                    binding.firstButton.setText("Post Delayed Text");
                    addLog("postDelayed: Выполнен через " + endTime + " мс");

                    Toast.makeText(getApplicationContext(), "Время выполненения: "+ endTime, Toast.LENGTH_LONG).show();
                }, 2000);
                addLog("postDelayed: Задача поставлена в очередь (таймер запущен)");
                Toast.makeText(MainActivity.this,
                        "postDelayed: Задача запланирована через 2 сек",
                        Toast.LENGTH_SHORT).show();


            }
        });
        binding.thirdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLog("post: Нажата кнопка, замер времени начат");
                long startTime = System.currentTimeMillis();
                new Handler(Looper.getMainLooper()).post(() -> {
                    long endTime = System.currentTimeMillis() - startTime;
                    binding.thirdButton.setText("Post Delayed Text");
                    Toast.makeText(getApplicationContext(), "Время выполненения: "+ endTime, Toast.LENGTH_LONG).show();
                    addLog("post: Выполнен через " + endTime + " мс");


                });
                addLog("post: Задача поставлена в очередь на выполнение");
                Toast.makeText(MainActivity.this,
                        "post: Задача в очереди Handler",
                        Toast.LENGTH_SHORT).show();

            }
        });
    }
    private void addLog(String message) {
        runOnUiThread(() -> {
            logBuilder.append(message).append("\n");
            binding.logView.setText(logBuilder.toString());
            // Автопрокрутка вниз
            final int scrollAmount = binding.logView.getLayout().getLineTop(binding.logView.getLineCount()) - binding.logView.getHeight();
            if (scrollAmount > 0)
                binding.logView.scrollTo(0, scrollAmount);
        });
    }
}