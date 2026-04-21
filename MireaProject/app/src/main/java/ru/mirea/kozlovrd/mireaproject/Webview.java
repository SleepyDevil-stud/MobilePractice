package ru.mirea.kozlovrd.mireaproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import android.webkit.WebView;
import android.webkit.WebViewClient;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Webview#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Webview extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Webview() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Webview.
     */
    // TODO: Rename and change types and number of parameters
    public static Webview newInstance(String param1, String param2) {
        Webview fragment = new Webview();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private WebView webView;
    private EditText urlInput;
    private Button goButton;
    private ImageButton backButton, forwardButton, reloadButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);

        // Инициализация элементов
        webView = view.findViewById(R.id.webView);
        urlInput = view.findViewById(R.id.urlInput);
        goButton = view.findViewById(R.id.goButton);
        backButton = view.findViewById(R.id.backButton);
        forwardButton = view.findViewById(R.id.forwardButton);
        reloadButton = view.findViewById(R.id.reloadButton);

        // Настройка WebView
        setupWebView();

        // Обработчики нажатий
        setupListeners();

        // Загрузка страницы по умолчанию
        loadUrl("https://www.google.com");

        return view;
    }
    private void setupWebView() {
        // Включаем поддержку JavaScript (нужно для многих сайтов)
        webView.getSettings().setJavaScriptEnabled(true);

        // Настройка WebViewClient для загрузки страниц внутри WebView
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // Обновляем URL в поле ввода после загрузки страницы
                urlInput.setText(url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Загружаем все ссылки внутри WebView
                view.loadUrl(url);
                return true;
            }
        });

        // Включаем жесты увеличения
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false); // Скрываем кнопки зума

        // Кеширование
        webView.getSettings().setCacheMode(android.webkit.WebSettings.LOAD_DEFAULT);
    }
    private void setupListeners() {
        // Кнопка "Перейти"
        goButton.setOnClickListener(v -> {
            String url = urlInput.getText().toString();
            loadUrl(url);
        });

        // Кнопка "Назад"
        backButton.setOnClickListener(v -> {
            if (webView.canGoBack()) {
                webView.goBack();
            }
        });

        // Кнопка "Вперёд"
        forwardButton.setOnClickListener(v -> {
            if (webView.canGoForward()) {
                webView.goForward();
            }
        });

        // Кнопка "Обновить"
        reloadButton.setOnClickListener(v -> {
            webView.reload();
        });

        // Обработка ввода с клавиатуры
        urlInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_GO) {
                loadUrl(urlInput.getText().toString());
                return true;
            }
            return false;
        });
    }

    private void loadUrl(String url) {
        // Добавляем https:// если URL не начинается с http
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }
        webView.loadUrl(url);
    }
}