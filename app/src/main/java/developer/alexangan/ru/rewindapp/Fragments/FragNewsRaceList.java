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
import developer.alexangan.ru.rewindapp.Interfaces.NewsCommunicator;
import developer.alexangan.ru.rewindapp.Interfaces.RetrofitAPI;
import developer.alexangan.ru.rewindapp.Models.GlobalConstants;
import developer.alexangan.ru.rewindapp.Models.NewsItem;
import developer.alexangan.ru.rewindapp.Models.RaceRankDetailsItem;
import developer.alexangan.ru.rewindapp.R;
import developer.alexangan.ru.rewindapp.Utils.NetworkUtils;
import developer.alexangan.ru.rewindapp.Utils.ViewUtils;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static developer.alexangan.ru.rewindapp.Models.GlobalConstants.tokenStr;

public class FragNewsRaceList extends ListFragment implements View.OnClickListener, Callback<List<RaceRankDetailsItem>>
{
    private NewsCommunicator mCommunicator;
    private Activity activity;
    /*    private Handler handler;
        private Runnable runnable;*/
    //private Call callGetNewsRaceList;
    private ProgressDialog requestServerDialog;
    AlertDialog alert;
    private int newsItemId;
    private NewsItem newsItem;
    List<RaceRankDetailsItem> l_raceRankDetailsItems;
    private ProgressDialog downloadingDialog;
    private ArrayList<NewsItem> l_newsItems;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        mCommunicator = (NewsCommunicator) getActivity();
        l_newsItems = new ArrayList<>();

        if (getArguments() != null)
        {
            newsItemId = getArguments().getInt("id");
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
        tvPreviousPageTitle.setText(R.string.News);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();
        RealmResults<NewsItem> rrNewsItems = realm.where(NewsItem.class).findAll();
        realm.commitTransaction();

        realm.beginTransaction();
        for (NewsItem newsItem : rrNewsItems)
        {
            NewsItem newsItemEx = realm.copyFromRealm(newsItem);
            l_newsItems.add(newsItemEx);
        }
        realm.commitTransaction();
        realm.close();

        newsItem = l_newsItems.get(newsItemId);
        final int id_news = newsItem.getId_news();

        if (NetworkUtils.isNetworkAvailable(activity))
        {
            if (tokenStr == null)
            {
                alertDialog("Info", getString(R.string.OfflineModeShowLoginScreenQuestion));
            } else
            {
                l_raceRankDetailsItems.clear();
                updateNewsRaceListView();

                downloadingDialog.show();

/*                NetworkUtils networkUtils = new NetworkUtils();
                callGetNewsRaceList = networkUtils.getNewsDetailed(this, API_GETDETAILEDNEWSRACE_URL, tokenStr, id_news);*/

                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        RetrofitAPI retrofitService = RetrofitAPI.retrofit.create(RetrofitAPI.class);

                        Call<List<RaceRankDetailsItem>> getRaceRankNewsDetailsList =
                                retrofitService.getRaceRankNewsDetailsList(GlobalConstants.tokenStr, id_news);

                        getRaceRankNewsDetailsList.enqueue(FragNewsRaceList.this);
                    }
                }, 100);
            }
        }
    }

    @Override
    public void onResponse(@NonNull Call<List<RaceRankDetailsItem>> call, @NonNull Response<List<RaceRankDetailsItem>> response)
    {
        if (response.isSuccessful())
        {
            l_raceRankDetailsItems = response.body();

            downloadingDialog.dismiss();

            if (l_raceRankDetailsItems.size() != 0)
            {
                updateNewsRaceListView();

                return;
            }
        }
        else
        {
            //int statusCode = response.code();
            ResponseBody errorBody = response.errorBody();

            try
            {
                Log.d("DEBUG", errorBody.string());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        downloadingDialog.dismiss();

        ViewUtils.showToastMessage(activity, getString(R.string.ServerError));

        mCommunicator.onDetailedNewsRaceReturned();
    }

    @Override
    public void onFailure(@NonNull Call<List<RaceRankDetailsItem>> call, @NonNull Throwable t)
    {
        downloadingDialog.dismiss();

        Log.d("DEBUG", t.getMessage());

        ViewUtils.showToastMessage(activity, getString(R.string.ServerAnswerNotReceived));

        mCommunicator.onDetailedNewsRaceReturned();
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

        //searchViewReports.setQuery("", false);
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.llReturn)
        {
            mCommunicator.onDetailedNewsRaceReturned();
        }
    }

    private void updateNewsRaceListView()
    {
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
