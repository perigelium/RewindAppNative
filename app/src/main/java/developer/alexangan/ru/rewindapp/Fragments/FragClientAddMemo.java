package developer.alexangan.ru.rewindapp.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.IOException;
import java.util.Calendar;

import developer.alexangan.ru.rewindapp.Interfaces.ClientsCommunicator;
import developer.alexangan.ru.rewindapp.Models.ClientMemoItem;
import developer.alexangan.ru.rewindapp.R;
import developer.alexangan.ru.rewindapp.Utils.NetworkUtils;
import developer.alexangan.ru.rewindapp.Utils.ViewUtils;
import developer.alexangan.ru.rewindapp.ViewOverrides.DayMonthYearPickerDialog;
import developer.alexangan.ru.rewindapp.ViewOverrides.HourMinutePickerDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static developer.alexangan.ru.rewindapp.Models.GlobalConstants.API_SEND_CLIENT_MEMO;
import static developer.alexangan.ru.rewindapp.Models.GlobalConstants.tokenStr;

public class FragClientAddMemo extends Fragment implements View.OnClickListener, View.OnTouchListener, Callback
{
    private ClientsCommunicator mCommunicator;
    private Activity activity;
    private int id_customer;
    private EditText etMemoTitle;
    private EditText etMemoPlace;
    private EditText etMemoDescription;
    private Call callSendClientMemo;
    private ProgressDialog downloadingDialog;
    private AlertDialog alert;
    private DayMonthYearPickerDialog dayMonthYearPickerDialog;
    private HourMinutePickerDialog hourMinutePickerDialog;
    private String dateYYYYMMDD;
    private int mYear, mMonth, mDayOfMonth;
    private int mMinute;
    private String timeHHMMSS;
    private int mHour;
    private LinearLayout llSelectDate, llSelectTime;
    private TextView tvMemoDate, tvMemoTime;
    private ImageButton tvSendClientMemo;
    private TextView tvMemoTitle;
    private String company_name;
    private TextView tvMemoCompanyTitle;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        mCommunicator = (ClientsCommunicator) getActivity();

        if (getArguments() != null)
        {
            id_customer = getArguments().getInt("id_customer");
            company_name = getArguments().getString("company_name", " ");
        }

        downloadingDialog = new ProgressDialog(getActivity(), R.style.AppCompatAlertDialogStyle);
        downloadingDialog.setTitle("");
        downloadingDialog.setMessage(getString(R.string.DownloadingDataPleaseWait));
        downloadingDialog.setIndeterminate(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.frag_client_add_memo, container, false);

        LinearLayout llReturn = (LinearLayout) rootView.findViewById(R.id.llReturn);
        llReturn.setOnClickListener(this);

        tvSendClientMemo = (ImageButton) rootView.findViewById(R.id.tvSendClientMemo);
        tvSendClientMemo.setOnClickListener(this);

        llSelectDate = (LinearLayout) rootView.findViewById(R.id.llSelectDate);
        llSelectDate.setOnTouchListener(this);
        llSelectDate.setOnClickListener(this);

        llSelectTime = (LinearLayout) rootView.findViewById(R.id.llSelectTime);
        llSelectTime.setOnTouchListener(this);
        llSelectTime.setOnClickListener(this);

        tvMemoDate = (TextView) rootView.findViewById(R.id.tvMemoDate);
        tvMemoTime = (TextView) rootView.findViewById(R.id.tvMemoTime);

        tvMemoCompanyTitle = (TextView) rootView.findViewById(R.id.tvMemoCompanyTitle);
        etMemoTitle = (EditText) rootView.findViewById(R.id.etMemoTitle);
        etMemoPlace = (EditText) rootView.findViewById(R.id.etMemoPlace);
        etMemoDescription = (EditText) rootView.findViewById(R.id.etMemoDescription);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        tvMemoCompanyTitle.setText(company_name);

        Calendar calendarNow = Calendar.getInstance(); // now
        mYear = calendarNow.get(Calendar.YEAR);
        mMonth = calendarNow.get(Calendar.MONTH);
        mDayOfMonth = calendarNow.get(Calendar.DAY_OF_MONTH);
        mHour = calendarNow.get(Calendar.HOUR_OF_DAY);
        mMinute = calendarNow.get(Calendar.MINUTE);

