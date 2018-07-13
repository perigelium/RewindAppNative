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

import java.util.List;

import developer.alexangan.ru.rewindapp.Adapters.ClientPracticesListAdapter;
import developer.alexangan.ru.rewindapp.Interfaces.ClientsCommunicator;
import developer.alexangan.ru.rewindapp.Models.ClientPraticheInfoItem;
import developer.alexangan.ru.rewindapp.R;

public class FragClientPracticesList extends ListFragment implements View.OnClickListener
{
    private ClientsCommunicator mCommunicator;
    private Activity activity;
    List<ClientPraticheInfoItem> l_clientPraticheInfoItems;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        mCommunicator = (ClientsCommunicator) getActivity();

        if (getArguments() != null)
        {
            l_clientPraticheInfoItems = getArguments().getParcelableArrayList("ListClientPraticheInfoItems");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.frag_client_practices_list, container, false);

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

        showClientPracticesList();
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.llReturn)
        {
            mCommunicator.popFragmentsBackStack();
        }
    }

    private void showClientPracticesList()
    {
        ClientPracticesListAdapter clientPracticesListAdapter = new ClientPracticesListAdapter(getActivity(), R.layout.client_practice_row, l_clientPraticheInfoItems);
        setListAdapter(clientPracticesListAdapter);

        ListView lv = getListView();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                view.setSelected(true);
                mCommunicator.onClientPracticeListItemSelected(l_clientPraticheInfoItems.get(position));
            }
        });
    }
}
