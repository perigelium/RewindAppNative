package developer.alexangan.ru.rewindapp.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import developer.alexangan.ru.rewindapp.Caldroid.CaldroidFragment;
import developer.alexangan.ru.rewindapp.Caldroid.CaldroidListener;
import developer.alexangan.ru.rewindapp.Interfaces.AppointmentsCommunicator;
import developer.alexangan.ru.rewindapp.Models.AppointmentInfoItem;
import developer.alexangan.ru.rewindapp.R;

import static developer.alexangan.ru.rewindapp.Models.GlobalConstants.mSettings;

public class FragAppointmentsCalendar extends Fragment implements View.OnClickListener, View.OnTouchListener
{
    private AppointmentsCommunicator mCommunicator;
    Activity activity;
    private List<AppointmentInfoItem> l_appointmentInfoItems;
    private ProgressDialog requestServerDialog;
    AlertDialog alert;
    CaldroidFragment caldroidFragment;
    private int curYear, curMonth;
    private TextView tvCurrentDayOrWeek;
    private boolean isDailyMode;
    private boolean firstStart;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        mCommunicator = (AppointmentsCommunicator) getActivity();

        if (getArguments() != null)
        {
            l_appointmentInfoItems = getArguments().getParcelableArrayList("ListAppointmentInfoItems");
            isDailyMode = getArguments().getBoolean("isDailyMode");
        }

        requestServerDialog = new ProgressDialog(getActivity(), R.style.AppCompatAlertDialogStyle);
        requestServerDialog.setTitle("");
        requestServerDialog.setMessage(getString(R.string.DownloadingDataPleaseWait));
        requestServerDialog.setIndeterminate(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.frag_appointments_calendar_layout, container, false);

        LinearLayout llReturn = (LinearLayout) rootView.findViewById(R.id.llReturn);
        llReturn.setOnClickListener(this);

        Button btnIntornoATe = (Button) rootView.findViewById(R.id.btnIntornoATe);
        btnIntornoATe.setOnClickListener(this);

        ImageButton ibOpenCalendar = (ImageButton) rootView.findViewById(R.id.ibReturnToWeeklyListMode);
        ibOpenCalendar.setOnClickListener(this);

        ImageButton ibAddCommitment = (ImageButton) rootView.findViewById(R.id.ibAddCommitment);
        ibAddCommitment.setOnClickListener(this);

        LinearLayout llOpenAppointmentsSearch = (LinearLayout) rootView.findViewById(R.id.llOpenAppointmentsSearch);
        llOpenAppointmentsSearch.setOnClickListener(this);

        tvCurrentDayOrWeek = (TextView) rootView.findViewById(R.id.tvCurrentDayOrWeek);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        if (isDailyMode)
        {
            tvCurrentDayOrWeek.setText("Oggi");
        } else
        {
            tvCurrentDayOrWeek.setText("Settimana corrente");
        }

        Calendar cal = Calendar.getInstance();
        curYear = cal.get(Calendar.YEAR);
        curMonth = cal.get(Calendar.MONTH);

        // Setup caldroid fragment
        caldroidFragment = new CaldroidFragment();

        // If Activity is created after rotation
        if (savedInstanceState != null)
        {
            caldroidFragment.restoreStatesFromKey(savedInstanceState,
                    "CALDROID_SAVED_STATE");
        }
        // If activity is created from fresh
        else
        {
            Bundle args = new Bundle();

            args.putInt(CaldroidFragment.THEME_RESOURCE, R.style.CaldroidDefault);
            args.putInt(CaldroidFragment.MONTH, curMonth + 1);
            args.putInt(CaldroidFragment.YEAR, curYear);
            args.putBoolean(CaldroidFragment.ENABLE_SWIPE, false);
            args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, false);
            args.putBoolean(CaldroidFragment.DISABLE_DATES, true);
            args.putBoolean(CaldroidFragment.SELECTED_DATES, true);
            args.putInt(CaldroidFragment.START_DAY_OF_WEEK, 2); // calendar starts on Monday

