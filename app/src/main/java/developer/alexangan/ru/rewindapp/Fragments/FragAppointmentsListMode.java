package developer.alexangan.ru.rewindapp.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import developer.alexangan.ru.rewindapp.Interfaces.AppointmentsCommunicator;
import developer.alexangan.ru.rewindapp.Models.AppointmentInfoItem;
import developer.alexangan.ru.rewindapp.R;
import developer.alexangan.ru.rewindapp.Utils.MyTextUtils;

import static developer.alexangan.ru.rewindapp.Models.GlobalConstants.mSettings;

public class FragAppointmentsListMode extends Fragment implements View.OnClickListener, View.OnTouchListener
{
    private AppointmentsCommunicator mCommunicator;
    Activity activity;
    private List<AppointmentInfoItem> l_appointmentInfoItems;
    private ProgressDialog requestServerDialog;
    AlertDialog alert;
    private static boolean isDailyMode;
    private TextView tvAppointmentsDailyMode;
    private TextView tvAppointmentsWeeklyMode;
    private ImageView tvAppointmentsWeeklyModeUnderline;
    private ImageView tvAppointmentsDailyModeUnderline;
    private long selectedDateMillis;
    private TextView tvSelectedDayOrWeek;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        mCommunicator = (AppointmentsCommunicator) getActivity();

        if (getArguments() != null)
        {
            l_appointmentInfoItems = getArguments().getParcelableArrayList("ListAppointmentInfoItems");
        }

        requestServerDialog = new ProgressDialog(getActivity(), R.style.AppCompatAlertDialogStyle);
        requestServerDialog.setTitle("");
        requestServerDialog.setMessage(getString(R.string.DownloadingDataPleaseWait));
        requestServerDialog.setIndeterminate(true);

        isDailyMode = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.frag_appointments_list_mode_layout, container, false);

        LinearLayout llReturn = (LinearLayout) rootView.findViewById(R.id.llReturn);
        llReturn.setOnClickListener(this);

        Button btnIntornoATe = (Button) rootView.findViewById(R.id.btnIntornoATe);
        btnIntornoATe.setOnClickListener(this);

        LinearLayout llOpenAppointmentsSearch = (LinearLayout) rootView.findViewById(R.id.llOpenAppointmentsSearch);
        llOpenAppointmentsSearch.setOnClickListener(this);

        tvAppointmentsDailyModeUnderline = (ImageView) rootView.findViewById(R.id.tvAppointmentsDailyModeUnderline);
        tvAppointmentsDailyMode = (TextView) rootView.findViewById(R.id.tvAppointmentsDailyMode);
        tvAppointmentsDailyMode.setOnClickListener(this);

        tvAppointmentsWeeklyModeUnderline = (ImageView) rootView.findViewById(R.id.tvAppointmentsWeeklyModeUnderline);
        tvAppointmentsWeeklyMode = (TextView) rootView.findViewById(R.id.tvAppointmentsWeeklyMode);
        tvAppointmentsWeeklyMode.setOnClickListener(this);

        tvSelectedDayOrWeek = (TextView) rootView.findViewById(R.id.tvSelectedDayOrWeek);

        ImageButton ibOpenCalendar = (ImageButton) rootView.findViewById(R.id.ibOpenCalendar);
        ibOpenCalendar.setOnClickListener(this);

        ImageButton ibAddCommitment = (ImageButton) rootView.findViewById(R.id.ibAddPersonalTask);
        ibAddCommitment.setOnClickListener(this);

        FloatingActionButton fabMapMode = (FloatingActionButton) rootView.findViewById(R.id.fabMapMode);
        fabMapMode.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        if (isDailyMode)
        {
            tvAppointmentsDailyMode.performClick();
        } else
        {
            tvAppointmentsWeeklyMode.performClick();
        }

