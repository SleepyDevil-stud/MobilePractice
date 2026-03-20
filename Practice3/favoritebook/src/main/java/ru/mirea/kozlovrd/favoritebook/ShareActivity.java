package ru.mirea.kozlovrd.favoritebook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ShareActivity extends AppCompatActivity {
    private EditText editTextBook;
    private EditText editTextQuote;
    private Button buttonSend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_share);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        editTextBook = findViewById(R.id.editTextBook);
        editTextQuote = findViewById(R.id.editTextQuote);
        buttonSend = findViewById(R.id.buttonSend);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userBook = editTextBook.getText().toString().trim();
                String userQuote = editTextQuote.getText().toString().trim();

                Intent resultIntent = new Intent();

                resultIntent.putExtra(MainActivity.BOOK_NAME_KEY, userBook);
                resultIntent.putExtra(MainActivity.QUOTES_KEY, userQuote);

                setResult(RESULT_OK, resultIntent);

                finish();
            }
        });
    }

}