        DatePickerDialog.OnDateSetListener date_listener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
            {
                String strdayOfMonth = String.valueOf(dayOfMonth);
                String strMonth = String.valueOf(month + 1);
                String strYear = String.valueOf(year);

                if (strMonth.length() == 1)
                {
                    strMonth = "0" + strMonth;
                }

                dateYYYYMMDD = strYear + "-" + strMonth + "-" + strdayOfMonth;

                String strDate = strdayOfMonth + "." + strMonth + "." + strYear;
                tvMemoDate.setText(strDate);

                //Log.d("DEBUG", dateYYYYMMDD);
            }
        };

        dayMonthYearPickerDialog = new DayMonthYearPickerDialog();
        dayMonthYearPickerDialog.setListener(date_listener);

        TimePickerDialog.OnTimeSetListener time_listener = new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute)
            {
                String strHour = String.valueOf(hour);

                if (strHour.length() == 1)
                {
                    strHour = "0" + strHour;
                }

                String strMinute = String.valueOf(minute);

                if (strMinute.length() == 1)
                {
                    strMinute = "0" + strMinute;
                }

                timeHHMMSS = strHour + ":" + strMinute + ":" + "00";

                String strTime = strHour + ":" + strMinute;
                tvMemoTime.setText(strTime);

                //Log.d("DEBUG", timeHHMMSS);
            }
        };

        hourMinutePickerDialog = new HourMinutePickerDialog();
        hourMinutePickerDialog.setListener(time_listener);

        etMemoTitle.requestFocus();
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.llReturn)
        {
            mCommunicator.popFragmentsBackStack();
            return;
        }

        if (view.getId() == R.id.llSelectDate)
        {
            Bundle bundle = new Bundle();
            bundle.putInt("year", mYear);
            bundle.putInt("month", mMonth);
            bundle.putInt("dayOfMonth", mDayOfMonth);
            dayMonthYearPickerDialog.setArguments(bundle);
            dayMonthYearPickerDialog.show(getFragmentManager(), "DayMonthYearPickerDialog");

            view.setBackgroundColor(Color.parseColor("#ffffffff"));

            return;
        }

        if (view.getId() == R.id.llSelectTime)
        {
            Bundle bundle = new Bundle();
            bundle.putInt("hour", mHour);
            bundle.putInt("minute", mMinute);
            hourMinutePickerDialog.setArguments(bundle);
            hourMinutePickerDialog.show(getFragmentManager(), "HourMinutePickerDialog");

            view.setBackgroundColor(Color.parseColor("#ffffffff"));

            return;
        }

        if (view.getId() == R.id.tvSendClientMemo)
        {
            String memoTitle = etMemoTitle.getText().toString();
            String memoPlace = etMemoPlace.getText().toString();
            String memoDescription = etMemoDescription.getText().toString();

            if (memoTitle.length() != 0 && dateYYYYMMDD!=null && timeHHMMSS!=null
                    && memoPlace.length() != 0 && memoDescription.length() != 0)
            {
                ClientMemoItem clientMemoItem =
                        new ClientMemoItem(id_customer, memoTitle, dateYYYYMMDD, timeHHMMSS, memoPlace, memoDescription);

                sendClientMemo(clientMemoItem);
            }
            return;
        }
    }

    private void sendClientMemo(ClientMemoItem clientMemoItem)
    {
        if (NetworkUtils.isNetworkAvailable(activity))
        {
            if (tokenStr == null)
            {
                alertDialog("Info", getString(R.string.OfflineModeShowLoginScreenQuestion));
            } else
            {
                downloadingDialog.show();

                NetworkUtils networkUtils = new NetworkUtils();

                callSendClientMemo = networkUtils.sendClientMemo(this, API_SEND_CLIENT_MEMO, clientMemoItem);
            }
        } else
        {
            ViewUtils.showToastMessage(activity, getString(R.string.CheckInternetConnection));
        }
    }

    @Override
    public void onFailure(Call call, IOException e)
    {
        if (call == callSendClientMemo)
        {
            ViewUtils.showToastMessage(activity, getString(R.string.ServerAnswerNotReceived));

            downloadingDialog.dismiss();
        }
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException
    {
        if (call == callSendClientMemo)
        {
            final String newsJSON = response.body().string();

            response.body().close();

            downloadingDialog.dismiss();

            if (newsJSON.equals("true"))
            {
                ViewUtils.showToastMessage(activity, getString(R.string.MemoAddedSuccessfully));
            } else
            {
                ViewUtils.showToastMessage(activity, getString(R.string.MemoAddingFailed));
            }
        }
    }

    private void alertDialog(String title, String message)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle);

        builder.setTitle(title)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("Si",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                mCommunicator.onLogoutCommand();
                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                alert.dismiss();
                            }
                        });

        alert = builder.create();

        alert.show();
    }

    @Override
    public boolean onTouch(View view, MotionEvent event)
    {
        if (view.getId() == R.id.llSelectDate || view.getId() == R.id.llSelectTime)
        {
            view.setBackgroundColor(Color.parseColor("#ffcde6f9"));
        }

        return false;
    }
}
