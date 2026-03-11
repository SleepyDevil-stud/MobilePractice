package ru.mirea.kozlovrd.toastapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private static final String STUDENT_GROUP = "БСБО-52-24";
    private static final String STUDENT_NUMBER = "15";
    private EditText editTextInput;
    private Button btnCountChars;
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
        editTextInput = findViewById(R.id.editTextInput);
        btnCountChars = findViewById(R.id.buttonCount);
        btnCountChars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countChars();
            }
        });
    }
    private void countChars(){
        String inputText = editTextInput.getText().toString();
        int charCount = inputText.length();
        String message = "Студент № " + STUDENT_NUMBER + "Группа " + STUDENT_GROUP + "Количество символов: " + charCount;

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}