package ru.mirea.kozlovrd.favoritebook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> activityResultLauncher;
    static final String BOOK_NAME_KEY = "book_name";
    static final String QUOTES_KEY = "quotes name";
    static final String USER_MESSAGE = "MESSAGE";
    private TextView textViewUserBook;
    private TextView textViewMain;
    private Button buttonOpen;

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
        textViewMain = findViewById(R.id.textViewMain);
        buttonOpen = findViewById(R.id.buttonOpen);

        ActivityResultLauncher<Intent> shareActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        // Этот код выполнится, когда ShareActivity закроется и вернет результат
                        if (result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                // Получаем данные из Intent
                                String bookName = data.getStringExtra(BOOK_NAME_KEY);
                                String quote = data.getStringExtra(QUOTES_KEY);

                                // Обновляем TextView на главном экране
                                String displayText = "Название Вашей любимой книги: " + bookName + ". Цитата: " + quote;
                                textViewMain.setText(displayText);
                            }
                        }
                    }
                });
        buttonOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Создаем Intent для открытия ShareActivity
                Intent intent = new Intent(MainActivity.this, ShareActivity.class);
                // Запускаем вторую активность с помощью нашего лаунчера, ожидая результат
                shareActivityLauncher.launch(intent);
            }
        });

    }
//    public void getInfoAboutBook(View view){
//        Intent intent = new Intent(this, ShareActivity.class);
//        intent.putExtra(BOOK_NAME_KEY, "Мечтают ли андроиды об электроовцах?\n");
//        intent.putExtra(QUOTES_KEY, "Ещё не дочитал");
//        activityResultLauncher.launch(intent);
//    }
}