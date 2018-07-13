package developer.alexangan.ru.rewindapp.Fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import developer.alexangan.ru.rewindapp.Adapters.ClientDocsListAdapter;
import developer.alexangan.ru.rewindapp.Interfaces.ClientsCommunicator;
import developer.alexangan.ru.rewindapp.Models.ClientDocsItem;
import developer.alexangan.ru.rewindapp.R;

public class FragClientDocsList extends ListFragment implements View.OnClickListener
{
    private ClientsCommunicator mCommunicator;
    private Activity activity;
    ArrayList<ClientDocsItem> l_clientDocsItems;
    int id_customer;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        mCommunicator = (ClientsCommunicator) getActivity();
        l_clientDocsItems = new ArrayList<>();

        if (getArguments() != null)
        {
            id_customer =  getArguments().getInt("id_customer");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.frag_client_docs_list, container, false);

        LinearLayout llReturn = (LinearLayout) rootView.findViewById(R.id.llReturn);
        llReturn.setOnClickListener(this);

        TextView tvPreviousPageTitle = (TextView) rootView.findViewById(R.id.tvPreviousPageTitle);
        tvPreviousPageTitle.setText(R.string.Client);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        if(l_clientDocsItems != null && l_clientDocsItems.size() != 0)
        {
            showClientDocsList();
        }
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.llReturn)
        {
            mCommunicator.popFragmentsBackStack();
        }
    }

    private void showClientDocsList()
    {
        ClientDocsListAdapter clientDocsListAdapter = new ClientDocsListAdapter(activity, R.layout.client_docs_list_row, l_clientDocsItems);
        setListAdapter(clientDocsListAdapter);

        ListView lv = getListView();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                view.setSelected(true);
                mCommunicator.onClientDocsListItemSelected(l_clientDocsItems.get(position));
            }
        });
    }
}
