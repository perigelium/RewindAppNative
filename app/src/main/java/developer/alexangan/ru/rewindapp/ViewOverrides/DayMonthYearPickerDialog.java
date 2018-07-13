package developer.alexangan.ru.rewindapp.ViewOverrides;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.NumberPicker;

import java.util.Calendar;

import developer.alexangan.ru.rewindapp.R;

public class DayMonthYearPickerDialog extends DialogFragment
{
    private DatePickerDialog.OnDateSetListener listener;
    private int year;
    private int month;
    private int dayOfMonth;
    private Calendar calendar;
    private NumberPicker monthPicker, yearPicker;
    private NumberPicker dayPicker;

    public void setListener(DatePickerDialog.OnDateSetListener listener)
    {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        calendar = Calendar.getInstance();

        View dialog = inflater.inflate(R.layout.day_month_year_picker_dialog, null);

        dayPicker = (NumberPicker) dialog.findViewById(R.id.picker_day);
        monthPicker = (NumberPicker) dialog.findViewById(R.id.picker_month);
        yearPicker = (NumberPicker) dialog.findViewById(R.id.picker_year);

        monthPicker.setMinValue(0);
        monthPicker.setMaxValue(11);
        monthPicker.setDisplayedValues(new String[]{"GEN", "FEB", "MAR", "APR", "MAG", "GIU", "LUG",
                "AGO", "SET", "OTT", "NOV", "DIC"});

        if (getArguments() != null)
        {
            year = getArguments().getInt("year");
            month = getArguments().getInt("month");
            dayOfMonth = getArguments().getInt("dayOfMonth");
        }

        yearPicker.setMinValue(year - 1);
        yearPicker.setMaxValue(2099);

        dayPicker.setMinValue(1);
        dayPicker.setMaxValue(calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

        yearPicker.setValue(year);
        monthPicker.setValue(month);
        dayPicker.setValue(dayOfMonth);

        builder.setView(dialog)
                // Add action buttons
                .setPositiveButton(" Ok ", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        listener.onDateSet(null, yearPicker.getValue(), monthPicker.getValue(), dayPicker.getValue());
                    }
                })
                .setNeutralButton("Annulla", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        DayMonthYearPickerDialog.this.getDialog().cancel();
                    }
                })
                .setNegativeButton("Attuale             ", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        year = calendar.get(Calendar.YEAR);
                        month = calendar.get(Calendar.MONTH);
                        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

/*                        int prevMonth = month - 1;

                        if (prevMonth == -1)
                        {
                            prevMonth = 11;
                        }*/

                        listener.onDateSet(null, year, month, dayOfMonth);
                    }
                });


        return builder.create();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#fff26f31"));
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTransformationMethod(null);
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.parseColor("#ff80afd3"));
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEUTRAL).setTransformationMethod(null);
        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(R.color.blueDark);
    }
}
