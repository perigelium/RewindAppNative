package developer.alexangan.ru.rewindapp.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import developer.alexangan.ru.rewindapp.Adapters.RaceRankDetailsListAdapter;
import developer.alexangan.ru.rewindapp.Interfaces.BonusAndGareCommunicator;
import developer.alexangan.ru.rewindapp.Interfaces.RetrofitAPI;
import developer.alexangan.ru.rewindapp.Models.GlobalConstants;
import developer.alexangan.ru.rewindapp.Models.RaceRankDetailsItem;
import developer.alexangan.ru.rewindapp.R;
import developer.alexangan.ru.rewindapp.Utils.NetworkUtils;
import developer.alexangan.ru.rewindapp.Utils.ViewUtils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static developer.alexangan.ru.rewindapp.Models.GlobalConstants.tokenStr;

public class FragRaceRankDetailsList extends ListFragment implements View.OnClickListener, Callback<List<RaceRankDetailsItem>>
{
    private BonusAndGareCommunicator mCommunicator;
    private Activity activity;
    private Call callGetRaceRankDetailsList;
    AlertDialog alert;
    List<RaceRankDetailsItem> l_raceRankDetailsItems;
    private ProgressDialog downloadingDialog;
    private String dateYYYYMM;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        mCommunicator = (BonusAndGareCommunicator) getActivity();

        if (getArguments() != null)
        {
            dateYYYYMM = getArguments().getString("dateYYYYMM");
        }

        downloadingDialog = new ProgressDialog(getActivity(), R.style.AppCompatAlertDialogStyle);
        downloadingDialog.setTitle("");
        downloadingDialog.setMessage(getString(R.string.DownloadingDataPleaseWait));
        downloadingDialog.setIndeterminate(true);

        l_raceRankDetailsItems = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.frag_race_rank_details_list, container, false);

        LinearLayout llReturn = (LinearLayout) rootView.findViewById(R.id.llReturn);
        llReturn.setOnClickListener(this);

        TextView tvPreviousPageTitle = (TextView) rootView.findViewById(R.id.tvPreviousPageTitle);
        tvPreviousPageTitle.setText(R.string.RaceRankTitle);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        l_raceRankDetailsItems.clear();
        updateRaceRankDetailsListView();

        if (NetworkUtils.isNetworkAvailable(activity))
        {
            if (tokenStr == null)
            {
                alertDialog("Info", getString(R.string.OfflineModeShowLoginScreenQuestion));
            } else
            {
                downloadingDialog.show();

                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        RetrofitAPI retrofitService = RetrofitAPI.retrofit.create(RetrofitAPI.class);

                        callGetRaceRankDetailsList =
                                retrofitService.getRaceRankDetailsList(GlobalConstants.tokenStr, dateYYYYMM);

                        callGetRaceRankDetailsList.enqueue(FragRaceRankDetailsList.this);
                    }
                }, 100);
            }
        }
    }

    @Override
    public View getView()
    {
        return super.getView();
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.llReturn)
        {
            mCommunicator.popFragmentBackStack();
        }
    }

    @Override
    public void onResponse(@NonNull Call<List<RaceRankDetailsItem>> callGetRaceRankDetailsList, @NonNull Response<List<RaceRankDetailsItem>> response)
    {
        downloadingDialog.dismiss();

        if (response.isSuccessful())
        {
            l_raceRankDetailsItems = response.body();

            if (l_raceRankDetailsItems != null && l_raceRankDetailsItems.size() != 0)
            {
                updateRaceRankDetailsListView();
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
    public void onFailure(@NonNull Call<List<RaceRankDetailsItem>> call, @NonNull Throwable t)
    {
        ViewUtils.showToastMessage(activity, getString(R.string.ServerAnswerNotReceived));

        downloadingDialog.dismiss();

        mCommunicator.popFragmentBackStack();
    }

    private void updateRaceRankDetailsListView()
    {
        if (l_raceRankDetailsItems == null)
        {
            l_raceRankDetailsItems = new ArrayList<>();
        }

        RaceRankDetailsListAdapter raceRankDetailsListAdapter = new RaceRankDetailsListAdapter(getActivity(), R.layout.race_news_details_row, l_raceRankDetailsItems);
        setListAdapter(raceRankDetailsListAdapter);
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
