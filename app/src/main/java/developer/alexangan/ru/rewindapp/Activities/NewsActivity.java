package developer.alexangan.ru.rewindapp.Activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import developer.alexangan.ru.rewindapp.Fragments.FragNewsDetailed;
import developer.alexangan.ru.rewindapp.Fragments.FragNewsList;
import developer.alexangan.ru.rewindapp.Fragments.FragNewsRaceList;
import developer.alexangan.ru.rewindapp.Interfaces.NewsCommunicator;
import developer.alexangan.ru.rewindapp.Models.GlobalConstants;
import developer.alexangan.ru.rewindapp.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static developer.alexangan.ru.rewindapp.Models.GlobalConstants.mSettings;

public class NewsActivity extends Activity implements NewsCommunicator, View.OnClickListener
{
    private FragmentManager mFragmentManager;
    private FragNewsList fragNewsList;
    private FragNewsDetailed fragNewsDetailed;
    private FragNewsRaceList fragNewsRaceList;

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_layout);

        fragNewsList = new FragNewsList();
        fragNewsDetailed = new FragNewsDetailed();
        fragNewsRaceList = new FragNewsRaceList();

        mSettings.edit().putString("newsSearchLastQueryString", "").commit();

        mFragmentManager = getFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

        mFragmentTransaction.add(R.id.newsFragContainer, fragNewsList);
        //mFragmentTransaction.addToBackStack(null);

        mFragmentTransaction.commit();
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.llReturn)
        {
            this.finish();
        }
    }

    @Override
    public void onNewsListItemSelected(int position)
    {
        if (!fragNewsDetailed.isAdded())
        {
            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

            Bundle args = new Bundle();
            args.putInt("id", position);
            fragNewsDetailed.setArguments(args);

            mFragmentTransaction.remove(fragNewsList);
            mFragmentTransaction.add(R.id.newsFragContainer, fragNewsDetailed);
            mFragmentTransaction.addToBackStack(null);

            mFragmentTransaction.commit();
        }
    }

    @Override
    public void onDetailedNewsReturned()
    {
        if (!fragNewsList.isAdded())
        {
            mFragmentManager.popBackStack();
        }
    }

    @Override
    public void onLogoutCommand()
    {
        GlobalConstants.logoutInProgress = true;
        this.finish();
    }

    @Override
    public void onDetailedNewsRaceReturned()
    {
        mFragmentManager.popBackStack();
    }

    @Override
    public void onBackPressed()
    {
        mSettings.edit().putString("newsSearchLastQueryString", "").commit();

        if(mFragmentManager.getBackStackEntryCount() == 0)
        {
            this.finish();
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public void onRaceNewsDetailsReturned(int newsItemId)
    {
        if (!fragNewsRaceList.isAdded())
        {
            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

            Bundle args = new Bundle();
            args.putInt("id", newsItemId);
            fragNewsRaceList.setArguments(args);

            mFragmentTransaction.remove(fragNewsDetailed);
            mFragmentTransaction.add(R.id.newsFragContainer, fragNewsRaceList);
            mFragmentTransaction.addToBackStack(null);

            //mFragmentTransaction.replace(R.id.newsFragContainer, fragNewsDetailed);

            mFragmentTransaction.commit();
        }
    }

    @Override
    public void onClose()
    {
        this.finish();
    }

}
