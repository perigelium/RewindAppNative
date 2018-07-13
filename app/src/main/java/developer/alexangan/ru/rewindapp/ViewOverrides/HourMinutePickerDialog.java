package developer.alexangan.ru.rewindapp.ViewOverrides;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.NumberPicker;

import developer.alexangan.ru.rewindapp.R;

public class HourMinutePickerDialog extends DialogFragment
{
    private TimePickerDialog.OnTimeSetListener listener;
    private int hour;
    private int minute;
    private NumberPicker hourPicker;
    private NumberPicker minutePicker;

    public void setListener(TimePickerDialog.OnTimeSetListener listener)
    {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialog = inflater.inflate(R.layout.hour_minute_picker_dialog, null);

        hourPicker = (NumberPicker) dialog.findViewById(R.id.picker_hour);
        minutePicker = (NumberPicker) dialog.findViewById(R.id.picker_minute);

        if (getArguments() != null)
        {
            hour = getArguments().getInt("hour");
            minute = getArguments().getInt("minute");
        }

        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);

        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);

        hourPicker.setValue(hour);
        minutePicker.setValue(minute);

        builder.setView(dialog)
                // Add action buttons
                .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        listener.onTimeSet(null, hourPicker.getValue(), minutePicker.getValue());
                    }
                })
                .setNeutralButton("Annulla", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        HourMinutePickerDialog.this.getDialog().cancel();
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
