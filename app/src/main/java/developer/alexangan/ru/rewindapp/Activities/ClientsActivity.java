package developer.alexangan.ru.rewindapp.Activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import developer.alexangan.ru.rewindapp.Fragments.FragClientAddMemo;
import developer.alexangan.ru.rewindapp.Fragments.FragClientAltuofiancoDetails;
import developer.alexangan.ru.rewindapp.Fragments.FragClientDetails;
import developer.alexangan.ru.rewindapp.Fragments.FragClientDocsList;
import developer.alexangan.ru.rewindapp.Fragments.FragClientPracticeDetails;
import developer.alexangan.ru.rewindapp.Fragments.FragClientPracticesList;
import developer.alexangan.ru.rewindapp.Fragments.FragClientsMap;
import developer.alexangan.ru.rewindapp.Fragments.FragClientsMapLegendAndFilter;
import developer.alexangan.ru.rewindapp.Fragments.FragClientsSearch;
import developer.alexangan.ru.rewindapp.Fragments.FragLeadDetails;
import developer.alexangan.ru.rewindapp.Fragments.FragLeadsList;
import developer.alexangan.ru.rewindapp.Interfaces.ClientsCommunicator;
import developer.alexangan.ru.rewindapp.Models.ClientDocsItem;
import developer.alexangan.ru.rewindapp.Models.ClientInfoItem;
import developer.alexangan.ru.rewindapp.Models.ClientPraticheInfoItem;
import developer.alexangan.ru.rewindapp.Models.GlobalConstants;
import developer.alexangan.ru.rewindapp.Models.LeadInfoItem;
import developer.alexangan.ru.rewindapp.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static developer.alexangan.ru.rewindapp.Models.GlobalConstants.mSettings;

public class ClientsActivity extends Activity implements ClientsCommunicator
{
    private FragmentManager mFragmentManager;
    private Fragment fragClientsMap;
    private Fragment fragClientsMapLegendAndFilter;
    private Fragment fragClientDetails;
    private Fragment fragClientPracticesList;
    private Fragment fragClientPracticeDetails;
    private Fragment fragClientAltuofiancoDetails;
    private Fragment fragClientAddMemo;
    private Fragment fragClientsSearch;
    private Fragment fragLeadsList;
    private Fragment fragLeadDetails;
    private Fragment fragClientDocsList;

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

        fragClientsMap = new FragClientsMap();
        fragClientsMapLegendAndFilter = new FragClientsMapLegendAndFilter();
        fragClientDetails = new FragClientDetails();
        fragClientPracticesList = new FragClientPracticesList();
        fragClientPracticeDetails = new FragClientPracticeDetails();
        fragClientAltuofiancoDetails = new FragClientAltuofiancoDetails();
        fragClientAddMemo = new FragClientAddMemo();
        fragClientsSearch = new FragClientsSearch();
        fragLeadsList = new FragLeadsList();
        fragLeadDetails = new FragLeadDetails();
        fragClientDocsList = new FragClientDocsList();

        mFragmentManager = getFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

        mFragmentTransaction.add(R.id.clientsFragContainer, fragClientsMap);
        mFragmentTransaction.addToBackStack(null);

        mFragmentTransaction.commit();
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
    public void onOpenLegendAndFilter(boolean gpsIsActive)
    {
        if (!fragClientsMapLegendAndFilter.isAdded())
        {
            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

            Bundle args = fragClientsMapLegendAndFilter.getArguments() != null ? fragClientsMapLegendAndFilter.getArguments() : new Bundle();

            args.putBoolean("gpsIsActive", gpsIsActive);

            fragClientsMapLegendAndFilter.setArguments(args);

            mFragmentTransaction.remove(fragClientsMap);
            mFragmentTransaction.add(R.id.clientsFragContainer, fragClientsMapLegendAndFilter);
            mFragmentTransaction.addToBackStack(null);

            mFragmentTransaction.commit();
        }
    }

