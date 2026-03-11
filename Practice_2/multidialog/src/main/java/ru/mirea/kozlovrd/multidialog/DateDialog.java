package ru.mirea.kozlovrd.multidialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;

public class DateDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener
{
    Calendar dateAndTime = Calendar.getInstance();
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        int year = dateAndTime.get(Calendar.YEAR);
        int month = dateAndTime.get(Calendar.MONTH);
        int day = dateAndTime.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(requireActivity(), this, year, month, day);
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = dayOfMonth + "." + (month+1) + "." + year;
        if(getActivity() != null){
            Snackbar.make(getActivity().findViewById(android.R.id.content),"Выбрана дата:"
                    + date, Snackbar.LENGTH_SHORT).show();
        }
    }
}
