package developer.alexangan.ru.rewindapp.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import developer.alexangan.ru.rewindapp.Adapters.NewsListAdapter;
import developer.alexangan.ru.rewindapp.Interfaces.NewsCommunicator;
import developer.alexangan.ru.rewindapp.Interfaces.RetrofitAPI;
import developer.alexangan.ru.rewindapp.Models.GlobalConstants;
import developer.alexangan.ru.rewindapp.Models.NewsItem;
import developer.alexangan.ru.rewindapp.R;
import developer.alexangan.ru.rewindapp.Utils.NetworkUtils;
import developer.alexangan.ru.rewindapp.Utils.ViewUtils;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static developer.alexangan.ru.rewindapp.Models.GlobalConstants.mSettings;
import static developer.alexangan.ru.rewindapp.Models.GlobalConstants.tokenStr;

public class FragNewsList extends ListFragment implements View.OnClickListener, SearchView.OnQueryTextListener, Callback<List<NewsItem>>
{
    private NewsCommunicator mCommunicator;
    private Activity activity;
    private Call callGetNewsList;
    private ProgressDialog requestServerDialog;
    AlertDialog alert;

    private SearchView svNews;
    private TextView tvCloseSearchView;
    private int firstNewsNumberInList;
    private int offsetNewsInList;
    private Button btnAdd10NewsToList;
    private LinearLayout llAdd10NewsToList;
    private boolean firstStart;
    private ImageView closeIcon;
    private ImageView magImage;
    private TextView textView;
    private LinearLayout llOpenSearchView;
    private ArrayList<NewsItem> l_newsItems;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        mCommunicator = (NewsCommunicator) getActivity();

        requestServerDialog = new ProgressDialog(getActivity(), R.style.AppCompatAlertDialogStyle);
        requestServerDialog.setTitle("");
        requestServerDialog.setMessage(getString(R.string.DownloadingDataPleaseWait));
        requestServerDialog.setIndeterminate(true);

        firstStart = true;
        l_newsItems = new ArrayList<>();
        fillNewsItemsArray();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.frag_news_layout, container, false);

        LinearLayout llReturn = (LinearLayout) rootView.findViewById(R.id.llReturn);
        llReturn.setOnClickListener(this);

        svNews = (SearchView) rootView.findViewById(R.id.svNews);

        llOpenSearchView = (LinearLayout) rootView.findViewById(R.id.llOpenSearchView);

        tvCloseSearchView = (TextView) rootView.findViewById(R.id.tvCloseSearchView);

        //scvNews = (ScrollViewEx) rootView.findViewById(R.id.scvNews);

        llAdd10NewsToList = (LinearLayout) rootView.findViewById(R.id.llAdd10NewsToList);
        btnAdd10NewsToList = (Button) rootView.findViewById(R.id.btnAdd10NewsToList);

        //int id = svNews.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        //searchTextHint = (TextView) svNews.findViewById(id);

        int searchHintBtnId = svNews.getContext().getResources()
                .getIdentifier("android:id/search_mag_icon", null, null);
        int hintTextId = svNews.getContext()
                .getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        int closeBtnId = svNews.getContext()
                .getResources()
                .getIdentifier("android:id/search_close_btn", null, null);
/*        int searchBtnId = svNews.getContext()
                .getResources()
                .getIdentifier("android:id/search_button", null, null);*/

        closeIcon = (ImageView) svNews.findViewById(closeBtnId);
        textView = (TextView) svNews.findViewById(hintTextId);


