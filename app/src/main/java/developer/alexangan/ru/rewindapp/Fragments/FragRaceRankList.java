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
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import developer.alexangan.ru.rewindapp.Adapters.RaceRankListAdapter;
import developer.alexangan.ru.rewindapp.Interfaces.BonusAndGareCommunicator;
import developer.alexangan.ru.rewindapp.Interfaces.RetrofitAPI;
import developer.alexangan.ru.rewindapp.Models.GlobalConstants;
import developer.alexangan.ru.rewindapp.Models.ItalianMonths;
import developer.alexangan.ru.rewindapp.Models.ProfileInfoItem;
import developer.alexangan.ru.rewindapp.Models.RaceRankItem;
import developer.alexangan.ru.rewindapp.R;
import developer.alexangan.ru.rewindapp.Utils.NetworkUtils;
import developer.alexangan.ru.rewindapp.Utils.OnSwipeTouchListener;
import developer.alexangan.ru.rewindapp.Utils.ViewUtils;
import developer.alexangan.ru.rewindapp.ViewOverrides.MonthYearPickerDialog;
import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static developer.alexangan.ru.rewindapp.Models.GlobalConstants.mSettings;
import static developer.alexangan.ru.rewindapp.Models.GlobalConstants.tokenStr;

public class FragRaceRankList extends ListFragment implements View.OnClickListener, Callback<List<RaceRankItem>>
{
    private BonusAndGareCommunicator mCommunicator;
    private Activity activity;
    private Call callGetRaceRankList;
    AlertDialog alert;
    List<RaceRankItem> l_raceRankItems;
    private ProgressDialog downloadingDialog;

    private static int mYear;
    private static int mMonth;
    private String dateMMYYYY;
    private TextView tvGareMonth;
    private FrameLayout flMonthSlider;
    private MonthYearPickerDialog pd;
    private boolean onInitialDownload;
    private String loggedAgentFullName;
    private ProfileInfoItem profileInfoItem;
    private Realm realm;

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        realm.close();

        String strMonth = String.valueOf(mMonth + 1);

        if (strMonth.length() == 1)
        {
            strMonth = "0" + strMonth;
        }

        mSettings.edit().putString("strYear", String.valueOf(mYear)).apply();
        mSettings.edit().putString("strMonth", strMonth).apply();
    }

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

        l_raceRankItems = new ArrayList<>();

        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        profileInfoItem = realm.where(ProfileInfoItem.class).findFirst();

        if (profileInfoItem != null)
        {
            //realm.copyFromRealm(profileInfoItem);
            loggedAgentFullName = profileInfoItem.getName() + " " + profileInfoItem.getSurname();
        }
        realm.commitTransaction();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.frag_race_rank_list, container, false);

        LinearLayout llReturn = (LinearLayout) rootView.findViewById(R.id.llReturn);
        llReturn.setOnClickListener(this);

        FrameLayout flListHeader = (FrameLayout) rootView.findViewById(R.id.flListHeader);
        flListHeader.setOnClickListener(this);

        flMonthSlider = (FrameLayout) rootView.findViewById(R.id.flMonthSlider);

        FrameLayout flPrevDate = (FrameLayout) rootView.findViewById(R.id.flPrevDate);
        flPrevDate.setOnClickListener(this);
        FrameLayout flNextDate = (FrameLayout) rootView.findViewById(R.id.flNextDate);
        flNextDate.setOnClickListener(this);

        tvGareMonth = (TextView) rootView.findViewById(R.id.tvMonthSelected);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        String strYear = mSettings.getString("strYear", "");
        String strMonth = mSettings.getString("strMonth", "");
