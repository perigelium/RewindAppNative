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

public class YearPickerDialog extends DialogFragment
{
    private static final int MIN_YEAR = 2010;
    private DatePickerDialog.OnDateSetListener listener;
    private int year;
    //private int month;
    private Calendar calendar;
    private NumberPicker yearPicker;

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

        View dialog = inflater.inflate(R.layout.year_picker_dialog, null);
        //monthPicker = (NumberPicker) dialog.findViewById(R.id.picker_month);
        yearPicker = (NumberPicker) dialog.findViewById(R.id.picker_year);

        if (getArguments() != null)
        {
            year = getArguments().getInt("year");
            //month = getArguments().getInt("month");
        }
        final int currentYear = calendar.get(Calendar.YEAR);

        yearPicker.setMinValue(MIN_YEAR);
        yearPicker.setMaxValue(currentYear + 1);

        yearPicker.setValue(year);
        //monthPicker.setValue(month);

        builder.setView(dialog)
                // Add action buttons
                .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        listener.onDateSet(null, yearPicker.getValue(), 0, 0);
                    }
                })
                .setNeutralButton("Annulla", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        YearPickerDialog.this.getDialog().cancel();
                    }
                })
                .setNegativeButton("Attuale             ", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        listener.onDateSet(null, currentYear, 0, 0);
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
