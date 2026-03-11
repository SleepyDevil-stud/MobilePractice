package ru.mirea.kozlovrd.multidialog;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

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
        Button btnTime = findViewById(R.id.timeButton);
        Button btnDate = findViewById(R.id.DateButton);
        Button btnProgress = findViewById(R.id.ProgressButton);
        btnTime.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onClickShowTime();
            }
        });
        btnDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onClickShowDate();
            }
        });
        btnProgress.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onClickShowProgress();
            }
        });
    }
    public void onClickShowTime(){
        TimeDialog dialogFragment = new TimeDialog();
        dialogFragment.show(getSupportFragmentManager(), "Mirea");
    }
    public void onClickShowDate(){
        DateDialog dialogFragment = new DateDialog();
        dialogFragment.show(getSupportFragmentManager(), "Mirea");
    }

    public void onClickShowProgress(){
        ProgressDialogFr dialogFragment = new ProgressDialogFr();
        dialogFragment.show(getSupportFragmentManager(), "Mirea");
    }
}