/*        mSettings.edit().remove("strYear").apply();
        mSettings.edit().remove("strMonth").apply();*/

        mYear = 0;
        mMonth = -2;

        try
        {
            mYear = Integer.parseInt(strYear);
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        }

        try
        {
            mMonth = Integer.parseInt(strMonth) - 1;
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        }

        Calendar calendarNow = Calendar.getInstance(); // now

        if (mYear == 0)
        {
            mYear = calendarNow.get(Calendar.YEAR);
        }

        if (mMonth == -2)
        {
            mMonth = calendarNow.get(Calendar.MONTH) - 1; // previous month
        }

        if (mMonth == -1)
        {
            mMonth = 11;
        }

        dateMMYYYY = ItalianMonths.numToLongString(mMonth) + " " + mYear;

        tvGareMonth.setText(dateMMYYYY);
        tvGareMonth.setOnClickListener(this);

        flMonthSlider.setOnTouchListener(new OnSwipeTouchListener(activity)
        {
            @Override
            public void onSwipeLeft()
            {
                shiftMonth(1);

                dateMMYYYY = ItalianMonths.numToLongString(mMonth) + " " + mYear;
                tvGareMonth.setText(dateMMYYYY);

                downloadRaceRankList();
            }

            @Override
            public void onSwipeRight()
            {
                shiftMonth(-1);

                dateMMYYYY = ItalianMonths.numToLongString(mMonth) + " " + mYear;
                tvGareMonth.setText(dateMMYYYY);

                downloadRaceRankList();

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
                tvGareMonth.setText(dateMMYYYY);

                downloadRaceRankList();
            }
        };

        pd = new MonthYearPickerDialog();
        pd.setListener(listener);

        if (onInitialDownload)
        {
            downloadRaceRankList();
        }
        else
        {
            updateRaceRankListView();
        }
    }

    private void shiftMonth(int incOrDec)
    {
        mMonth+=incOrDec;

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

    private void downloadRaceRankList()
    {
        if (NetworkUtils.isNetworkAvailable(activity))
        {
            if (tokenStr == null)
            {
                alertDialog("Info", getString(R.string.OfflineModeShowLoginScreenQuestion));
            } else
            {
                l_raceRankItems.clear();
                updateRaceRankListView();

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

                        callGetRaceRankList =
                                retrofitService.getRaceRankList(GlobalConstants.tokenStr, dateYYYYMM);

                        callGetRaceRankList.enqueue(FragRaceRankList.this);
                    }
                }, 100);

                //callGetRaceRankList = networkUtils.getDataForMonth(this, API_GET_CLASIFICATION_URL, dateYYYYMM);
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
            tvGareMonth.setText(dateMMYYYY);

            downloadRaceRankList();
        }

        if (view.getId() == R.id.flNextDate)
        {
            shiftMonth(1);

            dateMMYYYY = ItalianMonths.numToLongString(mMonth) + " " + mYear;
            tvGareMonth.setText(dateMMYYYY);

            downloadRaceRankList();
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

    @Override
    public void onResponse(@NonNull Call<List<RaceRankItem>> callGetRaceRankList, @NonNull Response<List<RaceRankItem>> response)
    {
        downloadingDialog.dismiss();

        if (response.isSuccessful())
        {
            l_raceRankItems = response.body();

            if (l_raceRankItems == null)
            {
                l_raceRankItems = new ArrayList<>();
            }

            if (l_raceRankItems.size() != 0)
            {
                onInitialDownload = false;
                updateRaceRankListView();
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
    public void onFailure(@NonNull Call<List<RaceRankItem>> call, @NonNull Throwable t)
    {
        ViewUtils.showToastMessage(activity, getString(R.string.ServerAnswerNotReceived));

        downloadingDialog.dismiss();

        if (onInitialDownload)
        {
            mCommunicator.popFragmentBackStack();
        }
    }

    private void updateRaceRankListView()
    {
        RaceRankListAdapter gareListAdapter = new RaceRankListAdapter(getActivity(), R.layout.race_rank_list_row, l_raceRankItems, profileInfoItem);
        setListAdapter(gareListAdapter);

        if (l_raceRankItems.size() == 0)
        {
            return;
        }

        ListView lv = getListView();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String agentName = l_raceRankItems.get(position).getName_agent();

                if (loggedAgentFullName.equals(agentName))
                {
                    String strMonth = String.valueOf(mMonth + 1);

                    if (strMonth.length() == 1)
                    {
                        strMonth = "0" + strMonth;
                    }

                    final String dateYYYYMM = String.valueOf(mYear) + strMonth;

                    mCommunicator.onRaceRankListItemSelected(position, dateYYYYMM);
                }
            }
        });
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
