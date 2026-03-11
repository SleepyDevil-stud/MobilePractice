package ru.mirea.kozlovrd.multidialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.snackbar.Snackbar;

public class ProgressDialogFr extends DialogFragment {

    private ProgressDialog progressDialog;
    private Handler handler = new Handler();
    private int progressStatus = 0;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        progressDialog = new ProgressDialog(requireActivity());
        progressDialog.setTitle("МИРЭА");
        progressDialog.setMessage("Студент № 15 Группа БСБО-52-24 \nЗагрузка...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);


        startProgressSimulation();

        return progressDialog;
    }

    private void startProgressSimulation() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 1;


                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (progressDialog != null) {
                                progressDialog.setProgress(progressStatus);

                                // Когда загрузка завершена
                                if (progressStatus == 100) {
                                    progressDialog.setMessage("Загрузка завершена!");


                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            dismiss();

                                            if (getActivity() != null) {
                                                View view = getActivity().findViewById(android.R.id.content);
                                                if (view != null) {
                                                    Snackbar.make(view, "Загрузка завершена!", Snackbar.LENGTH_LONG).show();
                                                }
                                            }
                                        }
                                    }, 1000);
                                }
                            }
                        }
                    });

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}