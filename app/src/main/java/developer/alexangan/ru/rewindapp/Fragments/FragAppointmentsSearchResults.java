package developer.alexangan.ru.rewindapp.Fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;

import developer.alexangan.ru.rewindapp.Adapters.AppointmentsSearchResultsListAdapter;
import developer.alexangan.ru.rewindapp.Interfaces.AppointmentsCommunicator;
import developer.alexangan.ru.rewindapp.Models.AppointmentsSearchResultsListItem;
import developer.alexangan.ru.rewindapp.R;

public class FragAppointmentsSearchResults extends ListFragment implements View.OnClickListener
{
    private AppointmentsCommunicator mCommunicator;
    private Activity activity;
    ArrayList<AppointmentsSearchResultsListItem> l_AppointmentsSearchResultsListItems;
    private ImageView ivCloseSearch;
    private ImageView ivMagnifier;
    private TextView tvSearchHint;
    private String searchQuery;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        mCommunicator = (AppointmentsCommunicator) getActivity();
        l_AppointmentsSearchResultsListItems = new ArrayList<>();

        if (getArguments() != null)
        {
            l_AppointmentsSearchResultsListItems = getArguments().getParcelableArrayList("appointmentsSearchResultsListItems");
            searchQuery = getArguments().getString("searchQuery", " ");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.frag_appointments_search_results, container, false);

        TextView tvReturn = (TextView) rootView.findViewById(R.id.tvReturn);
        tvReturn.setOnClickListener(this);

        SearchView svAppointments = (SearchView) rootView.findViewById(R.id.svAppointments);
        svAppointments.setInputType(InputType.TYPE_NULL);
        svAppointments.setEnabled(false);

        int searchHintBtnId = svAppointments.getContext().getResources()
                .getIdentifier("android:id/search_mag_icon", null, null);
        int hintTextId = svAppointments.getContext()
                .getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        int closeBtnId = svAppointments.getContext()
                .getResources()
                .getIdentifier("android:id/search_close_btn", null, null);

        ivCloseSearch = (ImageView) svAppointments.findViewById(closeBtnId);
        tvSearchHint = (TextView) svAppointments.findViewById(hintTextId);

        ivMagnifier = (ImageView) svAppointments.findViewById(searchHintBtnId);

/*        TextView tvPreviousPageTitle = (TextView) rootView.findViewById(R.id.tvPreviousPageTitle);
        tvPreviousPageTitle.setText(R.string.Client);*/

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        Typeface myCustomFont = Typeface.createFromAsset(activity.getAssets(), "fonts/SF_UI_Text_Regular.ttf");
        tvSearchHint.setTypeface(myCustomFont);

        tvSearchHint.setTextColor(Color.BLACK);
        tvSearchHint.setHintTextColor(Color.parseColor("#ff808080"));

        ivCloseSearch.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        ivMagnifier.setLayoutParams(new LinearLayout.LayoutParams(0, 0));

        tvSearchHint.setText(searchQuery);

        if(l_AppointmentsSearchResultsListItems != null && l_AppointmentsSearchResultsListItems.size() != 0)
        {
            showSearchResultsList();
        }
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.tvReturn)
        {
            mCommunicator.popFragmentsBackStack();
        }
    }

    private void showSearchResultsList()
    {
        AppointmentsSearchResultsListAdapter appointmentsSearchResultsListAdapter = new AppointmentsSearchResultsListAdapter(activity, R.layout.appointments_search_results_list_row, l_AppointmentsSearchResultsListItems);
        setListAdapter(appointmentsSearchResultsListAdapter);

        ListView lv = getListView();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                view.setSelected(true);
                mCommunicator.onAppointmentsSearchResultsListItemSelected(l_AppointmentsSearchResultsListItems.get(position));
            }
        });
    }
}