/*        boolean searchMode = mSettings.getBoolean("SearchMode", false);

        if( ! searchMode)
        {
            tvReturnPageTitle.setText("Mappa");
        }*/
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        displaySelectedPeriod();
    }

    private void displaySelectedPeriod()
    {
        selectedDateMillis = mSettings.getLong("selectedAppointmentDate", 0);
        Calendar calendar = Calendar.getInstance();

        if (selectedDateMillis != 0)
        {
            calendar.setTimeInMillis(selectedDateMillis);
        }

        String periodDisplayed = "";

        if(isDailyMode)
        {
            periodDisplayed = getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK), Locale.ITALIAN) + " "
                    + calendar.get(Calendar.DAY_OF_MONTH) + " " + getMonth(calendar.get(Calendar.MONTH), Locale.ITALIAN)
                    + " " + calendar.get(Calendar.YEAR);

            periodDisplayed = MyTextUtils.toDisplayCase(periodDisplayed);
        }
        else
        {
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == 1) {
                dayOfWeek = 8;
            }
            calendar.add(Calendar.DATE, -dayOfWeek+2);
            int firstDayOfWeek = calendar.get(Calendar.DAY_OF_MONTH);
            String firstMonth = getMonth(calendar.get(Calendar.MONTH), Locale.ITALIAN);
            firstMonth = MyTextUtils.toDisplayCase(firstMonth);

            calendar.add(Calendar.DATE, 6);
            int lastDayOfWeek = calendar.get(Calendar.DAY_OF_MONTH);
            String secondMonth = getMonth(calendar.get(Calendar.MONTH), Locale.ITALIAN);
            secondMonth = MyTextUtils.toDisplayCase(secondMonth);

            periodDisplayed = "Settimana dal " + firstDayOfWeek + " " + firstMonth +
                    " al " + lastDayOfWeek + " " + secondMonth;
        }

        tvSelectedDayOrWeek.setText(periodDisplayed);
    }

    public String getMonth(int month, Locale locale)
    {
        return DateFormatSymbols.getInstance(locale).getMonths()[month];
    }

    public String getDayOfWeek(int dow, Locale locale)
    {
        return DateFormatSymbols.getInstance(locale).getWeekdays()[dow];
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.llReturn)
        {
            mCommunicator.onClose();
            return;
        }

        if (view.getId() == R.id.fabMapMode)
        {
            mCommunicator.popFragmentsBackStack();
            return;
        }

        if (view.getId() == R.id.llOpenAppointmentsSearch)
        {
            mCommunicator.openAppointmentsSearch(FragAppointmentsListMode.this);
            return;
        }

        if (view.getId() == R.id.tvAppointmentsDailyMode)
        {
            tvAppointmentsWeeklyModeUnderline.setVisibility(View.INVISIBLE);
            tvAppointmentsDailyModeUnderline.setVisibility(View.VISIBLE);
            tvAppointmentsDailyMode.setAlpha(1.0f);
            tvAppointmentsWeeklyMode.setAlpha(0.5f);
            isDailyMode = true;
            displaySelectedPeriod();
            //mCommunicator.openAppointmentsListMode(l_appointmentInfoItems, true);
            return;
        }

        if (view.getId() == R.id.tvAppointmentsWeeklyMode)
        {
            tvAppointmentsDailyModeUnderline.setVisibility(View.INVISIBLE);
            tvAppointmentsWeeklyModeUnderline.setVisibility(View.VISIBLE);
            tvAppointmentsDailyMode.setAlpha(0.5f);
            tvAppointmentsWeeklyMode.setAlpha(1.0f);
            isDailyMode = false;
            displaySelectedPeriod();
            //mCommunicator.openAppointmentsListMode(l_appointmentInfoItems, false);
            return;
        }

        if (view.getId() == R.id.ibOpenCalendar)
        {
            mCommunicator.openAppointmentsCalendar(l_appointmentInfoItems, isDailyMode);
            return;
        }

        if (view.getId() == R.id.ibAddPersonalTask)
        {
            mCommunicator.openAppointmentsNewPersonalTask();
            return;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event)
    {
/*        if (view.getId() == R.id.llDocuments || view.getId() == R.id.llPractices || view.getId() == R.id.llAltuofianco)
        {
            view.setBackgroundColor(Color.parseColor("#ffcde6f9"));
        }*/

        return false;
    }

/*    private void alertDialogAddCommitment(String title)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle);

        LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.personal_commitment_dialog_layout, null);

        final EditText dateDDMMYYYY = (EditText) dialogView.findViewById(R.id.dialogusername);
        final EditText time = (EditText) dialogView.findViewById(R.id.dialogpassword);

        builder.setView(dialogView);

        builder.setTitle(title)
                //.setMessage(message)
                //.setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("Salva",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                // save data
                            }
                        });


        alert = builder.create();

        alert.show();
    }*/
}
