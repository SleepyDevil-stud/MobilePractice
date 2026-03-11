package ru.mirea.kozlovrd.multidialog;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;

public class TimeDialog extends DialogFragment implements TimePickerDialog.OnTimeSetListener
{
    Calendar dateAndTime = Calendar.getInstance();
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        int hour = dateAndTime.get(Calendar.HOUR_OF_DAY);
        int minutes = dateAndTime.get(Calendar.MINUTE);
        return new TimePickerDialog(requireActivity(), this, hour, minutes, true);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String time = hourOfDay + ":" + (minute<10 ? "0" + minute : minute);
        if(getActivity() != null){
            Snackbar.make(getActivity().findViewById(android.R.id.content),"Выбрано время:"
            + time, Snackbar.LENGTH_SHORT).show();
        }
    }
}
