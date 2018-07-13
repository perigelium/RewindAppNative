package developer.alexangan.ru.rewindapp.Activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;
import java.util.List;

import developer.alexangan.ru.rewindapp.Fragments.FragAppointmentDetails;
import developer.alexangan.ru.rewindapp.Fragments.FragAppointmentsCalendar;
import developer.alexangan.ru.rewindapp.Fragments.FragAppointmentsListMode;
import developer.alexangan.ru.rewindapp.Fragments.FragAppointmentsMap;
import developer.alexangan.ru.rewindapp.Fragments.FragAppointmentsMapLegendAndFilter;
import developer.alexangan.ru.rewindapp.Fragments.FragAppointmentsPersonalTask;
import developer.alexangan.ru.rewindapp.Fragments.FragAppointmentsSearch;
import developer.alexangan.ru.rewindapp.Fragments.FragAppointmentsSearchResults;
import developer.alexangan.ru.rewindapp.Fragments.FragClientAddMemo;
import developer.alexangan.ru.rewindapp.Interfaces.AppointmentsCommunicator;
import developer.alexangan.ru.rewindapp.Models.AppointmentInfoItem;
import developer.alexangan.ru.rewindapp.Models.AppointmentsSearchResultsListItem;
import developer.alexangan.ru.rewindapp.Models.GlobalConstants;
import developer.alexangan.ru.rewindapp.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AppointmentsActivity extends FragmentActivity implements AppointmentsCommunicator
{
    private FragmentManager mFragmentManager;
    private Fragment fragAppointmentsMap;
    private Fragment fragAppointmentsMapLegendAndFilter;
    private Fragment fragClientAddMemo;
    private Fragment fragAppointmentsSearch;
    private Fragment fragAppointmentsSearchResults;
    private Fragment fragAppointmentDetails;
    private Fragment fragAppointmentsListMode;
    private Fragment fragAppointmentsCalendar;
    private Fragment fragAppointmentsPersonalTask;

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clients_layout);

        fragAppointmentsMap = new FragAppointmentsMap();
        fragAppointmentsMapLegendAndFilter = new FragAppointmentsMapLegendAndFilter();
        fragAppointmentDetails = new FragAppointmentDetails();
        fragAppointmentsSearch = new FragAppointmentsSearch();
        fragClientAddMemo = new FragClientAddMemo();
        fragAppointmentsListMode = new FragAppointmentsListMode();
        fragAppointmentsCalendar = new FragAppointmentsCalendar();
        fragAppointmentsSearchResults = new FragAppointmentsSearchResults();
        fragAppointmentsPersonalTask = new FragAppointmentsPersonalTask();

        if (!fragAppointmentsMap.isAdded())
        {
            mFragmentManager = getFragmentManager();
            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

            mFragmentTransaction.add(R.id.clientsFragContainer, fragAppointmentsMap);
            mFragmentTransaction.addToBackStack(null);

            mFragmentTransaction.commit();
        }
    }

    @Override
    public void onLogoutCommand()
    {
        GlobalConstants.logoutInProgress = true;
        this.finish();
    }

    @Override
    public void onClose()
    {
        this.finish();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

        if (mFragmentManager.getBackStackEntryCount() == 0)
        {
            this.finish();
        }
    }

    @Override
    public void openLegendAndFilter(boolean gpsIsActive)
    {
        if (!fragAppointmentsMapLegendAndFilter.isAdded())
        {
            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

            Bundle args = fragAppointmentsMapLegendAndFilter.getArguments() != null ? fragAppointmentsMapLegendAndFilter.getArguments() : new Bundle();

            args.putBoolean("gpsIsActive", gpsIsActive);

            fragAppointmentsMapLegendAndFilter.setArguments(args);

            mFragmentTransaction.remove(fragAppointmentsMap);
            mFragmentTransaction.add(R.id.clientsFragContainer, fragAppointmentsMapLegendAndFilter);
            mFragmentTransaction.addToBackStack(null);

            mFragmentTransaction.commit();
        }
    }

    @Override
    public void onMapPinSelected(AppointmentInfoItem appointmentInfoItem)
    {
/*        if (!fragAppointmentDetails.isAdded())
        {
            mSettings.edit().putBoolean("SearchMode", false).apply();

            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

            Bundle args = fragAppointmentDetails.getArguments() != null ? fragAppointmentDetails.getArguments() : new Bundle();

            args.putParcelable("clientInfoItem", clientInfoItem);

            fragAppointmentDetails.setArguments(args);

            mFragmentTransaction.remove(fragAppointmentsMap);
            mFragmentTransaction.add(R.id.clientsFragContainer, fragAppointmentDetails);
            mFragmentTransaction.addToBackStack(null);

            mFragmentTransaction.commit();
        }*/
    }

    @Override
    public void onAppointmentAddMemoClicked(int id_customer, Fragment frag)
    {
        if (!fragClientAddMemo.isAdded())
        {
            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

            Bundle args = fragClientAddMemo.getArguments() != null ? fragClientAddMemo.getArguments() : new Bundle();

            args.putInt("id_customer", id_customer);

            fragClientAddMemo.setArguments(args);

            if(frag.isAdded())
            {
                mFragmentTransaction.remove(frag);
            }

            mFragmentTransaction.add(R.id.clientsFragContainer, fragClientAddMemo);
            mFragmentTransaction.addToBackStack(null);

            mFragmentTransaction.commit();
        }
    }

    @Override
    public void openAppointmentsSearch(Fragment frag)
    {
        if ( ! fragAppointmentsSearch.isAdded())
        {
            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

            if(frag.isAdded())
            {
                mFragmentTransaction.remove(frag);
            }

            mFragmentTransaction.add(R.id.clientsFragContainer, fragAppointmentsSearch);
            mFragmentTransaction.addToBackStack(null);

            mFragmentTransaction.commit();
        }
    }

    @Override
    public void onAppointmentsSearchResultsListItemSelected(AppointmentsSearchResultsListItem appointmentsSearchResultsListItem)
    {
/*        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

        if (!fragAppointmentDetails.isAdded())
        {
            mSettings.edit().putBoolean("SearchMode", true).apply();

            Bundle args = fragAppointmentDetails.getArguments() != null ? fragAppointmentDetails.getArguments() : new Bundle();

            args.putParcelable("clientInfoItem", clientInfoItem);

            fragAppointmentDetails.setArguments(args);

            mFragmentTransaction.remove(fragAppointmentsSearch);
            mFragmentTransaction.add(R.id.clientsFragContainer, fragAppointmentDetails);
            mFragmentTransaction.addToBackStack(null);

            mFragmentTransaction.commit();
        }*/
    }

    @Override
    public void openAppointmentsNewPersonalTask()
    {
        if (!fragAppointmentsPersonalTask.isAdded())
        {
            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

            if (fragAppointmentsListMode.isAdded())
            {
                mFragmentTransaction.remove(fragAppointmentsListMode);
            }

            if (fragAppointmentsCalendar.isAdded())
            {
                mFragmentTransaction.remove(fragAppointmentsCalendar);
            }

            mFragmentTransaction.add(R.id.clientsFragContainer, fragAppointmentsPersonalTask);
            mFragmentTransaction.addToBackStack(null);

            mFragmentTransaction.commit();
        }
    }

    @Override
    public void popFragmentsBackStack()
    {
        mFragmentManager.popBackStack();

        if (mFragmentManager.getBackStackEntryCount() == 0)
        {
            this.finish();
        }
    }

    @Override
    public void openAppointmentsCalendar(List<AppointmentInfoItem> lAppointmentInfoItems, boolean isDailyMode)
    {
        if ( ! fragAppointmentsCalendar.isAdded())
        {
            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

            Bundle args = fragAppointmentsCalendar.getArguments() != null ? fragAppointmentsCalendar.getArguments() : new Bundle();

            args.putParcelableArrayList("ListAppointmentInfoItems", (ArrayList<? extends Parcelable>) lAppointmentInfoItems);
            args.putBoolean("isDailyMode", isDailyMode);

            fragAppointmentsCalendar.setArguments(args);

            mFragmentTransaction.remove(fragAppointmentsListMode);

            mFragmentTransaction.add(R.id.clientsFragContainer, fragAppointmentsCalendar);
            mFragmentTransaction.addToBackStack(null);

            mFragmentTransaction.commit();
        }
    }

    @Override
    public void openAppointmentsListMode(List<AppointmentInfoItem> l_AppointmentInfoItems, boolean dailyMode, Fragment frag)
    {
        if (!fragAppointmentsListMode.isAdded())
        {
            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

            Bundle args = fragAppointmentsListMode.getArguments() != null ? fragAppointmentsListMode.getArguments() : new Bundle();

            args.putParcelableArrayList("ListAppointmentInfoItems", (ArrayList<? extends Parcelable>) l_AppointmentInfoItems);

            fragAppointmentsListMode.setArguments(args);

            if(frag.isAdded())
            {
                mFragmentTransaction.remove(frag);
            }

            mFragmentTransaction.add(R.id.clientsFragContainer, fragAppointmentsListMode);
            mFragmentTransaction.addToBackStack(null);

            mFragmentTransaction.commit();
        }
    }

    @Override
    public void openAppointmentsSearchResults(String searchQuery, List<AppointmentsSearchResultsListItem> appointmentsSearchResultsListItems)
    {
        if (!fragAppointmentsSearchResults.isAdded())
        {
            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

            Bundle args = fragAppointmentsSearchResults.getArguments() != null ? fragAppointmentsSearchResults.getArguments() : new Bundle();

            args.putParcelableArrayList("appointmentsSearchResultsListItems", (ArrayList<? extends Parcelable>) appointmentsSearchResultsListItems);
            args.putString("searchQuery", searchQuery);

            fragAppointmentsSearchResults.setArguments(args);

            mFragmentTransaction.remove(fragAppointmentsSearch);
            mFragmentTransaction.add(R.id.clientsFragContainer, fragAppointmentsSearchResults);
            mFragmentTransaction.addToBackStack(null);

            mFragmentTransaction.commit();
        }
    }
}
