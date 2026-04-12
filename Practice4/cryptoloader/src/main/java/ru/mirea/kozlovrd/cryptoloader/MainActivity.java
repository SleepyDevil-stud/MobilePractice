package ru.mirea.kozlovrd.cryptoloader;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.google.android.material.snackbar.Snackbar;

import java.security.InvalidParameterException;

import javax.crypto.SecretKey;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>{
    public final String TAG = this.getClass().getSimpleName();
    private final int LoaderID = 1234;
    private EditText editTextPhrase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextPhrase = findViewById(R.id.editTextPhrase);
    }

    public void onClickButton(View view) {
        String phrase = editTextPhrase.getText().toString().trim();

        if (phrase.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, введите фразу", Toast.LENGTH_SHORT).show();
            return;
        }

        try {

            SecretKey secretKey = MyLoader.generateKey();


            byte[] encryptedBytes = MyLoader.encryptMsg(phrase, secretKey);
            String encryptedBase64 = Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
            String keyBase64 = MyLoader.secretKeyToString(secretKey);

            Log.d(TAG, "Исходная фраза: " + phrase);
            Log.d(TAG, "Зашифрованная фраза (Base64): " + encryptedBase64);
            Log.d(TAG, "Ключ (Base64): " + keyBase64);


            Bundle bundle = new Bundle();
            bundle.putString(MyLoader.ARG_ENCRYPTED_MSG, encryptedBase64);
            bundle.putString(MyLoader.ARG_KEY, keyBase64);


            LoaderManager.getInstance(this).restartLoader(LoaderID, bundle, this);

        } catch (Exception e) {
            Log.e(TAG, "Ошибка при шифровании", e);
            Toast.makeText(this, "Ошибка при шифровании: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
        Log.d(TAG, "onLoaderReset");
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int i, @Nullable Bundle bundle) {
        if (i == LoaderID) {
            Toast.makeText(this, "onCreateLoader: " + i, Toast.LENGTH_SHORT).show();
            return new MyLoader(this, bundle);
        }
        throw new InvalidParameterException("Invalid Loader id");
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String s) {
        if (loader.getId() == LoaderID) {
            Log.d(TAG, "onLoadFinished: " + s);


            View rootView = findViewById(R.id.main);
            Snackbar.make(rootView, "Расшифрованная фраза: " + s, Snackbar.LENGTH_LONG)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    })
                    .show();


             Toast.makeText(this, "Расшифрованная фраза: " + s, Toast.LENGTH_LONG).show();
        }
    }
}