    @Override
    public void onMapPinSelected(ClientInfoItem clientInfoItem)
    {
        if (!fragClientDetails.isAdded())
        {
            mSettings.edit().putBoolean("SearchMode", false).apply();

            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

            Bundle args = fragClientDetails.getArguments() != null ? fragClientDetails.getArguments() : new Bundle();

            args.putParcelable("clientInfoItem", clientInfoItem);

            fragClientDetails.setArguments(args);

            mFragmentTransaction.remove(fragClientsMap);
            mFragmentTransaction.add(R.id.clientsFragContainer, fragClientDetails);
            mFragmentTransaction.addToBackStack(null);

            mFragmentTransaction.commit();
        }
    }

    @Override
    public void onClientPracticesSelected(List<ClientPraticheInfoItem> practiche_list)
    {
        if (!fragClientPracticesList.isAdded())
        {
            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

            Bundle args = fragClientPracticesList.getArguments() != null ? fragClientPracticesList.getArguments() : new Bundle();

            args.putParcelableArrayList("ListClientPraticheInfoItems", (ArrayList<? extends Parcelable>) practiche_list);

            fragClientPracticesList.setArguments(args);

            mFragmentTransaction.remove(fragClientDetails);
            mFragmentTransaction.add(R.id.clientsFragContainer, fragClientPracticesList);
            mFragmentTransaction.addToBackStack(null);

            mFragmentTransaction.commit();
        }
    }

    @Override
    public void onClientAltuofiancoDetailsSelected(ClientInfoItem clientInfoItem)
    {
        if (!fragClientAltuofiancoDetails.isAdded())
        {
            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

            Bundle args = fragClientAltuofiancoDetails.getArguments() != null ? fragClientAltuofiancoDetails.getArguments() : new Bundle();

            args.putParcelable("clientInfoItem", clientInfoItem);

            fragClientAltuofiancoDetails.setArguments(args);

            mFragmentTransaction.remove(fragClientDetails);
            mFragmentTransaction.add(R.id.clientsFragContainer, fragClientAltuofiancoDetails);
            mFragmentTransaction.addToBackStack(null);

            mFragmentTransaction.commit();
        }
    }

    @Override
    public void onClientAddMemoClicked(int id_customer, String company_name)
    {
        if (!fragClientAddMemo.isAdded())
        {
            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

            Bundle args = fragClientAddMemo.getArguments() != null ? fragClientAddMemo.getArguments() : new Bundle();

            args.putInt("id_customer", id_customer);
            args.putString("company_name", company_name);

            fragClientAddMemo.setArguments(args);

            mFragmentTransaction.remove(fragClientDetails);
            mFragmentTransaction.add(R.id.clientsFragContainer, fragClientAddMemo);
            mFragmentTransaction.addToBackStack(null);

            mFragmentTransaction.commit();
        }
    }

    @Override
    public void onOpenClientsSearch(Fragment frag)
    {
        if (!fragClientsSearch.isAdded())
        {
            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

            Bundle args = fragClientsSearch.getArguments() != null ? fragClientsSearch.getArguments() : new Bundle();

            String search_mode = frag.getClass().equals(FragLeadsList.class) ? "leads" : "clients";

            args.putString("search_mode", search_mode);

            fragClientsSearch.setArguments(args);

            if(frag.isAdded())
            {
                mFragmentTransaction.remove(frag);
            }

            mFragmentTransaction.add(R.id.clientsFragContainer, fragClientsSearch);
            mFragmentTransaction.addToBackStack(null);

            mFragmentTransaction.commit();
        }
    }