/*        ImageView searchIcon = (ImageView) svNews.findViewById(searchHintBtnId);
        searchIcon.setImageResource(R.drawable.transparent24px);*/

        magImage = (ImageView) svNews.findViewById(searchHintBtnId);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        Typeface myCustomFont = Typeface.createFromAsset(activity.getAssets(), "fonts/SF_UI_Text_Regular.ttf");
        textView.setTypeface(myCustomFont);

        textView.setTextColor(Color.BLACK);
        textView.setHintTextColor(Color.parseColor("#ff808080"));

        closeIcon.setImageResource(R.drawable.clearsearch_2);
        magImage.setLayoutParams(new LinearLayout.LayoutParams(0, 0));

        firstNewsNumberInList = l_newsItems.size();
        offsetNewsInList = 10;

        llOpenSearchView.setOnClickListener(this);
        tvCloseSearchView.setOnClickListener(this);

        svNews.setSubmitButtonEnabled(true);
        svNews.setOnQueryTextListener(this);

        svNews.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!hasFocus)
                {
                    tvCloseSearchView.setVisibility(View.GONE);
                    svNews.setVisibility(View.GONE);
                    llAdd10NewsToList.setVisibility(View.VISIBLE);
                    llOpenSearchView.setVisibility(View.VISIBLE);

                    updateNewsListView(l_newsItems);
                }
            }
        });

        btnAdd10NewsToList.setOnClickListener(this);

        if (!NetworkUtils.isNetworkAvailable(activity))
        {
            //fillNewsItemsArray();

            if (l_newsItems != null)
            {
                updateNewsListView(l_newsItems);
            }
        } else
        {
            if (firstStart && l_newsItems.size() == 0)
            {
                if (tokenStr == null)
                {
                    alertDialogRelogin("Info", getString(R.string.OfflineModeShowLoginScreenQuestion));
                } else
                {
                    requestServerDialog.show();

                    firstStart = false;

                    new Handler().postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            RetrofitAPI retrofitService = RetrofitAPI.retrofit.create(RetrofitAPI.class);

                            callGetNewsList =
                                    retrofitService.getNewsList(GlobalConstants.tokenStr, firstNewsNumberInList, offsetNewsInList);

                            callGetNewsList.enqueue(FragNewsList.this);
                        }
                    }, 100);
                }
            } else
            {
                if (l_newsItems != null)
                {
                    updateNewsListView(l_newsItems);
                }

                llAdd10NewsToList.setVisibility(View.VISIBLE);
            }
        }
    }

    private void fillNewsItemsArray()
    {
        Realm realm = Realm.getDefaultInstance();

        //l_newsItems.clear();

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
            mCommunicator.onClose();
            return;
        }

        if (view.getId() == R.id.llOpenSearchView)
        {
            view.setVisibility(View.GONE);
            svNews.setVisibility(View.VISIBLE);
            llAdd10NewsToList.setVisibility(View.GONE);
            tvCloseSearchView.setVisibility(View.VISIBLE);
            return;
        }

        if (view.getId() == R.id.btnAdd10NewsToList)
        {
            add10NewsToList();
            return;
        }

        if (view.getId() == R.id.tvCloseSearchView)
        {
            view.setVisibility(View.GONE);
            svNews.setVisibility(View.GONE);
            llAdd10NewsToList.setVisibility(View.VISIBLE);
            llOpenSearchView.setVisibility(View.VISIBLE);

            updateNewsListView(l_newsItems);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String queryString)
    {
        ViewUtils.disableSoftKeyboard(activity);

        mSettings.edit().putString("newsSearchLastQueryString", queryString).apply();

        String strQueryLower = queryString.toLowerCase();

        ArrayList<NewsItem> newsItems = new ArrayList<>();

        for (NewsItem newsItem : l_newsItems)
        //for (int i = 0; i < visitItems.size(); i++)
        {
            String strNewsTitle = newsItem.getTitle_news();
            String strNewsSubTitle = newsItem.getSubtitle_news();
            String strNewsText = newsItem.getNews();

            if (strNewsTitle.toLowerCase().contains(strQueryLower) || strNewsSubTitle.toLowerCase().contains(strQueryLower)
                    || strNewsText.toLowerCase().contains(strQueryLower))
            {
                newsItems.add(newsItem);
            }
        }

        //l_newsItems = newsItems;

        if (newsItems.size() == 0)
        {
            ViewUtils.showToastMessage(activity, getString(R.string.NoNewsCorrespondSearchCrtiteria));
        }
        updateNewsListView(newsItems);


        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText)
    {
/*        if (TextUtils.isEmpty(newText))
        {
            //mListView.clearTextFilter();
        } else
        {
            //mListView.setFilterText(newText.toString());
        }*/
        return false;
    }

    @Override
    public void onFailure(@NonNull Call<List<NewsItem>> call, @NonNull Throwable t)
    {
        ViewUtils.showToastMessage(activity, getString(R.string.ServerAnswerNotReceived));

        requestServerDialog.dismiss();

        if (l_newsItems.size() == 0)
        {
            mCommunicator.onClose();
        }
    }

    @Override
    public void onResponse(@NonNull Call<List<NewsItem>> callGetNewsList, @NonNull Response<List<NewsItem>> response)
    {
        requestServerDialog.dismiss();

        if (response.isSuccessful())
        {
            List<NewsItem> lNewsItems = response.body();

            if (lNewsItems != null && lNewsItems.size() != 0)
            {
                for (NewsItem newsItem : lNewsItems)
                {
                    l_newsItems.add(newsItem);
                }

                Realm realm = Realm.getDefaultInstance();

                realm.beginTransaction();

                for (NewsItem newsItem : lNewsItems)
                {
                    realm.copyToRealm(newsItem);
                }
                realm.commitTransaction();
                realm.close();

                if (this.isVisible())
                {
                    updateNewsListView(l_newsItems);
                    llAdd10NewsToList.setVisibility(View.VISIBLE);
                }

                firstNewsNumberInList += offsetNewsInList;
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

    private void updateNewsListView(ArrayList<NewsItem> l_newsItems)
    {
        NewsListAdapter newsListAdapter = new NewsListAdapter(getActivity(), R.layout.news_row, l_newsItems);
        setListAdapter(newsListAdapter);

        ListView lv = getListView();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                mCommunicator.onNewsListItemSelected(position);
            }
        });

/*        final int scvBottom = scvNews.getScrollY() + scvNews.getChildAt(scvNews.getChildCount() - 1).getHeight();//scvNews.getScrollY() +

        scvNews.post(new Runnable()
        {
            @Override
            public void run()
            {
                scvNews.scrollTo(0, scvBottom);
            }
        });*/
    }

    private void alertDialogRelogin(String title, String message)
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

