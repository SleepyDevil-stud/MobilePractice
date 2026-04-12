package ru.mirea.kozlovrd.thread;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;

import ru.mirea.kozlovrd.thread.databinding.ActivityThreadBinding;


public class thread extends AppCompatActivity {

    private ActivityThreadBinding binding;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_thread);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding = ActivityThreadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        TextView infoTextView = findViewById(R.id.textView);
        Thread mainThread = Thread.currentThread();
        infoTextView.setText("Имя текущего потока: " + mainThread.getName());
// Меняем имя и выводим в текстовом поле
        mainThread.setName("МОЙ НОМЕР ГРУППЫ: БСБО-52-24, НОМЕР ПО СПИСКУ: 15, МОЙ ЛЮБИИМЫЙ ФИЛЬМ: Призрак в доспехах");
        infoTextView.append("\n Новое имя потока: " + mainThread.getName());
        Log.d(thread.class.getSimpleName(),	"Stack:	"	+	Arrays.toString(mainThread.getStackTrace()));
        Log.d(thread.class.getSimpleName(),	"Group:	"	+	mainThread.getThreadGroup());
        binding.ButtonFirst.setOnClickListener(new	View.OnClickListener()	{
            @Override
            public	void	onClick(View	v)	{
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int numberThread = counter++;
                        Log.d("ThreadProject", String.format("Запущен поток № %d студентом группы № %s номер по" +
                                "списку № %d",numberThread,"БСБО-52-24",-1));
                        long endTime = System.currentTimeMillis() + 20 * 1000;
                        while(System.currentTimeMillis() < endTime){
                            synchronized (this){
                                try{
                                    wait(endTime	-	System.currentTimeMillis());
                                    Log.d(thread.class.getSimpleName(),	"Endtime:	"	+	endTime);
                                }catch(Exception e){
                                    throw new RuntimeException(e);
                                }
                            }
                            Log.d("ThreadProject",	"Выполнен	поток	№	"	+	numberThread);
                        }
                    }
                    }).start();
                }
        });

    }
}