    @Override
    public void onClientSearchResultsListItemSelected(ClientInfoItem clientInfoItem)
    {
        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

/*        if (fragClientDetails.isAdded())
        {
            mFragmentTransaction.remove(fragClientDetails);
            mFragmentTransaction.commit();
            mFragmentManager.executePendingTransactions();
        }*/

        if (!fragClientDetails.isAdded())
        {
            mSettings.edit().putBoolean("SearchMode", true).apply();

            Bundle args = fragClientDetails.getArguments() != null ? fragClientDetails.getArguments() : new Bundle();

            args.putParcelable("clientInfoItem", clientInfoItem);

            fragClientDetails.setArguments(args);

            mFragmentTransaction.remove(fragClientsSearch);
            mFragmentTransaction.add(R.id.clientsFragContainer, fragClientDetails);
            mFragmentTransaction.addToBackStack(null);

            mFragmentTransaction.commit();
        }
    }

    @Override
    public void onClientsLeadsListItemSelected(LeadInfoItem leadInfoItem)
    {
/*        if (!fragClientsLeadDetails.isAdded())
        {
            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

            Bundle args = fragClientsLeadDetails.getArguments() != null ? fragClientsLeadDetails.getArguments() : new Bundle();

            args.putParcelable("clientsLeadInfoItem", clientsLeadInfoItem);

            fragClientPracticeDetails.setArguments(args);

            mFragmentTransaction.remove(fragClientsLeadsList);
            mFragmentTransaction.add(R.id.clientsFragContainer, fragClientsLeadDetails);
            mFragmentTransaction.addToBackStack(null);

            mFragmentTransaction.commit();
        }*/
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
    public void openLeadsList()
    {
        if (!fragLeadsList.isAdded())
        {
            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

            mFragmentTransaction.remove(fragClientsMap);
            mFragmentTransaction.add(R.id.clientsFragContainer, fragLeadsList);
            mFragmentTransaction.addToBackStack(null);

            mFragmentTransaction.commit();
        }
    }

    @Override
    public void openLeadDetails(LeadInfoItem leadInfoItem, boolean editable)
    {
        if (!fragLeadDetails.isAdded())
        {
            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

            Bundle args = fragLeadDetails.getArguments() != null ? fragLeadDetails.getArguments() : new Bundle();

            args.putParcelable("leadInfoItem", leadInfoItem);
            args.putBoolean("editable", editable);

            fragLeadDetails.setArguments(args);

            mFragmentTransaction.remove(fragLeadsList);
            mFragmentTransaction.add(R.id.clientsFragContainer, fragLeadDetails);
            mFragmentTransaction.addToBackStack(null);

            mFragmentTransaction.commit();
        }
    }

    @Override
    public void onClientDocsListItemSelected(ClientDocsItem clientDocsItem)
    {
        // show document
    }

    @Override
    public void onClientDocsListSelected(int id_customer)
    {
        if (!fragClientDocsList.isAdded())
        {
            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

            Bundle args = fragClientDocsList.getArguments() != null ? fragClientDocsList.getArguments() : new Bundle();

            args.putInt("id_customer", id_customer);

            fragClientDocsList.setArguments(args);

            mFragmentTransaction.remove(fragClientDetails);
            mFragmentTransaction.add(R.id.clientsFragContainer, fragClientDocsList);
            mFragmentTransaction.addToBackStack(null);

            mFragmentTransaction.commit();
        }
    }

    @Override
    public void onClientPracticeListItemSelected(ClientPraticheInfoItem clientPraticheInfoItem)
    {
        if (!fragClientPracticeDetails.isAdded())
        {
            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

            Bundle args = fragClientPracticeDetails.getArguments() != null ? fragClientPracticeDetails.getArguments() : new Bundle();

            args.putParcelable("clientPracticeInfoItem", clientPraticheInfoItem);

            fragClientPracticeDetails.setArguments(args);

            mFragmentTransaction.remove(fragClientPracticesList);
            mFragmentTransaction.add(R.id.clientsFragContainer, fragClientPracticeDetails);
            mFragmentTransaction.addToBackStack(null);

            mFragmentTransaction.commit();
        }
    }
}
