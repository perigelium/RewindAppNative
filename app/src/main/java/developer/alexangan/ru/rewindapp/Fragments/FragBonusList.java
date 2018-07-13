package developer.alexangan.ru.rewindapp.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import developer.alexangan.ru.rewindapp.Adapters.BonusListAdapter;
import developer.alexangan.ru.rewindapp.Interfaces.BonusAndGareCommunicator;
import developer.alexangan.ru.rewindapp.Interfaces.RetrofitAPI;
import developer.alexangan.ru.rewindapp.Models.BonusItem;
import developer.alexangan.ru.rewindapp.Models.GlobalConstants;
import developer.alexangan.ru.rewindapp.Models.ItalianMonths;
import developer.alexangan.ru.rewindapp.R;
import developer.alexangan.ru.rewindapp.Utils.MyTextUtils;
import developer.alexangan.ru.rewindapp.Utils.NetworkUtils;
import developer.alexangan.ru.rewindapp.Utils.OnSwipeTouchListener;
import developer.alexangan.ru.rewindapp.Utils.ViewUtils;
import developer.alexangan.ru.rewindapp.ViewOverrides.MonthYearPickerDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static developer.alexangan.ru.rewindapp.Models.GlobalConstants.tokenStr;

public class FragBonusList extends ListFragment implements View.OnClickListener, Callback<List<BonusItem>>
{
    private BonusAndGareCommunicator mCommunicator;
    private Activity activity;
    Call<List<BonusItem>> callGetBonusesList;
    AlertDialog alert;
    List<BonusItem> l_bonusItems;
    private ProgressDialog downloadingDialog;
    private TextView tvBonusTotal;
    private int mYear;
    private int mMonth;
    private String dateMMYYYY;
    private TextView tvBonusMonth;
    private FrameLayout flMonthSlider;
    private Calendar calendarNow;
    private MonthYearPickerDialog pd;
    private boolean onInitialDownload;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        mCommunicator = (BonusAndGareCommunicator) getActivity();

        downloadingDialog = new ProgressDialog(getActivity(), R.style.AppCompatAlertDialogStyle);
        downloadingDialog.setTitle("");
        downloadingDialog.setMessage(getString(R.string.DownloadingDataPleaseWait));
        downloadingDialog.setIndeterminate(true);