/*    @Override
    public void onScrollChanged(ScrollViewEx scrollView, int x, int y, int oldx, int oldy)
    {
        View view = scrollView.getChildAt(scrollView.getChildCount() - 1);
        int scvBottom = view.getBottom();
        int scvHeight = scrollView.getHeight();
        int scrollY = scrollView.getScrollY();

        int diff = view.getBottom() - scrollView.getHeight() - scrollView.getScrollY();

        // if diff is zero, then the top has been reached
        if (diff == 0 && NetworkUtils.isNetworkAvailable(activity))
        {
            add10NewsToList();
        }
    }*/

    private void add10NewsToList()
    {
        if (!NetworkUtils.isNetworkAvailable(activity))
        {
            ViewUtils.showToastMessage(activity, getString(R.string.OfflineMode));
        }

        if (svNews.getVisibility() != View.VISIBLE)
        {
            if (tokenStr == null)
            {
                alertDialogRelogin("Info", getString(R.string.OfflineModeShowLoginScreenQuestion));
            } else
            {
                requestServerDialog.show();

                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        RetrofitAPI retrofitService = RetrofitAPI.retrofit.create(RetrofitAPI.class);

                        callGetNewsList =
                                retrofitService.getNewsList(GlobalConstants.tokenStr, firstNewsNumberInList, offsetNewsInList);

                        callGetNewsList.enqueue(FragNewsList.this);
                    }
                }, 100);
            }
        }
    }
}
