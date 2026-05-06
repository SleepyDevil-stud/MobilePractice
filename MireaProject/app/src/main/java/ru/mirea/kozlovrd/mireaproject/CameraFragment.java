package ru.mirea.kozlovrd.mireaproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CameraFragment extends Fragment {

    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private LinearLayout collageContainer;
    private Button takePhotoButton;
    private Button createCollageButton;
    private Button clearButton;

    private List<Bitmap> takenPhotos = new ArrayList<>();
    private String currentPhotoPath;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        collageContainer = view.findViewById(R.id.collageContainer);
        takePhotoButton = view.findViewById(R.id.takePhotoButton);
        createCollageButton = view.findViewById(R.id.createCollageButton);
        clearButton = view.findViewById(R.id.clearButton);

        takePhotoButton.setOnClickListener(v -> checkCameraPermission());
        createCollageButton.setOnClickListener(v -> createCollage());
        clearButton.setOnClickListener(v -> clearPhotos());

        return view;
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(getContext(), "Нужно разрешение для использования камеры", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(getContext(), "Ошибка создания файла", Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(requireContext(),
                        "ru.mirea.kozlovrd.mireaproject.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            Bitmap photoBitmap = BitmapFactory.decodeFile(currentPhotoPath);
            if (photoBitmap != null) {
                // Масштабируем фото для отображения
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(photoBitmap, 200, 200, true);
                takenPhotos.add(scaledBitmap);
                displayThumbnails();
            }
        }
    }

    private void displayThumbnails() {
        collageContainer.removeAllViews();
        for (int i = 0; i < takenPhotos.size(); i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageBitmap(takenPhotos.get(i));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200, 200);
            params.setMargins(8, 8, 8, 8);
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            final int index = i;
            imageView.setOnLongClickListener(v -> {
                takenPhotos.remove(index);
                displayThumbnails();
                Toast.makeText(getContext(), "Фото удалено", Toast.LENGTH_SHORT).show();
                return true;
            });

            collageContainer.addView(imageView);
        }

        if (takenPhotos.isEmpty()) {
            Toast.makeText(getContext(), "Нет фото. Сделайте несколько снимков", Toast.LENGTH_SHORT).show();
        }
    }

    private void createCollage() {
        if (takenPhotos.size() < 2) {
            Toast.makeText(getContext(), "Нужно минимум 2 фото для создания коллажа", Toast.LENGTH_LONG).show();
            return;
        }

        int width = 800;
        int height = 600;
        Bitmap collageBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(collageBitmap);
        canvas.drawColor(Color.WHITE);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(40);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextAlign(Paint.Align.CENTER);

        // Рисуем рамку
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        canvas.drawRect(10, 10, width - 10, height - 10, paint);

        paint.setStyle(Paint.Style.FILL);
        canvas.drawText("Мой Коллаж", width / 2, 60, paint);

        int cols = Math.min(2, takenPhotos.size());
        int rows = (int) Math.ceil((double) takenPhotos.size() / cols);
        int photoWidth = (width - 40) / cols;
        int photoHeight = (height - 120) / rows;

        for (int i = 0; i < takenPhotos.size(); i++) {
            int row = i / cols;
            int col = i % cols;
            int x = 20 + col * photoWidth;
            int y = 90 + row * photoHeight;

            Bitmap scaledPhoto = Bitmap.createScaledBitmap(takenPhotos.get(i), photoWidth, photoHeight, true);
            canvas.drawBitmap(scaledPhoto, x, y, null);
        }

        saveCollage(collageBitmap);
    }

    private void saveCollage(Bitmap bitmap) {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File collageFile = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "collage_" + timeStamp + ".png");
            FileOutputStream out = new FileOutputStream(collageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
            Toast.makeText(getContext(), "Коллаж сохранён: " + collageFile.getName(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Ошибка сохранения коллажа", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearPhotos() {
        takenPhotos.clear();
        collageContainer.removeAllViews();
        Toast.makeText(getContext(), "Все фото удалены", Toast.LENGTH_SHORT).show();
    }
}