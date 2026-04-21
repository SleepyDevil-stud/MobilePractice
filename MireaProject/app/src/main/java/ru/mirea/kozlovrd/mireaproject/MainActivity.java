package ru.mirea.kozlovrd.mireaproject;

import android.os.Bundle;



import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;


import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.view.MenuItem;
import androidx.annotation.NonNull;

import com.google.android.material.navigation.NavigationView;



import ru.mirea.kozlovrd.mireaproject.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Настройка Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Настройка DrawerLayout
        drawerLayout = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Кнопка-гамбургер
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Загружаем стартовый фрагмент
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new data())
                    .commit();
            // Подсвечиваем выбранный пункт меню
            navigationView.setCheckedItem(R.id.nav_slideshow);
        }

        // Обработка нажатий в меню
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int id = item.getItemId();

                if (id == R.id.nav_slideshow) {
                    selectedFragment = new data();
                    Toast.makeText(MainActivity.this, "IT Отрасль", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_gallery) {
                    // TODO: Создайте GalleryFragment, если нужна галерея
                    // Пока временно показываем сообщение
                    Toast.makeText(MainActivity.this, "Галерея (в разработке)", Toast.LENGTH_SHORT).show();
                    selectedFragment = new data(); // временно, замените на GalleryFragment
                } else if (id == R.id.nav_tools) {
                    selectedFragment = new ToolsFragment();
                    Toast.makeText(MainActivity.this, "Инструменты", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_browser) {  // ← ДОБАВЛЕНА ОБРАБОТКА для браузера
                    selectedFragment = new Webview();
                    Toast.makeText(MainActivity.this, "Браузер", Toast.LENGTH_SHORT).show();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.nav_host_fragment, selectedFragment)
                            .commit();
                }

                // Закрываем меню
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }
}