        onInitialDownload = true;
        l_bonusItems = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.frag_bonus_list, container, false);

        LinearLayout llReturn = (LinearLayout) rootView.findViewById(R.id.llReturn);
        llReturn.setOnClickListener(this);

        FrameLayout flListHeader = (FrameLayout) rootView.findViewById(R.id.flListHeader);
        flListHeader.setOnClickListener(this);

        FrameLayout flPrevDate = (FrameLayout) rootView.findViewById(R.id.flPrevDate);
        flPrevDate.setOnClickListener(this);
        FrameLayout flNextDate = (FrameLayout) rootView.findViewById(R.id.flNextDate);
        flNextDate.setOnClickListener(this);

        tvBonusTotal = (TextView) rootView.findViewById(R.id.tvBonusTotal);

        flMonthSlider = (FrameLayout) rootView.findViewById(R.id.flMonthSlider);

        tvBonusMonth = (TextView) rootView.findViewById(R.id.tvMonthSelected);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        calendarNow = Calendar.getInstance(); // now
        mYear = calendarNow.get(Calendar.YEAR);
        mMonth = calendarNow.get(Calendar.MONTH) - 1; // previous month

        if (mMonth == -1)
        {
            mMonth = 11;
        }

        dateMMYYYY = ItalianMonths.numToLongString(mMonth) + " " + mYear;

        tvBonusMonth.setText(dateMMYYYY);
        tvBonusMonth.setOnClickListener(this);

        flMonthSlider.setOnTouchListener(new OnSwipeTouchListener(activity)
        {
            @Override
            public void onSwipeLeft()
            {
                mMonth++;

                if (mMonth > 11)
                {
                    mMonth = 0;
                    mYear++;
                }
                dateMMYYYY = ItalianMonths.numToLongString(mMonth) + " " + mYear;
                tvBonusMonth.setText(dateMMYYYY);

                downloadBonusList();
            }

            @Override
            public void onSwipeRight()
            {
                mMonth--;

                if (mMonth < 0)
                {
                    mMonth = 11;
                    mYear--;
                }

                dateMMYYYY = ItalianMonths.numToLongString(mMonth) + " " + mYear;
                tvBonusMonth.setText(dateMMYYYY);

                downloadBonusList();
            }
        });

        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
            {
                mYear = year;
                mMonth = month;

                dateMMYYYY = ItalianMonths.numToLongString(month) + " " + year;
                tvBonusMonth.setText(dateMMYYYY);

                downloadBonusList();
            }
        };

        pd = new MonthYearPickerDialog();
        pd.setListener(listener);

        if (onInitialDownload)
        {
            downloadBonusList();
        }
        else
        {
            updateBonusListView();
        }
    }

    private void downloadBonusList()
    {
        if (NetworkUtils.isNetworkAvailable(activity))
        {
            if (tokenStr == null)
            {
                alertDialog("Info", getString(R.string.OfflineModeShowLoginScreenQuestion));
            } else
            {
                l_bonusItems.clear();
                updateBonusListView();

                downloadingDialog.show();

                String strMonth = String.valueOf(mMonth + 1);

                if (strMonth.length() == 1)
                {
                    strMonth = "0" + strMonth;
                }
                final String dateYYYYMM = String.valueOf(mYear) + strMonth;

                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        RetrofitAPI retrofitService = RetrofitAPI.retrofit.create(RetrofitAPI.class);

                        callGetBonusesList =
                                retrofitService.getBonusesList(GlobalConstants.tokenStr, dateYYYYMM);

                        callGetBonusesList.enqueue(FragBonusList.this);
                    }
                }, 100);
            }
        } else
        {
            ViewUtils.showToastMessage(activity, getString(R.string.CheckInternetConnection));
        }
    }

    @Override
    public View getView()
    {
        return super.getView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.llReturn)
        {
            mCommunicator.onClose();
        }

        if (view.getId() == R.id.flListHeader)
        {
            mCommunicator.popFragmentBackStack();
        }

        if (view.getId() == R.id.flPrevDate)
        {
            shiftMonth(-1);

            dateMMYYYY = ItalianMonths.numToLongString(mMonth) + " " + mYear;
            tvBonusMonth.setText(dateMMYYYY);

            downloadBonusList();
        }

        if (view.getId() == R.id.flNextDate)
        {
            shiftMonth(1);

            dateMMYYYY = ItalianMonths.numToLongString(mMonth) + " " + mYear;
            tvBonusMonth.setText(dateMMYYYY);

            downloadBonusList();
        }

        if (view.getId() == R.id.tvMonthSelected)
        {
            Bundle bundle = new Bundle();
            bundle.putInt("year", mYear);
            bundle.putInt("month", mMonth);
            pd.setArguments(bundle);
            pd.show(getFragmentManager(), "MonthYearPickerDialog");
        }
    }

    private void shiftMonth(int incOrDec)
    {
        mMonth += incOrDec;

        if (mMonth < 0)
        {
            mMonth = 11;
            mYear--;
        }

        if (mMonth > 11)
        {
            mMonth = 0;
            mYear++;
        }
    }

    @Override
    public void onResponse(@NonNull Call<List<BonusItem>> callGetBonusList, @NonNull Response<List<BonusItem>> response)
    {
        downloadingDialog.dismiss();

        if (response.isSuccessful())
        {
            l_bonusItems = response.body();

            if (l_bonusItems == null)
            {
                l_bonusItems = new ArrayList<>();
            }

            if (l_bonusItems.size() != 0)
            {
                onInitialDownload = false;
                updateBonusListView();
            } else
            {
                ViewUtils.showToastMessage(activity, getString(R.string.NoDataForThisMonth));
            }
        } else
        {
            ViewUtils.showToastMessage(activity, getString(R.string.ServerError));

            //int statusCode = response.code();
            ResponseBody errorBody = response.errorBody();

            try
            {
                Log.d("DEBUG", errorBody.string());

            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFailure(@NonNull Call<List<BonusItem>> call, @NonNull Throwable t)
    {
        ViewUtils.showToastMessage(activity, getString(R.string.ServerAnswerNotReceived));

        downloadingDialog.dismiss();

        if (onInitialDownload)
        {
            mCommunicator.popFragmentBackStack();
        }
    }

    private void updateBonusListView()
    {
        BonusListAdapter bonusListAdapter = new BonusListAdapter(getActivity(), R.layout.bonus_list_row, l_bonusItems);
        setListAdapter(bonusListAdapter);

        try
        {
            float totalBonus = 0;
            for (BonusItem bonusItem : l_bonusItems)
            {
                totalBonus += Float.valueOf(bonusItem.getBonus());
            }

            String strBonusTotal = String.valueOf(totalBonus);

            strBonusTotal = MyTextUtils.reformatCurrencyString(strBonusTotal);

            tvBonusTotal.setText(strBonusTotal);
        } catch (Exception e)
        {
            e.printStackTrace();
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
}
