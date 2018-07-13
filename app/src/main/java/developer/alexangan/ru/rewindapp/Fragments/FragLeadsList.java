package developer.alexangan.ru.rewindapp.Fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.List;

import developer.alexangan.ru.rewindapp.Adapters.LeadsListAdapter;
import developer.alexangan.ru.rewindapp.Interfaces.ClientsCommunicator;
import developer.alexangan.ru.rewindapp.Models.LeadInfoItem;
import developer.alexangan.ru.rewindapp.R;

public class FragLeadsList extends ListFragment implements View.OnClickListener
{
    private ClientsCommunicator mCommunicator;
    private Activity activity;
    List<LeadInfoItem> l_LeadsInfoItems;
    private Button btnClients;
    private FrameLayout flNewLead;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        mCommunicator = (ClientsCommunicator) getActivity();

/*        if (getArguments() != null)
        {
            l_clientLeadsInfoItems = getArguments().getParcelableArrayList("ListClientPraticheInfoItems");
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.frag_leads_list_layout, container, false);

        LinearLayout llReturn = (LinearLayout) rootView.findViewById(R.id.llReturn);
        llReturn.setOnClickListener(this);

        LinearLayout llOpenLeadsSearch = (LinearLayout) rootView.findViewById(R.id.llOpenLeadsSearch);
        llOpenLeadsSearch.setOnClickListener(this);

        btnClients = (Button) rootView.findViewById(R.id.btnClients);
        btnClients.setOnClickListener(this);

        flNewLead = (FrameLayout) rootView.findViewById(R.id.flNewLead);
        flNewLead.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        if(l_LeadsInfoItems!=null && l_LeadsInfoItems.size() != 0)
        {
            showLeadsList();
        }
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.llReturn)
        {
            mCommunicator.onClose();
            return;
        }

        if (view.getId() == R.id.btnClients)
        {
            mCommunicator.popFragmentsBackStack();
            return;
        }

        if (view.getId() == R.id.flNewLead)
        {
            mCommunicator.openLeadDetails(null, true);
            return;
        }

        if (view.getId() == R.id.llOpenLeadsSearch)
        {
            mCommunicator.onOpenClientsSearch(FragLeadsList.this);
            return;
        }
    }

    private void showLeadsList()
    {
        LeadsListAdapter leadsListAdapter = new LeadsListAdapter(getActivity(), R.layout.leads_list_row, l_LeadsInfoItems);
        setListAdapter(leadsListAdapter);

        ListView lv = getListView();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                view.setSelected(true);
                mCommunicator.onClientsLeadsListItemSelected(l_LeadsInfoItems.get(position));
            }
        });
    }
}