            caldroidFragment.setArguments(args);
        }

        FragmentActivity act = (FragmentActivity) getActivity();
        android.support.v4.app.FragmentTransaction t = act
                .getSupportFragmentManager().beginTransaction();

        // Attach to the activity
        t.replace(R.id.calendar_container, caldroidFragment);
        t.commit();

        final CaldroidListener listener = new CaldroidListener()
        {
            @Override
            public void onCaldroidViewCreated()
            {
                super.onCaldroidViewCreated();
            }

            @Override
            public void onSelectDate(Date date, View view)
            {
                ColorDrawable blueDark = new ColorDrawable(getResources().getColor(R.color.blueDark));

                if (isDailyMode)
                {
                    caldroidFragment.setBackgroundDrawableForDate(blueDark, date);
                    caldroidFragment.setTextColorForDate(R.color.white, date);
                } else
                {
                    markSelectedWeek(date, R.color.blueDark);
                }

                caldroidFragment.refreshView();

                mSettings.edit().putLong("selectedAppointmentDate", date.getTime()).apply();

                new Handler().post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            Thread.sleep(500);
                        } catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                });

                mCommunicator.popFragmentsBackStack();
            }

            @Override
            public void onChangeMonth(int month, int year)
            {
                Calendar calendarCurrentMonth = Calendar.getInstance();
                Calendar calendarSelectedMonth = Calendar.getInstance(Locale.ITALY);
                calendarSelectedMonth.set(Calendar.YEAR, year);
                calendarSelectedMonth.set(Calendar.MONTH, month - 1);

                disablePrevNextMonthDay(calendarSelectedMonth.getTime());

                grayOutPastDatesAndColorCurrentWeek(year, month);

                if (!isDailyMode)
                {
                    markSelectedWeek(calendarCurrentMonth.getTime(), R.color.orangeDark);
                }
            }
        };

        caldroidFragment.setCaldroidListener(listener);
    }

    private void grayOutPastDatesAndColorCurrentWeek(int year, int month)
    {
        Calendar calendarCurrentMonth = Calendar.getInstance(Locale.ITALY);
        Calendar calendarSelectedMonth = Calendar.getInstance(Locale.ITALY);

        int dayOfCurrentMonth = calendarCurrentMonth.get(Calendar.DAY_OF_MONTH);

        calendarSelectedMonth.set(Calendar.YEAR, year);
        calendarSelectedMonth.set(Calendar.MONTH, month - 1);

        int daysInSelectedMonth = calendarSelectedMonth.getActualMaximum(Calendar.DAY_OF_MONTH);

        long selectedMonthMillis = calendarSelectedMonth.getTimeInMillis();
        long currentMonthMillis = calendarCurrentMonth.getTimeInMillis();

        Map<Date, Integer> grayedOutDates = new HashMap<>();

        int dayOfWeek = calendarCurrentMonth.get(Calendar.DAY_OF_WEEK);

        if (!isDailyMode)
        {
            if (dayOfWeek == 1)
            {
                dayOfWeek = 8;
            }

            dayOfCurrentMonth = dayOfCurrentMonth - dayOfWeek + 2;

            int prevMonth = calendarCurrentMonth.get(Calendar.MONTH) - 1;

            if (prevMonth == -1)
            {
                prevMonth = 11;
            }

            int selectedMonth = calendarSelectedMonth.get(Calendar.MONTH);

            if(selectedMonth == prevMonth)
            {
                daysInSelectedMonth = daysInSelectedMonth - dayOfWeek + 2;
            }
        }

        if (selectedMonthMillis < currentMonthMillis)
        {
            for (int i = 1; i <= daysInSelectedMonth; i++)
            {
                calendarSelectedMonth.set(Calendar.DAY_OF_MONTH, i);
                grayedOutDates.put(calendarSelectedMonth.getTime(), R.color.gray80);
            }
        } else
        {
            for (int i = 1; i < dayOfCurrentMonth; i++)
            {
                calendarCurrentMonth.set(Calendar.DAY_OF_MONTH, i);
                grayedOutDates.put(calendarCurrentMonth.getTime(), R.color.gray80);
            }
        }

        if (!isDailyMode)
        {
            Calendar calendarCurrentWeek = Calendar.getInstance(Locale.ITALY);

            calendarCurrentWeek.add(Calendar.DAY_OF_MONTH, -dayOfWeek + 2);

            for (int j = 1; j <= 7; j++)
            {
                grayedOutDates.put(calendarCurrentWeek.getTime(), R.color.white);
                calendarCurrentWeek.add(Calendar.DAY_OF_MONTH, 1);
            }
        }

        caldroidFragment.setTextColorForDates(grayedOutDates);
        caldroidFragment.refreshView();
    }

    private void markSelectedWeek(Date date, int colorResource)
    {
        Calendar calendarW = Calendar.getInstance(Locale.ITALY);
        calendarW.setTime(date);
        Map<Date, Drawable> selectedWeekDates = new HashMap<>();
        ColorDrawable orangeDark = new ColorDrawable(getResources().getColor(colorResource));

/*        ZoneId zoneId = ZoneId.of("America/Los_Angeles");
        Instant instant = Instant.now();
        ZonedDateTime zDateTime = ZonedDateTime.ofInstant(instant, zoneId);
        DayOfWeek day = zDateTime.getDayOfWeek();*/

        int dayOfWeek = calendarW.get(Calendar.DAY_OF_WEEK);

        if (dayOfWeek == 1)
        {
            dayOfWeek = 8;
        }

        calendarW.add(Calendar.DAY_OF_MONTH, -dayOfWeek + 2);

        for (int i = 1; i <= 7; i++)
        {
            selectedWeekDates.put(calendarW.getTime(), orangeDark);
            calendarW.add(Calendar.DAY_OF_MONTH, 1);
        }

        caldroidFragment.setBackgroundDrawableForDates(selectedWeekDates);
        caldroidFragment.refreshView();
    }

    public void disablePrevNextMonthDay(Date date)
    {
        Calendar disableDates = Calendar.getInstance();

        disableDates.setTime(date); // disableDates is a Calendar object
        disableDates.set(Calendar.DAY_OF_MONTH, 1); // 1st day of current month

        caldroidFragment.setMinDate(disableDates.getTime());
        int maxDay = disableDates.getActualMaximum(Calendar.DAY_OF_MONTH);
        disableDates.set(Calendar.DAY_OF_MONTH, maxDay);
        caldroidFragment.setMaxDate(disableDates.getTime()); //last day of current month
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.llReturn)
        {
            mCommunicator.onClose();
            return;
        }

        if (view.getId() == R.id.ibReturnToWeeklyListMode)
        {
            mCommunicator.popFragmentsBackStack();
            return;
        }

        if (view.getId() == R.id.ibAddCommitment)
        {
            mCommunicator.openAppointmentsNewPersonalTask();
            return;
        }

        if (view.getId() == R.id.llOpenAppointmentsSearch)
        {
            mCommunicator.openAppointmentsSearch(FragAppointmentsCalendar.this);
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
}
