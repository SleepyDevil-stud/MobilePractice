package ru.mirea.kozlovrd.multiactivity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity2 extends AppCompatActivity {
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        textView = findViewById(R.id.textViewResult);
        Bundle extras = getIntent().getExtras();
        String message = null;

        if (extras != null) {
            message = extras.getString("EXTRA_MESSAGE");
        }

        // Отображаем полученное сообщение (если message == null, покажем текст по умолчанию)
        if (message != null && !message.isEmpty()) {
            textView.setText(message);
        } else {
            textView.setText("Сообщение не получено");
        }
    }
}